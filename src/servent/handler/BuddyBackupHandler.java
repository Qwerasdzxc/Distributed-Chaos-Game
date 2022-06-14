package servent.handler;

import app.AppConfig;
import app.models.ServentInfo;
import servent.message.BuddyBackupMessage;
import servent.message.Message;
import servent.message.MessageType;

public class BuddyBackupHandler implements MessageHandler {

    private final Message clientMessage;

    public BuddyBackupHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.BUDDY_BACKUP) {
            BuddyBackupMessage buddyBackupMessage = (BuddyBackupMessage) clientMessage;

            ServentInfo buddy = new ServentInfo(buddyBackupMessage.getSenderIpAddress(), buddyBackupMessage.getSenderPort());

            AppConfig.buddyJobResultBackups.put(buddy, buddyBackupMessage.getCurrentJobResult());
        } else {
            AppConfig.timestampedErrorPrint("BUDDY_BACKUP handler got something that is not buddy backup message.");
        }
    }
}
