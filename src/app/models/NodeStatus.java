package app.models;

public class NodeStatus {

    private final String jobName;

    private final int calculatedPointsCount;

    public NodeStatus(String jobName, int calculatedPointsCount) {
        this.jobName = jobName;
        this.calculatedPointsCount = calculatedPointsCount;
    }

    public String getJobName() {
        return jobName;
    }

    public int getCalculatedPointsCount() {
        return calculatedPointsCount;
    }
}
