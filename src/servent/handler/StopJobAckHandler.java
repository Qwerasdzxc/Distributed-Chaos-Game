package servent.handler;

import app.AppConfig;
import app.models.ServentInfo;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.StopJobAckMessage;


public class StopJobAckHandler implements MessageHandler {

    private final Message clientMessage;

    public StopJobAckHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.STOP_JOB_ACK) {
            StopJobAckMessage stopJobMessage = (StopJobAckMessage) clientMessage;
            ServentInfo teller = new ServentInfo(stopJobMessage.getSenderIpAddress(), stopJobMessage.getSenderPort());

            AppConfig.stopJobAcks.put(teller, stopJobMessage.getJob());
        } else {
            AppConfig.timestampedErrorPrint("STOP_JOB_ACK handler got something that is not stop job ack message.");
        }
    }
}
