package app.workers;

import app.AppConfig;
import app.Cancellable;
import app.models.Job;
import app.models.Point;
import app.models.SubFractal;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JobExecutionWorker implements Runnable, Cancellable {

    private final SubFractal subFractal;

    private boolean working;

    private final List<Point> assignedStartingPoints;
    private final List<Point> calculatedPoints;

    public JobExecutionWorker(SubFractal subFractal, List<Point> assignedStartingPoints) {
        this.subFractal = subFractal;
        this.assignedStartingPoints = assignedStartingPoints;
        this.working = true;
        this.calculatedPoints = new ArrayList<>();
    }

    @Override
    public void run() {
        AppConfig.timestampedStandardPrint("Executing job: " + subFractal.getJob().getName() + " with FID: " + subFractal.getFractalId().getValue());

        while (working) {
            Point newPoint = calculatePoint();
            calculatedPoints.add(newPoint);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() {
        working = false;
        AppConfig.timestampedStandardPrint("Stopping job: " + subFractal.getJob().getName());
    }

    private Point calculatePoint() {
        Random random = new Random();
        if (calculatedPoints.isEmpty()) {
            int x = random.nextInt(subFractal.getJob().getWidth() + 1);
            int y = random.nextInt(subFractal.getJob().getHeight() + 1);

            return new Point(x, y);
        }

        Point lastPoint = calculatedPoints.get(calculatedPoints.size() - 1);

        Point referencePoint = assignedStartingPoints.get(random.nextInt(assignedStartingPoints.size()));
        int x = referencePoint.getX();
        int y = referencePoint.getY();

        double proportionalX = x + subFractal.getJob().getProportion() * (lastPoint.getX() - x);
        double proportionalY = y + subFractal.getJob().getProportion() * (lastPoint.getY() - y);

        return new Point((int) proportionalX, (int) proportionalY);
    }

    public List<Point> getCalculatedPoints() {
        return calculatedPoints;
    }

    public SubFractal getSubFractal() {
        return subFractal;
    }

    @Override
    public String toString() {
        return "JobExecutionWorker{" +
                "subFractal=" + subFractal +
                ", working=" + working +
                '}';
    }
}
