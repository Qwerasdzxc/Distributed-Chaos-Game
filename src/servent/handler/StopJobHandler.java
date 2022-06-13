package servent.handler;

import app.AppConfig;
import app.workers.JobExecutionWorker;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.StopJobMessage;

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

            if (jobWorker != null && jobWorker.getJob().getName().equals(stopJobMessage.getJobName())) {
                jobWorker.stop();
                AppConfig.activeJobWorker = null;
                AppConfig.jobResults.clear();
            }
        } else {
            AppConfig.timestampedErrorPrint("STOP_JOB handler got something that is not stop job message.");
        }
    }
}
