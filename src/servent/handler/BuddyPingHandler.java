package servent.handler;

import app.AppConfig;
import servent.message.*;
import servent.message.util.MessageUtil;

public class BuddyPingHandler implements MessageHandler {

    private final Message clientMessage;

    public BuddyPingHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.BUDDY_PING) {
            BuddyPingMessage buddyPingMessage = (BuddyPingMessage) clientMessage;

            BuddyPongMessage buddyPongMessage = new BuddyPongMessage(AppConfig.myServentInfo.getListenerPort(),
                    buddyPingMessage.getSenderPort(), AppConfig.myServentInfo.getIpAddress(), buddyPingMessage.getSenderIpAddress());

            MessageUtil.sendMessage(buddyPongMessage);
        } else {
            AppConfig.timestampedErrorPrint("BUDDY_PING handler got something that is not buddy ping message.");
        }
    }
}
