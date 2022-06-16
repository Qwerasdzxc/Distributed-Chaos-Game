package app.models;

import java.io.Serializable;
import java.util.List;

public class JobResult implements Serializable {

    private static final long serialVersionUID = 5309170042791281555L;

    private final SubFractal subFractal;

    private final List<Point> calculatedPoints;

    public JobResult(SubFractal subFractal, List<Point> calculatedPoints) {
        this.subFractal = subFractal;
        this.calculatedPoints = calculatedPoints;
    }

    public SubFractal getSubFractal() {
        return subFractal;
    }

    public List<Point> getCalculatedPoints() {
        return calculatedPoints;
    }
}
