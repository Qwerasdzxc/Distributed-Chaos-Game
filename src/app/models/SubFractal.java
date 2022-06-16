package app.models;

import java.io.Serializable;
import java.util.List;

public class SubFractal implements Serializable {

    private static final long serialVersionUID = 5304120042791282555L;

    private final Job job;
    private final FractalId fractalId;

    private final List<Point> subFractalPositions;

    public SubFractal(Job job, FractalId fractalId, List<Point> subFractalPositions) {
        this.job = job;
        this.fractalId = fractalId;
        this.subFractalPositions = subFractalPositions;
    }

    public Job getJob() {
        return job;
    }

    public FractalId getFractalId() {
        return fractalId;
    }

    public List<Point> getSubFractalPositions() {
        return subFractalPositions;
    }

    @Override
    public String toString() {
        return "SubFractal{" +
                "job=" + job +
                ", fractalId=" + fractalId +
                '}';
    }
}
