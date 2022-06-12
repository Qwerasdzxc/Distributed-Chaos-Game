package servent.message;

import app.models.ServentInfo;

public class NodeJoinedMessage extends BasicMessage {

    private static final long serialVersionUID = 3819837286642127636L;

    private final ServentInfo joinedServent;

    public NodeJoinedMessage(int senderPort, int receiverPort, String senderIp, String receiverIp, ServentInfo joinedServent) {
        super(MessageType.NODE_JOINED, senderPort, receiverPort, senderIp, receiverIp);

        this.joinedServent = joinedServent;
    }

    public ServentInfo getJoinedServent() {
        return joinedServent;
    }
}
