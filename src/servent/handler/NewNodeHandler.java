package servent.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import app.AppConfig;
import app.models.ServentInfo;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.NodeJoinedMessage;
import servent.message.WelcomeMessage;
import servent.message.util.MessageUtil;

public class NewNodeHandler implements MessageHandler {

    private final Message clientMessage;

    public NewNodeHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.NEW_NODE) {
            int newNodePort = clientMessage.getSenderPort();
            String newNodeIp = clientMessage.getSenderIpAddress();
            ServentInfo newNodeInfo = new ServentInfo(newNodeIp, newNodePort);

            // New node is joining the system, let's notify all other nodes:
            for (final ServentInfo servent : AppConfig.activeNodes) {
				if (Objects.equals(servent.getUuid(), AppConfig.myServentInfo.getUuid()))
					continue;

                NodeJoinedMessage nodeJoinedMessage = new NodeJoinedMessage(AppConfig.myServentInfo.getListenerPort(),
                        servent.getListenerPort(), AppConfig.myServentInfo.getIpAddress(), servent.getIpAddress(), newNodeInfo);

                MessageUtil.sendMessage(nodeJoinedMessage);
            }

            AppConfig.activeNodes.add(newNodeInfo);
            AppConfig.timestampedStandardPrint("Current active nodes: " + AppConfig.activeNodes);

            WelcomeMessage wm = new WelcomeMessage(AppConfig.myServentInfo.getListenerPort(), newNodePort,
                    AppConfig.myServentInfo.getIpAddress(), newNodeIp, AppConfig.activeNodes,
                    new ArrayList<>(AppConfig.activeJobs), AppConfig.assignedJobs);

            MessageUtil.sendMessage(wm);
        } else {
            AppConfig.timestampedErrorPrint("NEW_NODE handler got something that is not new node message.");
        }

    }

}
