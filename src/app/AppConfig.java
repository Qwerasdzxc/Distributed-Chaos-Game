package app;

import app.models.Job;
import app.models.Point;
import app.models.ServentInfo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 * This class contains all the global application configuration stuff.
 * @author bmilojkovic
 *
 */
public class AppConfig {

	/**
	 * Convenience access for this servent's information
	 */
	public static ServentInfo myServentInfo;
	
	/**
	 * Print a message to stdout with a timestamp
	 * @param message message to print
	 */
	public static void timestampedStandardPrint(String message) {
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		Date now = new Date();
		
		System.out.println(timeFormat.format(now) + " - " + message);
	}
	
	/**
	 * Print a message to stderr with a timestamp
	 * @param message message to print
	 */
	public static void timestampedErrorPrint(String message) {
		DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
		Date now = new Date();
		
		System.err.println(timeFormat.format(now) + " - " + message);
	}
	
	public static boolean INITIALIZED = false;

	public static String BOOTSTRAP_IP;

	public static int BOOTSTRAP_PORT;

	/**
	 * Reads a config file. Should be called once at start of app.
	 * The config file should be of the following format:
	 * <br/>
	 * <code><br/>
	 * ip=localhost				- bootstrap server listener ip address <br/>
	 * port=2000				- bootstrap server listener port <br/>
	 * 
	 * </code>
	 * <br/>
	 * 
	 * @param configName name of configuration file
	 */
	public static void readBsConfig(String configName){
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(new File(configName)));
			
		} catch (IOException e) {
			timestampedErrorPrint("Couldn't open properties file. Exiting...");
			System.exit(0);
		}

		BOOTSTRAP_IP = properties.getProperty("ip");
		
		try {
			BOOTSTRAP_PORT = Integer.parseInt(properties.getProperty("port"));
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading bootstrap_port. Exiting...");
			System.exit(0);
		}
		
		myServentInfo = new ServentInfo(BOOTSTRAP_IP, BOOTSTRAP_PORT);
	}

	/**
	 * Reads a config file. Should be called once at start of app.
	 * The config file should be of the following format:
	 * <br/>
	 * <code><br/>
	 * ip=localhost				- server listener ip address <br/>
	 * port=1000				- server listener port <br/>
	 * bs.ip=localhost			- bootstrap ip address <br/>
	 * bs.port=4000				- bootstrap port <br/>
	 *
	 * </code>
	 * <br/>
	 *
	 * @param configName name of configuration file
	 */
	public static void readServentConfig(String configName){
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(new File(configName)));

		} catch (IOException e) {
			timestampedErrorPrint("Couldn't open properties file. Exiting...");
			System.exit(0);
		}

		BOOTSTRAP_IP = properties.getProperty("bs.ip");

		try {
			BOOTSTRAP_PORT = Integer.parseInt(properties.getProperty("bs.port"));
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading bootstrap_port. Exiting...");
			System.exit(0);
		}

		try {
			String ip = properties.getProperty("ip");
			int port = Integer.parseInt(properties.getProperty("port"));

			myServentInfo = new ServentInfo(ip, port);
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading port. Exiting...");
			System.exit(0);
		}

		try {
			int jobCount = Integer.parseInt(properties.getProperty("job_count"));

			for (int i = 0; i < jobCount; i++) {
				String name = properties.getProperty("job." + i + ".name");

				int n = Integer.parseInt(properties.getProperty("job." + i + ".n"));
				double proportion = Double.parseDouble(properties.getProperty("job." + i + ".proportion"));
				int width = Integer.parseInt(properties.getProperty("job." + i + ".width"));
				int height = Integer.parseInt(properties.getProperty("job." + i + ".height"));

				List<Point> positions = new ArrayList<>();

				for (int j = 0; j < n; j++) {
					String[] coordinates = properties.getProperty("job." + i + ".position." + j).split(",");
					int x = Integer.parseInt(coordinates[0]);
					int y = Integer.parseInt(coordinates[1]);

					positions.add(new Point(x, y));
				}

				myServentInfo.addJob(new Job(name, n, width, height, proportion, positions));
			}

			timestampedStandardPrint(myServentInfo.getJobs().toString());
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading jobs. Exiting...");
			System.exit(0);
		}
	}
	
}
