package app.workers;

import app.AppConfig;
import app.Cancellable;
import app.models.JobResult;
import app.models.ServentInfo;
import servent.message.BuddyBackupMessage;
import servent.message.util.MessageUtil;

import java.util.ArrayList;

public class BuddyBackupWorker implements Runnable, Cancellable {

    private volatile boolean working = true;

    @Override
    public void run() {
        while (working) {
            ServentInfo me = AppConfig.myServentInfo;

            try {
//                Thread.sleep(me.getStrongFailureLimit() / 2L);
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!working)
                return;

            JobExecutionWorker jobWorker = AppConfig.activeJobWorker;

            if (jobWorker == null)
                continue;

            ServentInfo firstBuddy = AppConfig.getMyFirstBuddy(me);

            if (firstBuddy == null)
                continue;

            // Sending backup to first buddy
            BuddyBackupMessage buddy1BackupMessage = new BuddyBackupMessage(
                    me.getListenerPort(), firstBuddy.getListenerPort(), me.getIpAddress(),
                    firstBuddy.getIpAddress(), new JobResult(jobWorker.getSubFractal(), new ArrayList<>(jobWorker.getCalculatedPoints())));

            MessageUtil.sendMessage(buddy1BackupMessage);

            // Sending backup to second buddy, if he exists
            ServentInfo secondBuddy = AppConfig.getMySecondBuddy(me);

            if (secondBuddy == null)
                continue;

            BuddyBackupMessage buddy2BackupMessage = new BuddyBackupMessage(
                    me.getListenerPort(), secondBuddy.getListenerPort(), me.getIpAddress(),
                    secondBuddy.getIpAddress(), new JobResult(jobWorker.getSubFractal(), new ArrayList<>(jobWorker.getCalculatedPoints())));

            MessageUtil.sendMessage(buddy2BackupMessage);
        }
    }

    @Override
    public void stop() {
        working = false;
    }

    public boolean isWorking() {
        return working;
    }

}
