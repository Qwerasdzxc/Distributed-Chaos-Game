package servent.message;

public class StopJobMessage extends BasicMessage {

    private static final long serialVersionUID = 3899237226642127636L;

    private final String jobName;

    public StopJobMessage(int senderPort, int receiverPort, String senderIpAddress, String receiverIpAddress, String jobName) {
        super(MessageType.STOP_JOB, senderPort, receiverPort, senderIpAddress, receiverIpAddress);

        this.jobName = jobName;
    }

    public String getJobName() {
        return jobName;
    }
}
