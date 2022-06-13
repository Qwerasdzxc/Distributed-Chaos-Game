package servent.handler;

import app.AppConfig;
import app.workers.JobExecutionWorker;
import servent.message.*;

public class ExecuteJobHandler implements MessageHandler {

    private final Message clientMessage;

    public ExecuteJobHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.EXECUTE_JOB) {
            ExecuteJobMessage executeJobMessage = (ExecuteJobMessage) clientMessage;

            JobExecutionWorker jobExecutionWorker = new JobExecutionWorker(executeJobMessage.getJob(), executeJobMessage.getAssignedStartingPoints());
            Thread jobExecution = new Thread(jobExecutionWorker);
            jobExecution.start();

            AppConfig.activeJobWorker = jobExecutionWorker;
        } else {
            AppConfig.timestampedErrorPrint("EXECUTE_JOB handler got something that is not execute job message.");
        }
    }
}
