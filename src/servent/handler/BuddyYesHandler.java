package servent.handler;

import app.AppConfig;
import app.models.ServentInfo;
import servent.message.BuddyPongMessage;
import servent.message.BuddyYesMessage;
import servent.message.Message;
import servent.message.MessageType;

import java.sql.Timestamp;

public class BuddyYesHandler implements MessageHandler {

    private final Message clientMessage;

    public BuddyYesHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.BUDDY_YES) {
            BuddyYesMessage buddyYesMessage = (BuddyYesMessage) clientMessage;
            ServentInfo buddy = new ServentInfo(buddyYesMessage.getSenderIpAddress(), buddyYesMessage.getSenderPort());

            AppConfig.buddyNodesSuspicionStatus.put(buddy, false);
        } else {
            AppConfig.timestampedErrorPrint("BUDDY_YES handler got something that is not buddy yes message.");
        }
    }
}
