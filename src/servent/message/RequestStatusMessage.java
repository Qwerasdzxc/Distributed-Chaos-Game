package servent.message;

public class RequestStatusMessage extends BasicMessage {

    private final String jobName;

    public RequestStatusMessage(int senderPort, int receiverPort, String senderIpAddress, String receiverIpAddress, String jobName) {
        super(MessageType.REQUEST_STATUS, senderPort, receiverPort, senderIpAddress, receiverIpAddress);

        this.jobName = jobName;
    }

    public String getJobName() {
        return jobName;
    }
}
