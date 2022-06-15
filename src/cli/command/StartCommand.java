package cli.command;

import app.AppConfig;
import app.models.*;
import app.workers.JobExecutionWorker;
import cli.CLIParser;
import servent.message.ExecuteJobMessage;
import servent.message.util.MessageUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;

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

        int nodeCountForJob = getNodeCountForJob(AppConfig.activeNodes.size(), job.getN());
        AppConfig.timestampedStandardPrint("We will use " + nodeCountForJob + " nodes for this job.");

        ServentInfo me = AppConfig.myServentInfo;
        ArrayList<ServentInfo> assignedNodes = new ArrayList<>();
        assignedNodes.add(me);

        if (nodeCountForJob < job.getN()) {
            ExecuteJobMessage executeJobMessage = new ExecuteJobMessage(me.getListenerPort(), me.getListenerPort(),
                    me.getIpAddress(), me.getIpAddress(), job, job.getPositions(), assignedNodes);

            MessageUtil.sendMessage(executeJobMessage);
            return;
        }

        Map<ServentInfo, List<Point>> assignedNodePoints = new HashMap<>();

        for (int i = 0; i < job.getN(); i++) {
            ServentInfo workerNode = AppConfig.activeNodes.get(i);
            List<Point> fractalPoints = new ArrayList<>();

            Point startPoint = job.getPositions().get(i);
            for (int j = 0; j < job.getN(); j++) {
                if (i == j) {
                    fractalPoints.add(startPoint);
                    continue;
                }

                int x = startPoint.getX();
                int y = startPoint.getY();

                double proportionalX = x + job.getProportion() * (job.getPositions().get(j).getX() - x);
                double proportionalY = y + job.getProportion() * (job.getPositions().get(j).getY() - y);

                fractalPoints.add(new Point((int) proportionalX, (int) proportionalY));
            }

            assignedNodePoints.put(workerNode, fractalPoints);
        }

        for (Map.Entry<ServentInfo, List<Point>> entry : assignedNodePoints.entrySet()) {
            ExecuteJobMessage executeJobMessage = new ExecuteJobMessage(me.getListenerPort(), entry.getKey().getListenerPort(),
                    me.getIpAddress(), entry.getKey().getIpAddress(), job, entry.getValue(), new ArrayList<>(assignedNodePoints.keySet()));

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

    private int getNodeCountForJob(int nodeCount, int n) {
        if (nodeCount < n)
            return 1;

        if (nodeCount == n)
            return nodeCount;

        return getFractalIds(nodeCount, n).size();
    }

    private List<FractalId> getFractalIds(int nodeCount, int n) {
        int depth = 1;
        int boundaryForSplit = n - 1;
        List<FractalId> fractalIds = new ArrayList<>();

        for (int i = 0; i < n; i ++) {
            fractalIds.add(new FractalId(String.valueOf(i)));
        }

        int depthChecker = 0;
        int nodesLeftForSplit = nodeCount - n;

        while (nodesLeftForSplit >= boundaryForSplit) {
            FractalId fractalIdToSplit = getFirstFractalIdToSplit(fractalIds, depth);

            List<FractalId> splittedFractalIds = splitFractalId(fractalIdToSplit, n);

            fractalIds.remove(fractalIdToSplit);
            fractalIds.addAll(splittedFractalIds);
            fractalIds.sort(Comparator.comparing(FractalId::getValue));

            nodesLeftForSplit -= boundaryForSplit;
            depthChecker += 1;
            if (depthChecker == n) {
                depth += 1;
                depthChecker = 0;
            }
        }

        return fractalIds;
    }

    private FractalId getFirstFractalIdToSplit(List<FractalId> fractalIds, int depth) {
        for (FractalId fractalId : fractalIds) {
            if (fractalId.getDepth() == depth)
                return fractalId;
        }

        return fractalIds.get(0);
    }

    private List<FractalId> splitFractalId(FractalId fractalIdToSplit, int n) {
        List<FractalId> fractalIds = new ArrayList<>();

        for (int i = 0; i < n; i ++) {
            fractalIds.add(new FractalId(fractalIdToSplit.getValue() + i));
        }

        return fractalIds;
    }
}
