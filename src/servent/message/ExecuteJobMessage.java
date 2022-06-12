package servent.message;

import app.models.Job;
import app.models.Point;

import java.util.List;

public class ExecuteJobMessage extends BasicMessage {

    private static final long serialVersionUID = 3899237286642127636L;

    private final Job job;

    private final List<Point> assignedStartingPoints;

    public ExecuteJobMessage(int senderPort, int receiverPort, String senderIp, String receiverIp, Job job, List<Point> assignedStartingPoints) {
        super(MessageType.EXECUTE_JOB, senderPort, receiverPort, senderIp, receiverIp);

        this.job = job;
        this.assignedStartingPoints = assignedStartingPoints;
    }

    public Job getJob() {
        return job;
    }

    public List<Point> getAssignedStartingPoints() {
        return assignedStartingPoints;
    }
}
