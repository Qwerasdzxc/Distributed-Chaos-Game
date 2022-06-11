package servent.handler;

import java.util.HashMap;

import app.AppConfig;
import app.models.ServentInfo;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.WelcomeMessage;
import servent.message.util.MessageUtil;

public class NewNodeHandler implements MessageHandler {

	private Message clientMessage;
	
	public NewNodeHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}
	
	@Override
	public void run() {
		if (clientMessage.getMessageType() == MessageType.NEW_NODE) {
			int newNodePort = clientMessage.getSenderPort();
			ServentInfo newNodeInfo = new ServentInfo("localhost", newNodePort);

			WelcomeMessage wm = new WelcomeMessage(AppConfig.myServentInfo.getListenerPort(), newNodePort, new HashMap<>());
			MessageUtil.sendMessage(wm);
			
		} else {
			AppConfig.timestampedErrorPrint("NEW_NODE handler got something that is not new node message.");
		}

	}

}
