package servent.handler;

import app.AppConfig;
import app.models.*;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.TellStatusMessage;

import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

public class TellStatusHandler implements MessageHandler {

    private final Message clientMessage;

    public TellStatusHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.TELL_STATUS) {
            TellStatusMessage tellStatusMessage = (TellStatusMessage) clientMessage;

            Optional<Job> job = Optional.empty();

            if (!tellStatusMessage.isTotal()) {
                job = AppConfig.activeJobs.stream().filter(job1 -> job1.equals(tellStatusMessage.getSubFractal().getJob())).findFirst();
                if (job.isEmpty()) {
                    AppConfig.timestampedStandardPrint("Got TELL_STATUS for job I don't have in system.");
                    return;
                }
            }

            ServentInfo teller = new ServentInfo(tellStatusMessage.getSenderIpAddress(), tellStatusMessage.getSenderPort());

            AppConfig.nodeStatuses.put(teller,
                    new NodeStatus(tellStatusMessage.getSubFractal(), tellStatusMessage.getCalculatedPointsCount()));

            if (tellStatusMessage.isTotal()) {
                if (AppConfig.nodeStatuses.size() == AppConfig.assignedNodeSubFractals.size()) {
                    printStatus(Optional.empty(), null);
                }
            } else {
                if (tellStatusMessage.getFid() == null && AppConfig.nodeStatuses.size() == AppConfig.assignedNodeSubFractals.values().stream().filter(subFractal -> subFractal.getJob().equals(tellStatusMessage.getSubFractal().getJob())).count()) {
                    printStatus(job, null);
                } else if (AppConfig.nodeStatuses.size() == AppConfig.assignedNodeSubFractals.values().stream().filter(subFractal -> subFractal.getJob().equals(tellStatusMessage.getSubFractal().getJob()) && subFractal.getFractalId().getValue().equals(tellStatusMessage.getFid())).count()) {
                    printStatus(job, tellStatusMessage.getFid());
                }
            }
        } else {
            AppConfig.timestampedErrorPrint("TELL_STATUS handler got something that is not tell status message.");
        }
    }

    private void printStatus(Optional<Job> job, String fid) {
        StringBuilder status = new StringBuilder();

        status.append("Status report ");
        if (job.isPresent()) {
            status.append("for job: ");
            status.append(job.get().getName());
            status.append(" ");
        }
        if (fid != null) {
            status.append("for fid: ");
            status.append(fid);
            status.append(" ");
        }
        status.append("Node worker count: ");
        status.append(AppConfig.nodeStatuses.size());
        status.append(".\n");
        for (Map.Entry<ServentInfo, NodeStatus> entry : AppConfig.nodeStatuses.entrySet()) {
            status.append("Node ");
            status.append(entry.getValue().getSubFractal().getFractalId().getValue());
            status.append(": ");
            status.append(entry.getValue().getCalculatedPointsCount());
            status.append("\n");
        }
        status.setLength(status.length() - 1);

        AppConfig.nodeStatuses.clear();

        AppConfig.timestampedStandardPrint(status.toString());
    }
}
