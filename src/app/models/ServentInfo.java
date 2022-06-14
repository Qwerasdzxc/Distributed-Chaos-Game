package app.models;

import app.AppConfig;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * This is an immutable class that holds all the information for a servent.
 *
 * @author bmilojkovic
 */
public class ServentInfo implements Serializable {

	private static final long serialVersionUID = 5304170042791281555L;
	private final String ipAddress;
	private final int listenerPort;
	private final List<Job> jobs;

	private int weakFailureLimit;
	private int strongFailureLimit;

	private final String uuid;

	public ServentInfo(String ipAddress, int listenerPort) {
		this.ipAddress = ipAddress;
		this.listenerPort = listenerPort;

		this.weakFailureLimit = 1000;
		this.strongFailureLimit = 10000;

		this.jobs = new ArrayList<>();
		this.uuid = AppConfig.sha256(ipAddress + ":" + listenerPort);
	}

	public String getIpAddress() {
		return ipAddress;
	}

	public int getListenerPort() {
		return listenerPort;
	}

	public List<Job> getJobs() {
		return jobs;
	}

	public void addJob(Job job) {
		if (jobs.contains(job))
			return;

		jobs.add(job);
	}

	public Job getJob(String name) {
		for (Job job : jobs) {
			if (job.getName().equals(name))
				return job;
		}

		return null;
	}

	public boolean hasJob(String name) {
		for (Job job : jobs) {
			if (job.getName().equals(name))
				return true;
		}

		return false;
	}

	public int getWeakFailureLimit() {
		return weakFailureLimit;
	}

	public int getStrongFailureLimit() {
		return strongFailureLimit;
	}

	public void setWeakFailureLimit(int weakFailureLimit) {
		this.weakFailureLimit = weakFailureLimit;
	}

	public void setStrongFailureLimit(int strongFailureLimit) {
		this.strongFailureLimit = strongFailureLimit;
	}

	public String getUuid() {
		return uuid;
	}

	@Override
	public String toString() {
		return "[" + ipAddress + "|" + listenerPort + "]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		ServentInfo that = (ServentInfo) o;

		return uuid.equals(that.uuid);
	}

	@Override
	public int hashCode() {
		return uuid.hashCode();
	}
}
