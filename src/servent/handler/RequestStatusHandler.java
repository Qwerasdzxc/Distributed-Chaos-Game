package servent.handler;

import app.AppConfig;
import app.workers.JobExecutionWorker;
import servent.message.*;
import servent.message.util.MessageUtil;

public class RequestStatusHandler implements MessageHandler {

    private final Message clientMessage;

    public RequestStatusHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.REQUEST_STATUS) {
            RequestStatusMessage requestStatusMessage = (RequestStatusMessage) clientMessage;

            JobExecutionWorker jobWorker = AppConfig.activeJobWorker;

            AppConfig.timestampedStandardPrint(requestStatusMessage.toString());

            if (jobWorker == null)
                return;

            if (!requestStatusMessage.isTotal() && !jobWorker.getSubFractal().getJob().equals(requestStatusMessage.getSubFractal().getJob()))
                return;

            if (!requestStatusMessage.isTotal() && requestStatusMessage.getFid() != null && !jobWorker.getSubFractal().getFractalId().getValue().equals(requestStatusMessage.getFid()))
                return;

            TellStatusMessage tellStatusMessage = new TellStatusMessage(
                    AppConfig.myServentInfo.getListenerPort(), requestStatusMessage.getSenderPort(),
                    AppConfig.myServentInfo.getIpAddress(), requestStatusMessage.getSenderIpAddress(),
                    jobWorker.getSubFractal(), jobWorker.getCalculatedPoints().size(), requestStatusMessage.getFid(), requestStatusMessage.isTotal());

            MessageUtil.sendMessage(tellStatusMessage);
        } else {
            AppConfig.timestampedErrorPrint("REQUEST_STATUS handler got something that is not request status message.");
        }
    }
}
