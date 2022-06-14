package servent.message;

import app.models.ServentInfo;

public class BuddyNoMessage extends BasicMessage {

    private final ServentInfo nodeChecked;

    public BuddyNoMessage(int senderPort, int receiverPort, String senderIpAddress, String receiverIpAddress, ServentInfo nodeChecked) {
        super(MessageType.BUDDY_NO, senderPort, receiverPort, senderIpAddress, receiverIpAddress);

        this.nodeChecked = nodeChecked;
    }

    public ServentInfo getNodeChecked() {
        return nodeChecked;
    }
}
