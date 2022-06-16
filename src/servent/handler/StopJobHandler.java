package servent.handler;

import app.AppConfig;
import app.models.ServentInfo;
import app.models.SubFractal;
import app.workers.JobExecutionWorker;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.StopJobMessage;

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

            AppConfig.activeJobs.remove(stopJobMessage.getJob());

            for (Map.Entry<ServentInfo, SubFractal> assignedJob : AppConfig.assignedNodeSubFractals.entrySet()) {
                if (assignedJob.getValue().getJob().equals(stopJobMessage.getJob())) {
                    assignedJob.setValue(null);
                }
            }
        } else {
            AppConfig.timestampedErrorPrint("STOP_JOB handler got something that is not stop job message.");
        }
    }
}
