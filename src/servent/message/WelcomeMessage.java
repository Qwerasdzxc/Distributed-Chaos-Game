package servent.message;

import app.models.ServentInfo;

import java.util.List;
import java.util.Map;

public class WelcomeMessage extends BasicMessage {

	private static final long serialVersionUID = -8981406250652693908L;

	private List<ServentInfo> activeNodes;

	public WelcomeMessage(int senderPort, int receiverPort, String senderIp, String receiverIp, List<ServentInfo> activeNodes) {
		super(MessageType.WELCOME, senderPort, receiverPort, senderIp, receiverIp);

		this.activeNodes = activeNodes;
	}

	public List<ServentInfo> getActiveNodes() {
		return activeNodes;
	}
}
