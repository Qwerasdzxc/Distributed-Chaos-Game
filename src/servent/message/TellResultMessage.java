package servent.message;

import app.models.Point;
import app.models.SubFractal;

import java.util.List;

public class TellResultMessage extends BasicMessage {

    private final SubFractal subFractal;

    private final List<Point> calculatedPoints;

    private final boolean isTotal;

    public TellResultMessage(int senderPort, int receiverPort, String senderIpAddress, String receiverIpAddress, SubFractal subFractal, List<Point> calculatedPoints, boolean isTotal) {
        super(MessageType.TELL_RESULT, senderPort, receiverPort, senderIpAddress, receiverIpAddress);

        this.subFractal = subFractal;
        this.calculatedPoints = calculatedPoints;
        this.isTotal = isTotal;
    }

    public SubFractal getSubFractal() {
        return subFractal;
    }

    public List<Point> getCalculatedPoints() {
        return calculatedPoints;
    }

    public boolean isTotal() {
        return isTotal;
    }
}
