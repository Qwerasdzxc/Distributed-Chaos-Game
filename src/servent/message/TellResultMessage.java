package servent.message;

import app.models.Point;

import java.util.List;

public class TellResultMessage extends BasicMessage {

    private final String jobName;

    private final List<Point> calculatedPoints;

    public TellResultMessage(int senderPort, int receiverPort, String senderIpAddress, String receiverIpAddress, String jobName, List<Point> calculatedPoints) {
        super(MessageType.TELL_RESULT, senderPort, receiverPort, senderIpAddress, receiverIpAddress);

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
