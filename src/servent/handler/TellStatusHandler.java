package servent.handler;

import app.AppConfig;
import app.models.*;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.TellStatusMessage;

import java.util.Map;

public class TellStatusHandler implements MessageHandler {

    private final Message clientMessage;

    public TellStatusHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.TELL_STATUS) {
            TellStatusMessage tellStatusMessage = (TellStatusMessage) clientMessage;

            // TODO: Handle the case when we are not the owner of the job (result command called on node without declared jobs)
            Job job = AppConfig.myServentInfo.getJob(tellStatusMessage.getJobName());

            ServentInfo teller = new ServentInfo(tellStatusMessage.getSenderIpAddress(), tellStatusMessage.getSenderPort());

            AppConfig.nodeStatuses.put(teller,
                    new NodeStatus(tellStatusMessage.getJobName(), tellStatusMessage.getCalculatedPointsCount()));

            // TODO: Handle nested fractals
            if (job.getN() == AppConfig.nodeStatuses.size()) {
                StringBuilder status = new StringBuilder();

                status.append("Status report for Job: ");
                status.append(job.getName());
                status.append(". Node worker count: ");
                status.append(AppConfig.nodeStatuses.size());
                status.append(".\n");
                for (Map.Entry<ServentInfo, NodeStatus> entry : AppConfig.nodeStatuses.entrySet()) {
                    status.append("Node ");
                    status.append(entry.getKey().getListenerPort());
                    status.append(": ");
                    status.append(entry.getValue().getCalculatedPointsCount());
                    status.append("\n");
                }
                status.setLength(status.length() - 1);

                AppConfig.nodeStatuses.clear();

                AppConfig.timestampedStandardPrint(status.toString());
            }
        } else {
            AppConfig.timestampedErrorPrint("TELL_STATUS handler got something that is not tell status message.");
        }
    }
}
