package servent.message;

public class BuddyPongMessage extends BasicMessage {

    public BuddyPongMessage(int senderPort, int receiverPort, String senderIpAddress, String receiverIpAddress) {
        super(MessageType.BUDDY_PONG, senderPort, receiverPort, senderIpAddress, receiverIpAddress);
    }
}
