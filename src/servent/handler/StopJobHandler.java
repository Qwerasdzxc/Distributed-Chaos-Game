package servent.handler;

import app.AppConfig;
import app.models.Job;
import app.models.ServentInfo;
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

            if (jobWorker != null && jobWorker.getJob().equals(stopJobMessage.getJob())) {
                jobWorker.stop();
                AppConfig.activeJobWorker = null;
                AppConfig.jobResults.clear();
            }

            AppConfig.activeJobs.remove(stopJobMessage.getJob());

            for (Map.Entry<ServentInfo, Job> assignedJob : AppConfig.assignedJobs.entrySet()) {
                if (assignedJob.getValue().equals(stopJobMessage.getJob())) {
                    assignedJob.setValue(null);
                }
            }
        } else {
            AppConfig.timestampedErrorPrint("STOP_JOB handler got something that is not stop job message.");
        }
    }
}
