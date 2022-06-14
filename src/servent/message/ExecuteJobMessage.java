package servent.message;

import app.models.Job;
import app.models.Point;
import app.models.ServentInfo;

import java.util.List;

public class ExecuteJobMessage extends BasicMessage {

    private static final long serialVersionUID = 3899237286642127636L;

    private final Job job;

    private final List<Point> assignedStartingPoints;

    private final List<ServentInfo> assignedNodes;

    public ExecuteJobMessage(int senderPort, int receiverPort, String senderIp, String receiverIp, Job job, List<Point> assignedStartingPoints, List<ServentInfo> assignedNodes) {
        super(MessageType.EXECUTE_JOB, senderPort, receiverPort, senderIp, receiverIp);

        this.job = job;
        this.assignedStartingPoints = assignedStartingPoints;
        this.assignedNodes = assignedNodes;
    }

    public Job getJob() {
        return job;
    }

    public List<Point> getAssignedStartingPoints() {
        return assignedStartingPoints;
    }

    public List<ServentInfo> getAssignedNodes() {
        return assignedNodes;
    }
}
