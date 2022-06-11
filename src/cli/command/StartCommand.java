package cli.command;

import app.AppConfig;
import app.models.Job;
import app.models.Point;
import app.workers.JobExecutionWorker;
import cli.CLIParser;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class StartCommand implements CLICommand {

    private final CLIParser parser;

    public StartCommand(CLIParser parser) {
        this.parser = parser;
    }

    @Override
    public String commandName() {
        return "start";
    }

    @Override
    public void execute(String args) {
        Job job;

        if (args != null) {
            job = AppConfig.myServentInfo.getJob(args);

            if (job == null) {
                AppConfig.timestampedErrorPrint("Job doesn't exist.");
                return;
            }
        } else {
            Scanner scanner = parser.getScanner();

            AppConfig.timestampedStandardPrint("Set job name: ");
            String name = scanner.nextLine().trim();

            if (AppConfig.myServentInfo.hasJob(name)) {
                AppConfig.timestampedErrorPrint("Job already exists.");
                return;
            }

            AppConfig.timestampedStandardPrint("Set job position count: ");
            int n = scanner.nextInt();
            scanner.nextLine();

            AppConfig.timestampedStandardPrint("Set proportion: ");
            double proportion = scanner.nextDouble();
            scanner.nextLine();

            AppConfig.timestampedStandardPrint("Set image width: ");
            int width = scanner.nextInt();
            scanner.nextLine();

            AppConfig.timestampedStandardPrint("Set image height: ");
            int height = scanner.nextInt();
            scanner.nextLine();

            List<Point> positions = new ArrayList<>();
            for (int i = 0; i < n; i++) {
                AppConfig.timestampedStandardPrint("Set coordinates for position " + i + ", separated by commas: ");
                String[] coordinates = scanner.nextLine().trim().split(",");
                int x = Integer.parseInt(coordinates[0]);
                int y = Integer.parseInt(coordinates[1]);

                positions.add(new Point(x, y));
            }

            job = new Job(name, n, width, height, proportion, positions);
        }

        AppConfig.timestampedStandardPrint("Starting job: " + job);

        JobExecutionWorker jobExecutionWorker = new JobExecutionWorker(job);
        Thread jobExecution = new Thread(jobExecutionWorker);
        jobExecution.start();

        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        jobExecutionWorker.stop();
        renderImage(job, jobExecutionWorker.getCalculatedPoints());
    }

    public static void renderImage(Job job, List<Point> points) {
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
            File file = new File("chaos/fractals/" + job.getName() + "_" + job.getProportion() + ".png");
            if (!file.getParentFile().exists()) {
                file.getParentFile().mkdirs();
            }

            file.createNewFile();
            ImageIO.write(newImage, "PNG", file);
            AppConfig.timestampedStandardPrint("Fractal image rendered.");
        } catch (IOException e) {
            AppConfig.timestampedErrorPrint(e.getMessage());
            e.printStackTrace();
        }
    }
}
