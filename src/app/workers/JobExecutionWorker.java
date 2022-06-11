package app.workers;

import app.AppConfig;
import app.Cancellable;
import app.models.Job;
import app.models.Point;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class JobExecutionWorker implements Runnable, Cancellable {

    private final Job job;

    private boolean working;

    private final List<Point> calculatedPoints;

    public JobExecutionWorker(Job job) {
        this.job = job;
        this.working = true;
        this.calculatedPoints = new ArrayList<>();
    }

    @Override
    public void run() {
        AppConfig.timestampedStandardPrint("Executing job: " + job.getName());

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
        AppConfig.timestampedStandardPrint("Stopping job: " + job.getName());
    }

    private Point calculatePoint() {
        Random random = new Random();
        if (calculatedPoints.isEmpty()) {
            int x = random.nextInt(job.getWidth() + 1);
            int y = random.nextInt(job.getHeight() + 1);

            return new Point(x, y);
        }

        Point lastPoint = calculatedPoints.get(calculatedPoints.size() - 1);

        Point referencePoint = job.getPositions().get(random.nextInt(job.getPositions().size()));
        int x = referencePoint.getX();
        int y = referencePoint.getY();

        double proportionalX = x + job.getProportion() * (lastPoint.getX() - x);
        double proportionalY = y + job.getProportion() * (lastPoint.getY() - y);

        return new Point((int) proportionalX, (int) proportionalY);
    }

    public List<Point> getCalculatedPoints() {
        return calculatedPoints;
    }
}
