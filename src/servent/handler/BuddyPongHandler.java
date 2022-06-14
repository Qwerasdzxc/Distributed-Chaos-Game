package servent.handler;

import app.AppConfig;
import app.models.ServentInfo;
import servent.message.BuddyPingMessage;
import servent.message.BuddyPongMessage;
import servent.message.Message;
import servent.message.MessageType;

import java.sql.Timestamp;

public class BuddyPongHandler implements MessageHandler {

    private final Message clientMessage;

    public BuddyPongHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.BUDDY_PONG) {
            BuddyPongMessage buddyPongMessage = (BuddyPongMessage) clientMessage;

            ServentInfo buddy = new ServentInfo(buddyPongMessage.getSenderIpAddress(), buddyPongMessage.getSenderPort());

            AppConfig.buddyNodesLastPongTime.put(buddy, new Timestamp(System.currentTimeMillis()));
            AppConfig.buddyNodesSuspicionStatus.put(buddy, false);
        } else {
            AppConfig.timestampedErrorPrint("BUDDY_PONG handler got something that is not buddy pong message.");
        }
    }
}
