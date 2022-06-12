package app;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import app.models.ServentInfo;
import servent.message.NewNodeMessage;
import servent.message.util.MessageUtil;

public class ServentInitializer implements Runnable {

	private ServentInfo getSomeServentFromSystem() {
		int bsPort = AppConfig.BOOTSTRAP_PORT;

		try {
			Socket bsSocket = new Socket("localhost", bsPort);
			
			PrintWriter bsWriter = new PrintWriter(bsSocket.getOutputStream());
			bsWriter.write("Hail\n" + AppConfig.myServentInfo.getListenerPort() + "\n");
			bsWriter.write(AppConfig.myServentInfo.getIpAddress() + "\n");
			bsWriter.flush();
			
			Scanner bsScanner = new Scanner(bsSocket.getInputStream());
			int port = bsScanner.nextInt();
			bsScanner.nextLine();
			String ip = bsScanner.nextLine();
			
			bsSocket.close();

			return new ServentInfo(ip, port);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public void run() {
		ServentInfo someServent = getSomeServentFromSystem();
		
		if (someServent == null) {
			AppConfig.timestampedErrorPrint("Error in contacting bootstrap. Exiting...");
			System.exit(0);
		}
		if (someServent.getListenerPort() == -1) { //bootstrap gave us -1 -> we are first
			AppConfig.activeNodes.add(AppConfig.myServentInfo);
			AppConfig.timestampedStandardPrint("First node in Chaos system.");
		} else { //bootstrap gave us something else - let that node know that we are here

			NewNodeMessage nnm = new NewNodeMessage(AppConfig.myServentInfo.getListenerPort(), someServent.getListenerPort(), AppConfig.myServentInfo.getIpAddress(), someServent.getIpAddress());
			MessageUtil.sendMessage(nnm);
		}
	}

}
