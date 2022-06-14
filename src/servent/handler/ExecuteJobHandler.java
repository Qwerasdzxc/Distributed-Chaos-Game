package servent.handler;

import app.AppConfig;
import app.models.ServentInfo;
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

            for (ServentInfo node : executeJobMessage.getAssignedNodes()) {
                AppConfig.assignedJobs.put(node, executeJobMessage.getJob());
            }

            JobExecutionWorker jobExecutionWorker = new JobExecutionWorker(executeJobMessage.getJob(), executeJobMessage.getAssignedStartingPoints());
            Thread jobExecution = new Thread(jobExecutionWorker);
            jobExecution.start();

            AppConfig.activeJobWorker = jobExecutionWorker;
            AppConfig.activeJobs.add(executeJobMessage.getJob());
        } else {
            AppConfig.timestampedErrorPrint("EXECUTE_JOB handler got something that is not execute job message.");
        }
    }
}
