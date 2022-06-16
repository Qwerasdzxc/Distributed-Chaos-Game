package servent.handler;

import app.AppConfig;
import app.models.ServentInfo;
import app.workers.JobExecutionWorker;
import servent.message.*;

import java.util.HashSet;

public class ExecuteJobHandler implements MessageHandler {

    private final Message clientMessage;

    public ExecuteJobHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.EXECUTE_JOB) {
            ExecuteJobMessage executeJobMessage = (ExecuteJobMessage) clientMessage;

            AppConfig.assignedNodeSubFractals.putAll(executeJobMessage.getAssignedNodeSubFractals());

            JobExecutionWorker jobExecutionWorker = new JobExecutionWorker(executeJobMessage.getSubFractal(), executeJobMessage.getSubFractal().getSubFractalPositions());
            Thread jobExecution = new Thread(jobExecutionWorker);
            jobExecution.start();

            AppConfig.activeJobWorker = jobExecutionWorker;
            AppConfig.activeJobs = new HashSet<>(executeJobMessage.getActiveJobs());
        } else {
            AppConfig.timestampedErrorPrint("EXECUTE_JOB handler got something that is not execute job message.");
        }
    }
}
