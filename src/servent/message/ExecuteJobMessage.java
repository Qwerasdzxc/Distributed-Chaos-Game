package servent.message;

import app.models.Job;
import app.models.Point;
import app.models.ServentInfo;
import app.models.SubFractal;

import java.util.List;
import java.util.Map;

public class ExecuteJobMessage extends BasicMessage {

    private static final long serialVersionUID = 3899237286642127636L;

    private final SubFractal subFractal;

    private final Map<ServentInfo, SubFractal> assignedNodeSubFractals;

    public ExecuteJobMessage(int senderPort, int receiverPort, String senderIp, String receiverIp, SubFractal subFractal, Map<ServentInfo, SubFractal> assignedNodeSubFractals) {
        super(MessageType.EXECUTE_JOB, senderPort, receiverPort, senderIp, receiverIp);

        this.subFractal = subFractal;
        this.assignedNodeSubFractals = assignedNodeSubFractals;
    }

    public SubFractal getSubFractal() {
        return subFractal;
    }

    public Map<ServentInfo, SubFractal> getAssignedNodeSubFractals() {
        return assignedNodeSubFractals;
    }
}
