package app.models;

import java.io.Serializable;

public class FractalId implements Serializable {

    private static final long serialVersionUID = 5304120042791281555L;

    private final String value;

    private final int depth;

    public FractalId(String value) {
        this.value = value;
        this.depth = value.length();
    }

    public String getValue() {
        return value;
    }

    public int getDepth() {
        return depth;
    }

    @Override
    public String toString() {
        return value;
    }
}
