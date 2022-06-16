package servent.handler;

import app.AppConfig;
import app.workers.JobExecutionWorker;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.RequestResultMessage;
import servent.message.TellResultMessage;
import servent.message.util.MessageUtil;

import java.util.ArrayList;

public class RequestResultHandler implements MessageHandler {

    private final Message clientMessage;

    public RequestResultHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.REQUEST_RESULT) {
            RequestResultMessage requestResultMessage = (RequestResultMessage) clientMessage;

            JobExecutionWorker jobWorker = AppConfig.activeJobWorker;

            if (jobWorker == null || !jobWorker.getSubFractal().getJob().equals(requestResultMessage.getSubFractal().getJob()))
                return;

            TellResultMessage tellResultMessage = new TellResultMessage(
                    AppConfig.myServentInfo.getListenerPort(), requestResultMessage.getSenderPort(),
                    AppConfig.myServentInfo.getIpAddress(), requestResultMessage.getSenderIpAddress(),
                    jobWorker.getSubFractal(), new ArrayList<>(jobWorker.getCalculatedPoints()), requestResultMessage.isTotal());

            MessageUtil.sendMessage(tellResultMessage);
        } else {
            AppConfig.timestampedErrorPrint("REQUEST_RESULT handler got something that is not request result message.");
        }
    }
}
