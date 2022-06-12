package servent.handler;

import app.AppConfig;
import app.models.Job;
import app.models.Point;
import app.workers.JobExecutionWorker;
import servent.message.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.List;

public class ExecuteJobHandler implements MessageHandler {

    private final Message clientMessage;

    public ExecuteJobHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.EXECUTE_JOB) {
            ExecuteJobMessage executeJobMessage = (ExecuteJobMessage) clientMessage;

            AppConfig.timestampedStandardPrint("Starting job: " + executeJobMessage.getJob());

            JobExecutionWorker jobExecutionWorker = new JobExecutionWorker(executeJobMessage.getJob(), executeJobMessage.getAssignedStartingPoints());
            Thread jobExecution = new Thread(jobExecutionWorker);
            jobExecution.start();

            AppConfig.activeJobWorker = jobExecutionWorker;

            try {
                Thread.sleep(15000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            jobExecutionWorker.stop();

            AppConfig.activeJobWorker = null;

            renderImage(AppConfig.myServentInfo.getListenerPort(), executeJobMessage.getJob(), jobExecutionWorker.getCalculatedPoints());

            AppConfig.timestampedStandardPrint("Stopping job: " + executeJobMessage.getJob());
        } else {
            AppConfig.timestampedErrorPrint("EXECUTE_JOB handler got something that is not execute job message.");
        }
    }

    public static void renderImage(int port, Job job, List<Point> points) {
        BufferedImage image = new BufferedImage(job.getWidth(), job.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
        WritableRaster writableRaster = image.getRaster();
        int[] rgb = new int[3];
        rgb[0] = 255;
        for (Point point : points) {
            writableRaster.setPixel(point.getX(), point.getY(), rgb);
        }

        BufferedImage newImage = new BufferedImage(writableRaster.getWidth(), writableRaster.getHeight(),
                BufferedImage.TYPE_3BYTE_BGR);
        newImage.setData(writableRaster);
        try {
            File file = new File("chaos/fractals/" + job.getName() + "_" + job.getProportion() + "_" + port + ".png");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            file.createNewFile();
            ImageIO.write(newImage, "PNG", file);
        } catch (IOException e) {
            AppConfig.timestampedErrorPrint(e.getMessage());
            e.printStackTrace();
        }
    }
}
