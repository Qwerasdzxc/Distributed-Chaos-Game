package servent.handler;

import app.AppConfig;
import servent.message.BuddyNoMessage;
import servent.message.BuddyYesMessage;
import servent.message.Message;
import servent.message.MessageType;

public class BuddyNoHandler implements MessageHandler {

    private final Message clientMessage;

    public BuddyNoHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.BUDDY_NO) {
            BuddyNoMessage buddyNoMessage = (BuddyNoMessage) clientMessage;

            AppConfig.timestampedErrorPrint("Confirmation of suspicion received. Node " +
                    buddyNoMessage.getNodeChecked().getListenerPort() + " is not giving signs of life.");
        } else {
            AppConfig.timestampedErrorPrint("BUDDY_NO handler got something that is not buddy no message.");
        }
    }
}
