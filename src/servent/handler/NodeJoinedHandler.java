package servent.handler;

import app.AppConfig;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.NodeJoinedMessage;

public class NodeJoinedHandler implements MessageHandler {

    private final Message clientMessage;

    public NodeJoinedHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.NODE_JOINED) {
            NodeJoinedMessage nodeJoinedMessage = (NodeJoinedMessage) clientMessage;
            AppConfig.activeNodes.add(nodeJoinedMessage.getJoinedServent());

            AppConfig.timestampedStandardPrint("Current active nodes: " + AppConfig.activeNodes);
        } else {
            AppConfig.timestampedErrorPrint("NODE_JOINED handler got something that is not node joined message.");
        }
    }
}
