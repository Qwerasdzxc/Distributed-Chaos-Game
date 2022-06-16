package servent.message;

import app.models.SubFractal;

public class TellStatusMessage extends BasicMessage {

    private final SubFractal subFractal;

    private final int calculatedPointsCount;

    private final String fid;

    private final boolean isTotal;

    public TellStatusMessage(int senderPort, int receiverPort, String senderIpAddress, String receiverIpAddress, SubFractal subFractal, int calculatedPointsCount, String fid, boolean isTotal) {
        super(MessageType.TELL_STATUS, senderPort, receiverPort, senderIpAddress, receiverIpAddress);

        this.subFractal = subFractal;
        this.calculatedPointsCount = calculatedPointsCount;
        this.fid = fid;
        this.isTotal = isTotal;
    }

    public SubFractal getSubFractal() {
        return subFractal;
    }

    public int getCalculatedPointsCount() {
        return calculatedPointsCount;
    }

    public String getFid() {
        return fid;
    }

    public boolean isTotal() {
        return isTotal;
    }
}
