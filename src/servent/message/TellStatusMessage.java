package servent.message;

public class TellStatusMessage extends BasicMessage {

    private final String jobName;

    private final int calculatedPointsCount;

    public TellStatusMessage(int senderPort, int receiverPort, String senderIpAddress, String receiverIpAddress, String jobName, int calculatedPointsCount) {
        super(MessageType.TELL_STATUS, senderPort, receiverPort, senderIpAddress, receiverIpAddress);

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
