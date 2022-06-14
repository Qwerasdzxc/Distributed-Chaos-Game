package servent.message;

import app.models.Job;
import app.models.ServentInfo;

import java.util.List;
import java.util.Map;

public class WelcomeMessage extends BasicMessage {

	private static final long serialVersionUID = -8981406250652693908L;

	private final List<ServentInfo> activeNodes;
	private final List<Job> activeJobs;
	private final Map<ServentInfo, Job> assignedJobs;

	public WelcomeMessage(int senderPort, int receiverPort, String senderIp, String receiverIp, List<ServentInfo> activeNodes, List<Job> activeJobs, Map<ServentInfo, Job> assignedJobs) {
		super(MessageType.WELCOME, senderPort, receiverPort, senderIp, receiverIp);

		this.activeNodes = activeNodes;
		this.activeJobs = activeJobs;
		this.assignedJobs = assignedJobs;
	}

	public List<ServentInfo> getActiveNodes() {
		return activeNodes;
	}

	public List<Job> getActiveJobs() {
		return activeJobs;
	}

	public Map<ServentInfo, Job> getAssignedJobs() {
		return assignedJobs;
	}
}
