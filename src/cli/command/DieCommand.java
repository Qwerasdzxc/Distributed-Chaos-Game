package cli.command;

import app.AppConfig;
import cli.CLIParser;
import servent.SimpleServentListener;

public class DieCommand implements CLICommand {

    private final CLIParser cliParser;
    private final SimpleServentListener listener;

    public DieCommand(CLIParser cliParser, SimpleServentListener listener) {
        this.cliParser = cliParser;
        this.listener = listener;
    }

    @Override
    public String commandName() {
        return "die";
    }

    @Override
    public void execute(String args) {
        AppConfig.timestampedStandardPrint("I am passing to the other side without telling anyone...");

        if (AppConfig.activeJobWorker != null)
            AppConfig.activeJobWorker.stop();

        AppConfig.pingPongWorker.stop();
        AppConfig.buddyCarerWorker.stop();
        cliParser.stop();
        listener.stop();
    }
}
