package servent.message;

import app.models.Point;

import java.util.List;

public class QuitMessage extends BasicMessage {

    private final String jobName;
    private final List<Point> calculatedPoints;

    public QuitMessage(int senderPort, int receiverPort, String senderIpAddress, String receiverIpAddress, String jobName, List<Point> calculatedPoints) {
        super(MessageType.QUIT, senderPort, receiverPort, senderIpAddress, receiverIpAddress);

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
