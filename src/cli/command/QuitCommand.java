package cli.command;

import app.AppConfig;
import app.models.ServentInfo;
import app.workers.JobExecutionWorker;
import cli.CLIParser;
import servent.SimpleServentListener;
import servent.message.QuitMessage;
import servent.message.util.MessageUtil;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Objects;

public class QuitCommand implements CLICommand {

    private final CLIParser cliParser;
    private final SimpleServentListener listener;

    public QuitCommand(CLIParser cliParser, SimpleServentListener listener) {
        this.cliParser = cliParser;
        this.listener = listener;
    }

    @Override
    public String commandName() {
        return "quit";
    }

    @Override
    public void execute(String args) {
        for (ServentInfo servent : AppConfig.activeNodes) {
            if (Objects.equals(servent.getUuid(), AppConfig.myServentInfo.getUuid()))
                continue;

            QuitMessage quitMessage;
            JobExecutionWorker jobWorker = AppConfig.activeJobWorker;
            if (jobWorker != null) {
                quitMessage = new QuitMessage(AppConfig.myServentInfo.getListenerPort(),
                        servent.getListenerPort(), AppConfig.myServentInfo.getIpAddress(), servent.getIpAddress(),
                        jobWorker.getSubFractal(), new ArrayList<>(jobWorker.getCalculatedPoints()));
            } else {
                quitMessage = new QuitMessage(AppConfig.myServentInfo.getListenerPort(),
                        servent.getListenerPort(), AppConfig.myServentInfo.getIpAddress(),
                        servent.getIpAddress(), null, null);
            }

            MessageUtil.sendMessage(quitMessage);
        }

        try {
            Socket bsSocket = new Socket(AppConfig.BOOTSTRAP_IP, AppConfig.BOOTSTRAP_PORT);

            PrintWriter bsWriter = new PrintWriter(bsSocket.getOutputStream());
            bsWriter.write("Quit\n" + AppConfig.myServentInfo.getListenerPort() + "\n");
            bsWriter.write(AppConfig.myServentInfo.getIpAddress() + "\n");

            bsWriter.flush();
            bsSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        AppConfig.timestampedStandardPrint("Shutting down...");

        if (AppConfig.activeJobWorker != null)
            AppConfig.activeJobWorker.stop();

        AppConfig.buddyPingPongWorker.stop();
        AppConfig.buddyCarerWorker.stop();
        AppConfig.buddyBackupWorker.stop();
        cliParser.stop();
        listener.stop();
    }
}
