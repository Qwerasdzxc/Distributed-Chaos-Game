package app.workers;

import app.AppConfig;
import app.Cancellable;
import app.models.ServentInfo;
import servent.message.BuddyPingMessage;
import servent.message.util.MessageUtil;

public class PingPongWorker implements Runnable, Cancellable {

    private volatile boolean working = true;

    @Override
    public void run() {
        while (working) {
            ServentInfo me = AppConfig.myServentInfo;

            try {
                Thread.sleep(me.getWeakFailureLimit() / 2);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (!working)
                return;

            ServentInfo firstBuddy = AppConfig.getMyFirstBuddy(me);

            if (firstBuddy == null)
                continue;

            // Pinging first buddy
            BuddyPingMessage buddy1PingMessage = new BuddyPingMessage(
                    me.getListenerPort(), firstBuddy.getListenerPort(), me.getIpAddress(), firstBuddy.getIpAddress());
            MessageUtil.sendMessage(buddy1PingMessage);

            // Pinging second buddy, if he exists
            ServentInfo secondBuddy = AppConfig.getMySecondBuddy(me);

            if (secondBuddy == null)
                continue;

            BuddyPingMessage buddy2PingMessage = new BuddyPingMessage(
                    me.getListenerPort(), secondBuddy.getListenerPort(), me.getIpAddress(), secondBuddy.getIpAddress());
            MessageUtil.sendMessage(buddy2PingMessage);
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
