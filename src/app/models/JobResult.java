package app.models;

import java.io.Serializable;
import java.util.List;

public class JobResult implements Serializable {

    private static final long serialVersionUID = 5309170042791281555L;

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
