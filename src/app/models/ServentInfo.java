package app.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

	public ServentInfo(String ipAddress, int listenerPort) {
		this.ipAddress = ipAddress;
		this.listenerPort = listenerPort;

		this.jobs = new ArrayList<>();
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

	@Override
	public String toString() {
		return "[" + ipAddress + "|" + listenerPort + "]";
	}

}
