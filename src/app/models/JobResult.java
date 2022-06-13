package app.models;

import java.util.List;

public class JobResult {

    private final String jobName;

    private final List<Point> calculatedPoints;

    public JobResult(String jobName, List<Point> calculatedPoints) {
        this.jobName = jobName;
        this.calculatedPoints = calculatedPoints;
    }

    public String getJobName() {
        return jobName;
    }

    public List<Point> getCalculatedPoints() {
        return calculatedPoints;
    }
}
