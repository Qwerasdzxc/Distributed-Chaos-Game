package servent.handler;

import app.AppConfig;
import app.models.Job;
import app.models.JobResult;
import app.models.Point;
import app.models.ServentInfo;
import servent.message.Message;
import servent.message.MessageType;
import servent.message.TellResultMessage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class TellResultHandler implements MessageHandler {

    private final Message clientMessage;

    public TellResultHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.TELL_RESULT) {
            TellResultMessage tellResultMessage = (TellResultMessage) clientMessage;

            // TODO: Handle the case when we are not the owner of the job (result command called on node without declared jobs)
            Job job = AppConfig.myServentInfo.getJob(tellResultMessage.getJobName());

            ServentInfo teller = new ServentInfo(tellResultMessage.getSenderIpAddress(), tellResultMessage.getSenderPort());

            AppConfig.jobResults.put(teller,
                    new JobResult(tellResultMessage.getJobName(), tellResultMessage.getCalculatedPoints()));

            // TODO: Handle nested fractals
            if (job.getN() == AppConfig.jobResults.size()) {
                List<Point> mergedPoints = new ArrayList<>();
                for (JobResult jobResult : AppConfig.jobResults.values()) {
                    mergedPoints.addAll(jobResult.getCalculatedPoints());
                }

                AppConfig.jobResults.clear();

                renderImage(AppConfig.myServentInfo.getListenerPort(), job, mergedPoints);
                AppConfig.timestampedStandardPrint("Generated PNG for job: " + job.getName());
            }
        } else {
            AppConfig.timestampedErrorPrint("TELL_RESULT handler got something that is not tell result message.");
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
