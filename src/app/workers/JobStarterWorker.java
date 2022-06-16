package app.workers;

import app.AppConfig;
import app.models.*;
import servent.message.ExecuteJobMessage;
import servent.message.StopJobMessage;
import servent.message.util.MessageUtil;

import java.util.*;

public class JobStarterWorker implements Runnable {

    private final List<Job> jobsToStart;

    public JobStarterWorker(List<Job> jobsToStart) {
        this.jobsToStart = jobsToStart;
    }

    @Override
    public void run() {
        if (jobsToStart.size() > 1) {
            AppConfig.timestampedStandardPrint("Sending stop job messages and waiting for acks...");

            for (Map.Entry<ServentInfo, SubFractal> entry : AppConfig.assignedNodeSubFractals.entrySet()) {
                StopJobMessage stopJobMessage = new StopJobMessage(AppConfig.myServentInfo.getListenerPort(),
                        entry.getKey().getListenerPort(), AppConfig.myServentInfo.getIpAddress(),
                        entry.getKey().getIpAddress(), entry.getValue().getJob());

                MessageUtil.sendMessage(stopJobMessage);
            }

            int expectedAcks = AppConfig.assignedNodeSubFractals.size();
            while (AppConfig.stopJobAcks.size() != expectedAcks) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            AppConfig.stopJobAcks.clear();
            AppConfig.timestampedStandardPrint("All STOP_JOB_ACKS received.");
        }

        AppConfig.timestampedStandardPrint("Starting job execution calculation and sending...");

        ServentInfo me = AppConfig.myServentInfo;
        Map<Job, List<ServentInfo>> nodesForJobs = getNodesForJobsMap(AppConfig.activeNodes, jobsToStart);
        Map<ServentInfo, SubFractal> assignedNodeSubFractals = new HashMap<>();

        for (Map.Entry<Job, List<ServentInfo>> entry : nodesForJobs.entrySet()) {
            Job job = entry.getKey();
            int nodeCountForJob = entry.getValue().size();
            AppConfig.timestampedStandardPrint("We will use " + entry.getValue().size() + " nodes for job: " + job.getName());

            if (nodeCountForJob < job.getN()) {
                ServentInfo soleWorker = entry.getValue().get(0);
                SubFractal subFractal = new SubFractal(job, new FractalId("0"), job.getPositions());
                assignedNodeSubFractals.put(soleWorker, subFractal);

                continue;
            }

            List<SubFractal> subFractals = getFractalIds(nodeCountForJob, job);

            // Remove nodes that won't be used - because they don't have their Fractal ID
            for (int i = entry.getValue().size() - 1; entry.getValue().size() != subFractals.size(); i --) {
                entry.getValue().remove(i);
            }

            for (int i = 0; i < subFractals.size(); i ++) {
                ServentInfo workerNode = entry.getValue().get(i);
                assignedNodeSubFractals.put(workerNode, subFractals.get(i));
            }
        }

        for (Map.Entry<ServentInfo, SubFractal> subEntry : assignedNodeSubFractals.entrySet()) {
            ExecuteJobMessage executeJobMessage = new ExecuteJobMessage(me.getListenerPort(), subEntry.getKey().getListenerPort(),
                    me.getIpAddress(), subEntry.getKey().getIpAddress(), subEntry.getValue(), assignedNodeSubFractals, jobsToStart);

            MessageUtil.sendMessage(executeJobMessage);
        }
    }

    Map<Job, List<ServentInfo>> getNodesForJobsMap(List<ServentInfo> nodes, List<Job> jobs) {
        Map<Job, List<ServentInfo>> nodesForJobs = new HashMap<>();
        int minNodeCount = nodes.size() / jobs.size();
        int leftover = nodes.size() % jobs.size();

        for (int i = 0; i < jobs.size(); i ++) {
            List<ServentInfo> nodesForJob = new ArrayList<>();

            for (int j = i * minNodeCount; j < i * minNodeCount + minNodeCount; j ++) {
                nodesForJob.add(nodes.get(j));
            }

            if (leftover > 0) {
                nodesForJob.add(nodes.get(nodes.size() - leftover));
                leftover --;
            }

            if (nodesForJob.size() < jobs.get(i).getN()) {
                ServentInfo soleWorker = nodesForJob.get(0);
                nodesForJob.clear();
                nodesForJob.add(soleWorker);
            }

            nodesForJobs.put(jobs.get(i), nodesForJob);
        }

        return nodesForJobs;
    }

    private List<Point> getStartingPointsForIndex(int pointIndex, List<Point> points, double proportion) {
        List<Point> childPoints = new ArrayList<>();

        Point startPoint = points.get(pointIndex);
        for (int j = 0; j < points.size(); j++) {
            if (pointIndex == j) {
                childPoints.add(startPoint);
                continue;
            }

            int x = startPoint.getX();
            int y = startPoint.getY();

            double proportionalX = x + proportion * (points.get(j).getX() - x);
            double proportionalY = y + proportion * (points.get(j).getY() - y);

            childPoints.add(new Point((int) proportionalX, (int) proportionalY));
        }

        return childPoints;
    }

    private List<SubFractal> getFractalIds(int nodeCount, Job job) {
        int depth = 1;
        int boundaryForSplit = job.getN() - 1;
        List<SubFractal> fractalIds = new ArrayList<>();

        for (int i = 0; i < job.getN(); i ++) {
            List<Point> points = getStartingPointsForIndex(i, job.getPositions(), job.getProportion());
            fractalIds.add(new SubFractal(job, new FractalId(String.valueOf(i)), points));
        }

        int depthChecker = 0;
        int nodesLeftForSplit = nodeCount - job.getN();

        while (nodesLeftForSplit >= boundaryForSplit) {
            SubFractal fractalIdToSplit = getFirstFractalIdToSplit(fractalIds, depth);

            List<SubFractal> splittedFractalIds = splitFractalId(fractalIdToSplit, job.getN());

            fractalIds.remove(fractalIdToSplit);
            fractalIds.addAll(splittedFractalIds);
            fractalIds.sort(Comparator.comparing(f -> f.getFractalId().getValue()));

            nodesLeftForSplit -= boundaryForSplit;
            depthChecker += 1;
            if (depthChecker == job.getN()) {
                depth += 1;
                depthChecker = 0;
            }
        }

        return fractalIds;
    }

    private SubFractal getFirstFractalIdToSplit(List<SubFractal> fractalIds, int depth) {
        for (SubFractal fractalId : fractalIds) {
            if (fractalId.getFractalId().getDepth() == depth)
                return fractalId;
        }

        return fractalIds.get(0);
    }

    private List<SubFractal> splitFractalId(SubFractal fractalIdToSplit, int n) {
        List<SubFractal> fractalIds = new ArrayList<>();

        for (int i = 0; i < n; i ++) {
            List<Point> points = getStartingPointsForIndex(i, fractalIdToSplit.getSubFractalPositions(), fractalIdToSplit.getJob().getProportion());
            fractalIds.add(new SubFractal(fractalIdToSplit.getJob(), new FractalId(fractalIdToSplit.getFractalId().getValue() + i), points));
        }

        return fractalIds;
    }
}
