package servent.handler;

import app.AppConfig;
import app.models.ServentInfo;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.QuitMessage;

public class QuitHandler implements MessageHandler {

    private final Message clientMessage;

    public QuitHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.QUIT) {
            QuitMessage quitMessage = (QuitMessage) clientMessage;
            ServentInfo quitter = new ServentInfo(quitMessage.getSenderIpAddress(), quitMessage.getSenderPort());

            // TODO: Handle received job data

            AppConfig.activeNodes.removeIf(serventInfo -> serventInfo.getUuid().equals(quitter.getUuid()));
            AppConfig.timestampedStandardPrint("Current active nodes: " + AppConfig.activeNodes);
        } else {
            AppConfig.timestampedErrorPrint("QUIT handler got something that is not quit message.");
        }
    }
}
