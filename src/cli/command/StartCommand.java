package cli.command;

import app.AppConfig;
import app.models.*;
import cli.CLIParser;
import servent.message.ExecuteJobMessage;
import servent.message.util.MessageUtil;
import java.util.*;

public class StartCommand implements CLICommand {

    private final CLIParser parser;

    public StartCommand(CLIParser parser) {
        this.parser = parser;
    }

    @Override
    public String commandName() {
        return "start";
    }

    @Override
    public void execute(String args) {
        Job job;

        if (AppConfig.activeJobs.size() >= AppConfig.activeNodes.size()) {
            AppConfig.timestampedStandardPrint("There is not enough free nodes.");
            return;
        }

        if (args != null) {
            job = AppConfig.myServentInfo.getJob(args);

            if (job == null) {
                AppConfig.timestampedStandardPrint("Job doesn't exist.");
                return;
            }
        } else {
            Scanner scanner = parser.getScanner();

            AppConfig.timestampedStandardPrint("Set job name: ");
            String name = scanner.nextLine().trim();

            if (AppConfig.activeJobs.stream().anyMatch(j -> j.getName().equals(name))) {
                AppConfig.timestampedErrorPrint("Job already exists.");
                return;
            }

            AppConfig.timestampedStandardPrint("Set job position count: ");
            int n = scanner.nextInt();
            scanner.nextLine();

            AppConfig.timestampedStandardPrint("Set proportion: ");
            double proportion = scanner.nextDouble();
            scanner.nextLine();

            AppConfig.timestampedStandardPrint("Set image width: ");
            int width = scanner.nextInt();
            scanner.nextLine();

            AppConfig.timestampedStandardPrint("Set image height: ");
            int height = scanner.nextInt();
            scanner.nextLine();

            List<Point> positions = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                AppConfig.timestampedStandardPrint("Set coordinates for position " + i + ", separated by commas: ");
                String[] coordinates = scanner.nextLine().trim().split(",");
                int x = Integer.parseInt(coordinates[0]);
                int y = Integer.parseInt(coordinates[1]);

                positions.add(new Point(x, y));
            }

            job = new Job(name, n, width, height, proportion, positions);
        }

        int nodeCountForJob = getNodeCountForJob(AppConfig.activeNodes.size(), job);
        AppConfig.timestampedStandardPrint("We will use " + nodeCountForJob + " nodes for this job.");

        ServentInfo me = AppConfig.myServentInfo;
        Map<ServentInfo, SubFractal> assignedNodeSubFractals = new HashMap<>();

        if (nodeCountForJob < job.getN()) {
            SubFractal subFractal = new SubFractal(job, new FractalId("0"), job.getPositions());
            assignedNodeSubFractals.put(me, subFractal);
            ExecuteJobMessage executeJobMessage = new ExecuteJobMessage(me.getListenerPort(), me.getListenerPort(),
                    me.getIpAddress(), me.getIpAddress(), subFractal, assignedNodeSubFractals);

            MessageUtil.sendMessage(executeJobMessage);
            return;
        }

        List<SubFractal> subFractals = getFractalIds(nodeCountForJob, job);

        AppConfig.timestampedStandardPrint(subFractals.toString());
        for (int i = 0; i < subFractals.size(); i ++) {
            ServentInfo workerNode = AppConfig.activeNodes.get(i);
            assignedNodeSubFractals.put(workerNode, subFractals.get(i));
        }

        for (Map.Entry<ServentInfo, SubFractal> entry : assignedNodeSubFractals.entrySet()) {
            ExecuteJobMessage executeJobMessage = new ExecuteJobMessage(me.getListenerPort(), entry.getKey().getListenerPort(),
                    me.getIpAddress(), entry.getKey().getIpAddress(), entry.getValue(), assignedNodeSubFractals);

            MessageUtil.sendMessage(executeJobMessage);
        }
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

    private int getNodeCountForJob(int nodeCount, Job job) {
        if (nodeCount < job.getN())
            return 1;

        if (nodeCount == job.getN())
            return nodeCount;

        return getFractalIds(nodeCount, job).size();
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
