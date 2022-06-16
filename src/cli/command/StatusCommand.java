package cli.command;

import app.AppConfig;
import app.models.ServentInfo;
import app.models.SubFractal;
import servent.message.RequestResultMessage;
import servent.message.RequestStatusMessage;
import servent.message.util.MessageUtil;

import java.util.Map;

public class StatusCommand implements CLICommand {

    @Override
    public String commandName() {
        return "status";
    }

    @Override
    public void execute(String argsStr) {
        String[] args = new String[]{};
        if (argsStr != null && argsStr.contains(" ")) {
            args = argsStr.split(" ");
        }

        String jobName = null;
        String fractalId = null;
        if (args.length == 1) {
            jobName = args[0];
        }
        else if (args.length == 2) {
            jobName = args[0];
            fractalId = args[1];
        }

        Map.Entry<ServentInfo, SubFractal> fidRequestEntry = null;

        for (Map.Entry<ServentInfo, SubFractal> entry : AppConfig.assignedNodeSubFractals.entrySet()) {
            if (jobName != null && !entry.getValue().getJob().getName().equals(jobName))
                continue;

            if (entry.getValue().getFractalId().getValue().equals(fractalId)) {
                fidRequestEntry = entry;
                break;
            }

            if (fractalId != null)
                continue;

            RequestStatusMessage requestStatusMessage = new RequestStatusMessage(AppConfig.myServentInfo.getListenerPort(),
                    entry.getKey().getListenerPort(), AppConfig.myServentInfo.getIpAddress(), entry.getKey().getIpAddress(), entry.getValue(), null, jobName != null);

            MessageUtil.sendMessage(requestStatusMessage);
        }

        if (fractalId != null && fidRequestEntry != null) {
            RequestStatusMessage requestStatusMessage = new RequestStatusMessage(AppConfig.myServentInfo.getListenerPort(),
                    fidRequestEntry.getKey().getListenerPort(), AppConfig.myServentInfo.getIpAddress(), fidRequestEntry.getKey().getIpAddress(), fidRequestEntry.getValue(), fractalId, false);

            MessageUtil.sendMessage(requestStatusMessage);
        }
    }
}
