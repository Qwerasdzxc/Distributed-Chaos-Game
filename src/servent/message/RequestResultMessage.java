package servent.message;

import app.models.SubFractal;

public class RequestResultMessage extends BasicMessage {

    private final SubFractal subFractal;

    private final boolean isTotal;

    public RequestResultMessage(int senderPort, int receiverPort, String senderIpAddress, String receiverIpAddress, SubFractal subFractal, boolean isTotal) {
        super(MessageType.REQUEST_RESULT, senderPort, receiverPort, senderIpAddress, receiverIpAddress);

        this.subFractal = subFractal;
        this.isTotal = isTotal;
    }

    public SubFractal getSubFractal() {
        return subFractal;
    }

    public boolean isTotal() {
        return isTotal;
    }
}
