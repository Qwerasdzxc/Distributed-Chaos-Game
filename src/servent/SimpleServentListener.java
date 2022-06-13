package servent;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import app.AppConfig;
import app.Cancellable;
import servent.handler.*;
import servent.message.Message;
import servent.message.util.MessageUtil;

public class SimpleServentListener implements Runnable, Cancellable {

    private volatile boolean working = true;

    public SimpleServentListener() {

    }

    /*
     * Thread pool for executing the handlers. Each client will get it's own handler thread.
     */
    private final ExecutorService threadPool = Executors.newWorkStealingPool();

    @Override
    public void run() {
        ServerSocket listenerSocket = null;
        try {
            listenerSocket = new ServerSocket(AppConfig.myServentInfo.getListenerPort(), 100);
            /*
             * If there is no connection after 1s, wake up and see if we should terminate.
             */
            listenerSocket.setSoTimeout(1000);
        } catch (IOException e) {
            AppConfig.timestampedErrorPrint("Couldn't open listener socket on: " + AppConfig.myServentInfo.getListenerPort());
            System.exit(0);
        }


        while (working) {
            try {
                Message clientMessage;

                Socket clientSocket = listenerSocket.accept();

                //GOT A MESSAGE! <3
                clientMessage = MessageUtil.readMessage(clientSocket);

                MessageHandler messageHandler = new NullHandler(clientMessage);

                /*
                 * Each message type has its own handler.
                 * If we can get away with stateless handlers, we will,
                 * because that way is much simpler and less error-prone.
                 */
                switch (clientMessage.getMessageType()) {
                    case NEW_NODE:
                        messageHandler = new NewNodeHandler(clientMessage);
                        break;
                    case WELCOME:
                        messageHandler = new WelcomeHandler(clientMessage);
                        break;
                    case NODE_JOINED:
                        messageHandler = new NodeJoinedHandler(clientMessage);
                        break;
                    case EXECUTE_JOB:
                        messageHandler = new ExecuteJobHandler(clientMessage);
                        break;
                    case STOP_JOB:
                        messageHandler = new StopJobHandler(clientMessage);
                        break;
                    case REQUEST_RESULT:
                        messageHandler = new RequestResultHandler(clientMessage);
                        break;
                    case TELL_RESULT:
                        messageHandler = new TellResultHandler(clientMessage);
                        break;
                    case REQUEST_STATUS:
                        messageHandler = new RequestStatusHandler(clientMessage);
                        break;
                    case TELL_STATUS:
                        messageHandler = new TellStatusHandler(clientMessage);
                        break;
                    case QUIT:
                        messageHandler = new QuitHandler(clientMessage);
                }

                threadPool.submit(messageHandler);
            } catch (SocketTimeoutException timeoutEx) {
                //Uncomment the next line to see that we are waking up every second.
//				AppConfig.timedStandardPrint("Waiting...");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() {
        this.working = false;
    }

}
