package servent.message;

import app.models.Job;

public class StopJobAckMessage extends BasicMessage {

    private static final long serialVersionUID = 3899217226642127636L;

    private final Job job;

    public StopJobAckMessage(int senderPort, int receiverPort, String senderIpAddress, String receiverIpAddress, Job job) {
        super(MessageType.STOP_JOB_ACK, senderPort, receiverPort, senderIpAddress, receiverIpAddress);

        this.job = job;
    }

    public Job getJob() {
        return job;
    }
}
