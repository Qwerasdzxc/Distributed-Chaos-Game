package servent.handler;

import app.AppConfig;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.WelcomeMessage;

public class WelcomeHandler implements MessageHandler {

	private Message clientMessage;
	
	public WelcomeHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}
	
	@Override
	public void run() {
		if (clientMessage.getMessageType() == MessageType.WELCOME) {
			WelcomeMessage welcomeMsg = (WelcomeMessage) clientMessage;

//			UpdateMessage um = new UpdateMessage(AppConfig.myServentInfo.getListenerPort(), AppConfig.chordState.getNextNodePort(), "");
//			MessageUtil.sendMessage(um);
			
		} else {
			AppConfig.timestampedErrorPrint("Welcome handler got a message that is not WELCOME");
		}

	}

}
