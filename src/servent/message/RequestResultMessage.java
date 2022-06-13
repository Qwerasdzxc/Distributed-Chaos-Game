package servent.message;

public class RequestResultMessage extends BasicMessage {

    private final String jobName;

    public RequestResultMessage(int senderPort, int receiverPort, String senderIpAddress, String receiverIpAddress, String jobName) {
        super(MessageType.REQUEST_RESULT, senderPort, receiverPort, senderIpAddress, receiverIpAddress);

        this.jobName = jobName;
    }

    public String getJobName() {
        return jobName;
    }
}
