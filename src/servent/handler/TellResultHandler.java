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
import java.util.Optional;
import java.util.function.Predicate;

public class TellResultHandler implements MessageHandler {

    private final Message clientMessage;

    public TellResultHandler(Message clientMessage) {
        this.clientMessage = clientMessage;
    }

    @Override
    public void run() {
        if (clientMessage.getMessageType() == MessageType.TELL_RESULT) {
            TellResultMessage tellResultMessage = (TellResultMessage) clientMessage;

            Optional<Job> job = AppConfig.activeJobs.stream().filter(job1 -> job1.equals(tellResultMessage.getSubFractal().getJob())).findFirst();
            if (job.isEmpty()) {
                AppConfig.timestampedStandardPrint("Got TELL_RESULT for job I don't have in system.");
                return;
            }

            ServentInfo teller = new ServentInfo(tellResultMessage.getSenderIpAddress(), tellResultMessage.getSenderPort());

            AppConfig.jobResults.put(teller,
                    new JobResult(tellResultMessage.getSubFractal(), tellResultMessage.getCalculatedPoints()));

            if (!tellResultMessage.isTotal()) {
                AppConfig.jobResults.clear();

                renderImage(tellResultMessage.getSubFractal().getFractalId().getValue(), job.get(), tellResultMessage.getCalculatedPoints());
                AppConfig.timestampedStandardPrint("Generated PNG for job: " + job.get().getName() + " for FID: " + tellResultMessage.getSubFractal().getFractalId().getValue());
                return;
            }

            long totalSubFractalCount = AppConfig.assignedNodeSubFractals.values().stream().filter(
                    subFractal -> subFractal.getJob().equals(job.get())).count();

            if (totalSubFractalCount == AppConfig.jobResults.size()) {
                List<Point> mergedPoints = new ArrayList<>();
                for (JobResult jobResult : AppConfig.jobResults.values()) {
                    mergedPoints.addAll(jobResult.getCalculatedPoints());
                }

                AppConfig.jobResults.clear();

                renderImage(job.get(), mergedPoints);
                AppConfig.timestampedStandardPrint("Generated PNG for job: " + job.get().getName());
            }
        } else {
            AppConfig.timestampedErrorPrint("TELL_RESULT handler got something that is not tell result message.");
        }
    }

    public static void renderImage(Job job, List<Point> points) {
        renderImage("total", job, points);
    }

    public static void renderImage(String fid, Job job, List<Point> points) {
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
            File file = new File("chaos/fractals/" + job.getName() + "_" + job.getProportion() + "_" + fid + ".png");
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
