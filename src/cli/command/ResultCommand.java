package cli.command;

import app.AppConfig;
import app.models.ServentInfo;
import servent.message.RequestResultMessage;
import servent.message.util.MessageUtil;

public class ResultCommand implements CLICommand {

    @Override
    public String commandName() {
        return "result";
    }

    @Override
    public void execute(String args) {
        if (args.trim().isEmpty())
            return;

        for (ServentInfo servent : AppConfig.activeNodes) {
            RequestResultMessage requestResultMessage = new RequestResultMessage(AppConfig.myServentInfo.getListenerPort(),
                    servent.getListenerPort(), AppConfig.myServentInfo.getIpAddress(), servent.getIpAddress(), args);

            MessageUtil.sendMessage(requestResultMessage);
        }
    }

}

