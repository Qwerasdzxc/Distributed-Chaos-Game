package servent.message;

import app.models.Point;
import app.models.SubFractal;

import java.util.List;

public class QuitMessage extends BasicMessage {

    private final SubFractal subFractal;
    private final List<Point> calculatedPoints;

    public QuitMessage(int senderPort, int receiverPort, String senderIpAddress, String receiverIpAddress, SubFractal subFractal, List<Point> calculatedPoints) {
        super(MessageType.QUIT, senderPort, receiverPort, senderIpAddress, receiverIpAddress);

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
