package servent.message;

import app.models.JobResult;

public class BuddyBackupMessage extends BasicMessage {

    private final JobResult currentJobResult;

    public BuddyBackupMessage(int senderPort, int receiverPort, String senderIpAddress, String receiverIpAddress, JobResult currentJobResult) {
        super(MessageType.BUDDY_BACKUP, senderPort, receiverPort, senderIpAddress, receiverIpAddress);

        this.currentJobResult = currentJobResult;
    }

    public JobResult getCurrentJobResult() {
        return currentJobResult;
    }
}
