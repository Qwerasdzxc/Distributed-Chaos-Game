package servent.message;

import app.models.ServentInfo;

public class BuddyYesMessage extends BasicMessage {

    private final ServentInfo nodeChecked;

    public BuddyYesMessage(int senderPort, int receiverPort, String senderIpAddress, String receiverIpAddress, ServentInfo nodeChecked) {
        super(MessageType.BUDDY_YES, senderPort, receiverPort, senderIpAddress, receiverIpAddress);

        this.nodeChecked = nodeChecked;
    }

    public ServentInfo getNodeChecked() {
        return nodeChecked;
    }
}
