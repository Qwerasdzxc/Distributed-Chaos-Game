package app.workers;

import app.AppConfig;
import app.Cancellable;
import app.models.JobResult;
import app.models.ServentInfo;
import servent.message.BuddyIsAliveMessage;
import servent.message.util.MessageUtil;

import java.sql.Timestamp;

public class BuddyCarerWorker implements Runnable, Cancellable {

    private volatile boolean working = true;

    @Override
    public void run() {
        while (working) {
            ServentInfo me = AppConfig.myServentInfo;

            try {
                Thread.sleep(me.getWeakFailureLimit() * 2L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!working)
                return;

            ServentInfo firstBuddy = AppConfig.getMyFirstBuddy(me);
            ServentInfo secondBuddy = AppConfig.getMySecondBuddy(me);

            if (firstBuddy == null)
                continue;

            long now = System.currentTimeMillis();
            Timestamp firstBuddyLastPong = AppConfig.buddyNodesLastPongTime.get(firstBuddy);

            if (firstBuddyLastPong != null && now - firstBuddyLastPong.getTime() > AppConfig.myServentInfo.getWeakFailureLimit()) {
                Boolean prevStatus = AppConfig.buddyNodesSuspicionStatus.put(firstBuddy, true);
                if (prevStatus != null && !prevStatus) {
                    // Ask buddy's other buddy if he is alive
                    ServentInfo buddiesOtherBuddy = AppConfig.getBuddiesOtherBuddy(me, firstBuddy);
                    if (buddiesOtherBuddy != null) {
                        BuddyIsAliveMessage buddyIsAliveMessage = new BuddyIsAliveMessage(
                                me.getListenerPort(), buddiesOtherBuddy.getListenerPort(), me.getIpAddress(), buddiesOtherBuddy.getIpAddress(), firstBuddy);
                        MessageUtil.sendMessage(buddyIsAliveMessage);
                    }
                }
                else if (now - firstBuddyLastPong.getTime() > AppConfig.myServentInfo.getStrongFailureLimit()) {
                    startRemovalProcessForBuddy(firstBuddy);
                }
            }

            if (secondBuddy == null)
                continue;

            now = System.currentTimeMillis();
            Timestamp secondBuddyLastPong = AppConfig.buddyNodesLastPongTime.get(secondBuddy);

            if (secondBuddyLastPong != null && now - secondBuddyLastPong.getTime() > AppConfig.myServentInfo.getWeakFailureLimit()) {
                Boolean prevStatus = AppConfig.buddyNodesSuspicionStatus.put(secondBuddy, true);
                if (prevStatus != null && !prevStatus) {
                    // Ask buddy's other buddy if he is alive
                    ServentInfo buddiesOtherBuddy = AppConfig.getBuddiesOtherBuddy(me, secondBuddy);
                    if (buddiesOtherBuddy != null) {
                        BuddyIsAliveMessage buddyIsAliveMessage = new BuddyIsAliveMessage(
                                me.getListenerPort(), buddiesOtherBuddy.getListenerPort(), me.getIpAddress(), buddiesOtherBuddy.getIpAddress(), secondBuddy);
                        MessageUtil.sendMessage(buddyIsAliveMessage);
                    }
                }
                else if (now - secondBuddyLastPong.getTime() > AppConfig.myServentInfo.getStrongFailureLimit()) {
                    startRemovalProcessForBuddy(secondBuddy);
                }
            }
        }
    }

    private void startRemovalProcessForBuddy(ServentInfo buddy) {
        // Saving Buddy's last known work
        // TODO: If we were not working on this job, send it to other nodes who were
        JobExecutionWorker jobWorker = AppConfig.activeJobWorker;
        if (jobWorker != null) {
            JobResult lastBuddyBackup = AppConfig.buddyJobResultBackups.get(buddy);
            if (lastBuddyBackup != null && jobWorker.getSubFractal().getJob().equals(lastBuddyBackup.getSubFractal().getJob())) {
                jobWorker.getCalculatedPoints().addAll(lastBuddyBackup.getCalculatedPoints());

                AppConfig.timestampedStandardPrint("I took over calculated points from node: " + buddy.getListenerPort());
            }
        }

        // TODO: Start node removal process
        AppConfig.timestampedStandardPrint("Starting removal process for node: " + buddy.getListenerPort());
    }

    @Override
    public void stop() {
        working = false;
    }

    public boolean isWorking() {
        return working;
    }
}
