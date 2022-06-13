package cli.command;

import app.AppConfig;
import app.models.ServentInfo;
import servent.message.StopJobMessage;
import servent.message.util.MessageUtil;

public class StopCommand implements CLICommand {

    @Override
    public String commandName() {
        return "stop";
    }

    @Override
    public void execute(String args) {
        if (args.trim().isEmpty())
            return;

        for (ServentInfo servent : AppConfig.activeNodes) {
            StopJobMessage stopJobMessage = new StopJobMessage(AppConfig.myServentInfo.getListenerPort(),
                    servent.getListenerPort(), AppConfig.myServentInfo.getIpAddress(), servent.getIpAddress(), args);

            MessageUtil.sendMessage(stopJobMessage);
        }
    }
}
