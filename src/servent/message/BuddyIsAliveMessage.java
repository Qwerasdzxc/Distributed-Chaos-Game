package servent.message;

import app.models.ServentInfo;

public class BuddyIsAliveMessage extends BasicMessage {

    private final ServentInfo nodeToCheck;

    public BuddyIsAliveMessage(int senderPort, int receiverPort, String senderIpAddress, String receiverIpAddress, ServentInfo nodeToCheck) {
        super(MessageType.BUDDY_IS_ALIVE, senderPort, receiverPort, senderIpAddress, receiverIpAddress);

        this.nodeToCheck = nodeToCheck;
    }

    public ServentInfo getNodeToCheck() {
        return nodeToCheck;
    }
}
