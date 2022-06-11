package app.models;

import java.util.List;

public class Job {

    private final String name;

    private final int n;
    private final int width;
    private final int height;

    private final double proportion;

    private final List<Point> positions;

    public Job(String name, int n, int width, int height, double proportion, List<Point> positions) {
        this.name = name;
        this.n = n;
        this.width = width;
        this.height = height;
        this.proportion = proportion;
        this.positions = positions;
    }

    public String getName() {
        return name;
    }

    public int getN() {
        return n;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public double getProportion() {
        return proportion;
    }

    public List<Point> getPositions() {
        return positions;
    }

    @Override
    public String toString() {
        return "Job{" +
                "name='" + name + '\'' +
                ", n=" + n +
                ", width=" + width +
                ", height=" + height +
                ", proportion=" + proportion +
                ", positions=" + positions +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Job job = (Job) o;

        return name.equals(job.name);
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
