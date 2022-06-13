package cli.command;

import app.AppConfig;
import app.models.ServentInfo;
import servent.message.RequestStatusMessage;
import servent.message.util.MessageUtil;

public class StatusCommand implements CLICommand {

    @Override
    public String commandName() {
        return "status";
    }

    @Override
    public void execute(String args) {
        if (args.trim().isEmpty())
            return;

        for (ServentInfo servent : AppConfig.activeNodes) {
            RequestStatusMessage requestStatusMessage = new RequestStatusMessage(AppConfig.myServentInfo.getListenerPort(),
                    servent.getListenerPort(), AppConfig.myServentInfo.getIpAddress(), servent.getIpAddress(), args);

            MessageUtil.sendMessage(requestStatusMessage);
        }
    }
}
