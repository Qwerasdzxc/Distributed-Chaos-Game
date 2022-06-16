package cli.command;

import app.AppConfig;
import app.models.ServentInfo;
import app.models.SubFractal;
import servent.message.RequestResultMessage;
import servent.message.util.MessageUtil;

import java.util.Map;

public class ResultCommand implements CLICommand {

    @Override
    public String commandName() {
        return "result";
    }

    @Override
    public void execute(String args) {
        if (args.trim().isEmpty())
            return;

        String jobName = args.split(" ")[0];
        String fractalId = null;

        if (args.contains(" ")) {
            fractalId = args.split(" ")[1];
        }

        Map.Entry<ServentInfo, SubFractal> fidRequestEntry = null;

        for (Map.Entry<ServentInfo, SubFractal> entry : AppConfig.assignedNodeSubFractals.entrySet()) {
            if (!entry.getValue().getJob().getName().equals(jobName))
                continue;

            if (entry.getValue().getFractalId().getValue().equals(fractalId)) {
                fidRequestEntry = entry;
                break;
            }

            if (fractalId != null)
                continue;

            RequestResultMessage requestResultMessage = new RequestResultMessage(AppConfig.myServentInfo.getListenerPort(),
                    entry.getKey().getListenerPort(), AppConfig.myServentInfo.getIpAddress(), entry.getKey().getIpAddress(), entry.getValue(), true);

            MessageUtil.sendMessage(requestResultMessage);
        }

        if (fractalId != null && fidRequestEntry != null) {
            RequestResultMessage requestResultMessage = new RequestResultMessage(AppConfig.myServentInfo.getListenerPort(),
                    fidRequestEntry.getKey().getListenerPort(), AppConfig.myServentInfo.getIpAddress(), fidRequestEntry.getKey().getIpAddress(), fidRequestEntry.getValue(), false);

            MessageUtil.sendMessage(requestResultMessage);
        }
    }

}

