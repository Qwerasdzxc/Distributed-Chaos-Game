package servent.handler;

import app.AppConfig;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.WelcomeMessage;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class WelcomeHandler implements MessageHandler {

	private final Message clientMessage;
	
	public WelcomeHandler(Message clientMessage) {
		this.clientMessage = clientMessage;
	}
	
	@Override
	public void run() {
		if (clientMessage.getMessageType() == MessageType.WELCOME) {
			WelcomeMessage welcomeMsg = (WelcomeMessage) clientMessage;

			AppConfig.activeNodes.addAll(welcomeMsg.getActiveNodes());
			AppConfig.timestampedStandardPrint("Current active nodes: " + AppConfig.activeNodes);

			try {
				Socket bsSocket = new Socket(AppConfig.BOOTSTRAP_IP, AppConfig.BOOTSTRAP_PORT);

				PrintWriter bsWriter = new PrintWriter(bsSocket.getOutputStream());
				bsWriter.write("New\n" + AppConfig.myServentInfo.getListenerPort() + "\n");
				bsWriter.write(AppConfig.myServentInfo.getIpAddress() + "\n");

				bsWriter.flush();
				bsSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			AppConfig.timestampedErrorPrint("Welcome handler got a message that is not WELCOME");
		}

	}

}
