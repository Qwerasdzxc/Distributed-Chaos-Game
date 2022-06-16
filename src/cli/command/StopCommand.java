package cli.command;

import app.AppConfig;
import app.models.Job;
import app.models.ServentInfo;
import app.workers.JobStarterWorker;
import servent.message.StopJobMessage;
import servent.message.util.MessageUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

public class StopCommand implements CLICommand {

    @Override
    public String commandName() {
        return "stop";
    }

    @Override
    public void execute(String args) {
        if (args.trim().isEmpty())
            return;

        Optional<Job> jobToStop = AppConfig.activeJobs.stream().filter(job -> job.getName().equals(args.trim())).findAny();

        if (jobToStop.isEmpty()) {
            AppConfig.timestampedStandardPrint("Couldn't stop Job. It is not active in the system");
            return;
        }

        List<Job> jobs = new ArrayList<>(AppConfig.activeJobs);
        jobs.remove(jobToStop.get());

        JobStarterWorker jobStarterWorker = new JobStarterWorker(jobs, true);
        Thread jobStarterWorkerThread = new Thread(jobStarterWorker);
        jobStarterWorkerThread.start();
    }
}
