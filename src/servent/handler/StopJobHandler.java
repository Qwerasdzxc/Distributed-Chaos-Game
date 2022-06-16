package servent.handler;

import app.AppConfig;
import app.models.ServentInfo;
import app.models.SubFractal;
import app.workers.JobExecutionWorker;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.StopJobAckMessage;
import servent.message.StopJobMessage;
import servent.message.util.MessageUtil;

import java.util.Map;

public class StopJobHandler implements MessageHandler {

    private final Message clientMessage;

    public StopJobHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.STOP_JOB) {
            StopJobMessage stopJobMessage = (StopJobMessage) clientMessage;

            JobExecutionWorker jobWorker = AppConfig.activeJobWorker;

            if (jobWorker != null && jobWorker.getSubFractal().getJob().equals(stopJobMessage.getJob())) {
                jobWorker.stop();
                AppConfig.activeJobWorker = null;
                AppConfig.jobResults.clear();
            }

            AppConfig.activeJobs.clear();
            AppConfig.assignedNodeSubFractals.clear();

            StopJobAckMessage stopJobAckMessage = new StopJobAckMessage(AppConfig.myServentInfo.getListenerPort(),
                    stopJobMessage.getSenderPort(), AppConfig.myServentInfo.getIpAddress(), stopJobMessage.getSenderIpAddress(), stopJobMessage.getJob());

            MessageUtil.sendMessage(stopJobAckMessage);
        } else {
            AppConfig.timestampedErrorPrint("STOP_JOB handler got something that is not stop job message.");
        }
    }
}
