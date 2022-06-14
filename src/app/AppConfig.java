package app;

import app.models.*;
import app.workers.BuddyCarerWorker;
import app.workers.JobExecutionWorker;
import app.workers.PingPongWorker;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
	 * Currently active nodes in system
	 */
	public static List<ServentInfo> activeNodes;

	/**
	 * Currently active job worker in our node
	 */
	public static JobExecutionWorker activeJobWorker;

	/**
	 * Buddy system for pinging nodes
	 */
	public static PingPongWorker pingPongWorker;

	/**
	 * Buddy system for checking node failures
	 */
	public static BuddyCarerWorker buddyCarerWorker;

	/**
	 * Accumulating received TellResult messages
	 */
	public static Map<ServentInfo, JobResult> jobResults;

	/**
	 * Accumulating received TellStatus messages
	 */
	public static Map<ServentInfo, NodeStatus> nodeStatuses;

	/**
	 * Current suspicious status of our watching nodes in Buddy system
	 */
	public static Map<ServentInfo, Boolean> buddyNodesSuspicionStatus;

	/**
	 * Current really suspicious status of our watching nodes in Buddy system
	 */
	public static Map<ServentInfo, Timestamp> buddyNodesLastPongTime;
	
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

	public static ServentInfo getMyFirstBuddy(ServentInfo me) {
		if (activeNodes.size() <= 1)
			return null;

		int myIndex = activeNodes.indexOf(me);

		if (myIndex == 0)
			return activeNodes.get(AppConfig.activeNodes.size() - 1);

		return activeNodes.get(myIndex - 1);
	}

	public static ServentInfo getMySecondBuddy(ServentInfo me) {
		if (activeNodes.size() <= 2)
			return null;

		int myIndex = activeNodes.indexOf(me);
		if (myIndex == activeNodes.size() - 1)
			return activeNodes.get(0);

		return activeNodes.get(myIndex + 1);
	}

	public static ServentInfo getBuddiesOtherBuddy(ServentInfo me, ServentInfo buddy) {
		if (activeNodes.size() <= 2)
			return null;

		int buddyIndex = activeNodes.indexOf(buddy);

		if (buddyIndex == 0) {
			ServentInfo buddy1 = activeNodes.get(AppConfig.activeNodes.size() - 1);
			ServentInfo buddy2 = activeNodes.get(1);

			if (buddy1.equals(me))
				return buddy2;

			return buddy1;
		}

		if (buddyIndex == activeNodes.size() - 1) {
			ServentInfo buddy1 = activeNodes.get(AppConfig.activeNodes.size() - 2);
			ServentInfo buddy2 = activeNodes.get(0);

			if (buddy1.equals(me))
				return buddy2;

			return buddy1;
		}

		ServentInfo buddy1 = activeNodes.get(buddyIndex + 1);
		ServentInfo buddy2 = activeNodes.get(buddyIndex - 1);

		if (buddy1.equals(me))
			return buddy2;

		return buddy1;
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
			int weakFailureLimit = Integer.parseInt(properties.getProperty("weak_failure_limit"));
			int strongFailureLimit = Integer.parseInt(properties.getProperty("strong_failure_limit"));

			myServentInfo.setWeakFailureLimit(weakFailureLimit);
			myServentInfo.setStrongFailureLimit(strongFailureLimit);
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
		} catch (NumberFormatException e) {
			timestampedErrorPrint("Problem reading jobs. Exiting...");
			System.exit(0);
		}

		pingPongWorker = new PingPongWorker();
		Thread pingPongWorkerThread = new Thread(pingPongWorker);
		pingPongWorkerThread.start();

		buddyCarerWorker = new BuddyCarerWorker();
		Thread buddyCarerWorkerThread = new Thread(buddyCarerWorker);
		buddyCarerWorkerThread.start();

		jobResults = new ConcurrentHashMap<>();
		nodeStatuses = new ConcurrentHashMap<>();
		buddyNodesSuspicionStatus = new ConcurrentHashMap<>();
		buddyNodesLastPongTime = new ConcurrentHashMap<>();
		activeNodes = new ArrayList<>();
	}

	public static String sha256(final String base) {
		try{
			final MessageDigest digest = MessageDigest.getInstance("SHA-256");
			final byte[] hash = digest.digest(base.getBytes(StandardCharsets.UTF_8));
			final StringBuilder hexString = new StringBuilder();
			for (byte b : hash) {
				final String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString();
		} catch(Exception ex){
			throw new RuntimeException(ex);
		}
	}
}
