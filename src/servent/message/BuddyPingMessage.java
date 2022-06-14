package servent.message;

public class BuddyPingMessage extends BasicMessage {

    public BuddyPingMessage(int senderPort, int receiverPort, String senderIpAddress, String receiverIpAddress) {
        super(MessageType.BUDDY_PING, senderPort, receiverPort, senderIpAddress, receiverIpAddress);
    }
}
