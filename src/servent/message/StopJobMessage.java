package servent.message;

import app.models.Job;

public class StopJobMessage extends BasicMessage {

    private static final long serialVersionUID = 3899237226642127636L;

    private final Job job;

    public StopJobMessage(int senderPort, int receiverPort, String senderIpAddress, String receiverIpAddress, Job job) {
        super(MessageType.STOP_JOB, senderPort, receiverPort, senderIpAddress, receiverIpAddress);

        this.job = job;
    }

    public Job getJob() {
        return job;
    }
}
