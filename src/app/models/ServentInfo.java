package app.models;

import java.io.Serializable;

/**
 * This is an immutable class that holds all the information for a servent.
 *
 * @author bmilojkovic
 */
public class ServentInfo implements Serializable {

	private static final long serialVersionUID = 5304170042791281555L;
	private final String ipAddress;
	private final int listenerPort;

	public ServentInfo(String ipAddress, int listenerPort) {
		this.ipAddress = ipAddress;
		this.listenerPort = listenerPort;
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public int getListenerPort() {
		return listenerPort;
	}

	@Override
	public String toString() {
		return "[" + ipAddress + "|" + listenerPort + "]";
	}

}
