package app.models;

public class NodeStatus {

    private final SubFractal subFractal;

    private final int calculatedPointsCount;

    public NodeStatus(SubFractal subFractal, int calculatedPointsCount) {
        this.subFractal = subFractal;
        this.calculatedPointsCount = calculatedPointsCount;
    }

    public SubFractal getSubFractal() {
        return subFractal;
    }

    public int getCalculatedPointsCount() {
        return calculatedPointsCount;
    }

    @Override
    public String toString() {
        return "NodeStatus{" +
                "subFractal=" + subFractal +
                ", calculatedPointsCount=" + calculatedPointsCount +
                '}';
    }
}
