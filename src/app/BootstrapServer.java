package app;

import app.models.ServentInfo;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.function.Predicate;

public class BootstrapServer {

	private volatile boolean working = true;
	private final List<ServentInfo> activeServents;
	
	private class CLIWorker implements Runnable {
		@Override
		public void run() {
			Scanner sc = new Scanner(System.in);
			
			String line;
			while(true) {
				line = sc.nextLine();
				
				if (line.equals("halt")) {
					working = false;
					break;
				}
			}
			
			sc.close();
		}
	}
	
	public BootstrapServer() {
		activeServents = new ArrayList<>();
	}
	
	public void doBootstrap(int bsPort) {
		Thread cliThread = new Thread(new CLIWorker());
		cliThread.start();
		
		ServerSocket listenerSocket = null;
		try {
			listenerSocket = new ServerSocket(bsPort);
			listenerSocket.setSoTimeout(0);
		} catch (IOException e1) {
			AppConfig.timestampedErrorPrint("Problem while opening listener socket.");
			System.exit(0);
		}
		
		Random rand = new Random(System.currentTimeMillis());
		
		while (working) {
			try {
				Socket newServentSocket = listenerSocket.accept();
				
				 /* 
				 * Handling these messages is intentionally sequential, to avoid problems with
				 * concurrent initial starts.
				 * 
				 * In practice, we would have an always-active backbone of servents to avoid this problem.
				 */
				
				Scanner socketScanner = new Scanner(newServentSocket.getInputStream());
				String message = socketScanner.nextLine();

				/*
				 * New servent has hailed us. He is sending us his own listener port.
				 * He wants to get a listener port from a random active servent,
				 * or -1 if he is the first one.
				 */
				if (message.equals("Hail")) {
					int newServentPort = socketScanner.nextInt();
					socketScanner.nextLine();
					String newServentIp = socketScanner.nextLine();

					AppConfig.timestampedStandardPrint("got " + newServentPort + " on IP " + newServentIp);
					PrintWriter socketWriter = new PrintWriter(newServentSocket.getOutputStream());
					
					if (activeServents.isEmpty()) {
						socketWriter.write(-1 + "\n" + newServentIp);
						activeServents.add(new ServentInfo(newServentIp, newServentPort));

						AppConfig.timestampedStandardPrint("adding " + newServentPort + " on IP " + newServentIp);
					} else {
						ServentInfo randServent = activeServents.get(rand.nextInt(activeServents.size()));
						socketWriter.write(randServent.getListenerPort() + "\n" + randServent.getIpAddress());
					}
					
					socketWriter.flush();
					newServentSocket.close();
				} else if (message.equals("New")) {
					/**
					 * When a servent is confirmed not to be a collider, we add him to the list.
					 */
					int newServentPort = socketScanner.nextInt();
					socketScanner.nextLine();
					String newServentIp = socketScanner.nextLine();
					
					AppConfig.timestampedStandardPrint("adding " + newServentPort + " on IP " + newServentIp);
					
					activeServents.add(new ServentInfo(newServentIp, newServentPort));
					newServentSocket.close();
				} else if (message.equals("Quit")) {
					int quitterServentPort = socketScanner.nextInt();
					socketScanner.nextLine();
					String quitterServentIp = socketScanner.nextLine();
					ServentInfo quitter = new ServentInfo(quitterServentIp, quitterServentPort);

					AppConfig.timestampedStandardPrint("removing " + quitterServentPort + " on IP " + quitterServentIp);

					activeServents.removeIf(serventInfo -> serventInfo.getUuid().equals(quitter.getUuid()));
					newServentSocket.close();
				}
				
			} catch (IOException e) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				AppConfig.timestampedErrorPrint(sw.toString());
			}
		}
	}
	
	/**
	 * Expects one command line argument - the port to listen on.
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			AppConfig.timestampedErrorPrint("Bootstrap started without port argument.");
		}
		
		int bsPort = 0;
		try {
			bsPort = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			AppConfig.timestampedErrorPrint("Bootstrap port not valid: " + args[0]);
			System.exit(0);
		}
		
		AppConfig.timestampedStandardPrint("Bootstrap server started on port: " + bsPort);
		
		BootstrapServer bs = new BootstrapServer();
		bs.doBootstrap(bsPort);
	}
}
