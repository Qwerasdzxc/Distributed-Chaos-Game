package cli.command;

import app.AppConfig;
import app.models.Job;
import app.models.ServentInfo;
import servent.message.StopJobMessage;
import servent.message.util.MessageUtil;

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

        for (ServentInfo servent : AppConfig.activeNodes) {
            StopJobMessage stopJobMessage = new StopJobMessage(AppConfig.myServentInfo.getListenerPort(),
                    servent.getListenerPort(), AppConfig.myServentInfo.getIpAddress(), servent.getIpAddress(), jobToStop.get());

            MessageUtil.sendMessage(stopJobMessage);
        }
    }
}
