package servent.message;

import app.models.SubFractal;

public class RequestStatusMessage extends BasicMessage {

    private final SubFractal subFractal;

    private final String fid;

    private final boolean isTotal;

    public RequestStatusMessage(int senderPort, int receiverPort, String senderIpAddress, String receiverIpAddress, SubFractal subFractal, String fid, boolean isTotal) {
        super(MessageType.REQUEST_STATUS, senderPort, receiverPort, senderIpAddress, receiverIpAddress);

        this.subFractal = subFractal;
        this.fid = fid;
        this.isTotal = isTotal;
    }

    public SubFractal getSubFractal() {
        return subFractal;
    }

    public String getFid() {
        return fid;
    }

    public boolean isTotal() {
        return isTotal;
    }
}
