package servent.handler;

import app.AppConfig;
import app.models.ServentInfo;
import servent.message.*;
import servent.message.util.MessageUtil;

public class BuddyIsAliveHandler implements MessageHandler {

    private final Message clientMessage;

    public BuddyIsAliveHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.BUDDY_IS_ALIVE) {
            BuddyIsAliveMessage buddyIsAliveMessage = (BuddyIsAliveMessage) clientMessage;

            ServentInfo buddyToCheck = buddyIsAliveMessage.getNodeToCheck();

            Boolean isBuddySuspicious = AppConfig.buddyNodesSuspicionStatus.get(buddyToCheck);
            BasicMessage response;
            if (isBuddySuspicious != null && !isBuddySuspicious) {
                response = new BuddyYesMessage(AppConfig.myServentInfo.getListenerPort(),
                        buddyIsAliveMessage.getSenderPort(), AppConfig.myServentInfo.getIpAddress(), buddyIsAliveMessage.getSenderIpAddress(), buddyToCheck);
            } else {
                response = new BuddyNoMessage(AppConfig.myServentInfo.getListenerPort(),
                        buddyIsAliveMessage.getSenderPort(), AppConfig.myServentInfo.getIpAddress(), buddyIsAliveMessage.getSenderIpAddress(), buddyToCheck);
            }

            MessageUtil.sendMessage(response);
        } else {
            AppConfig.timestampedErrorPrint("BUDDY_IS_ALIVE handler got something that is not buddy is alive message.");
        }
    }
}
