package org.irri.iric.portal.aws;

public class AWSTimer {

	private String instanceId;
	private boolean init;
	private AWSInstanceCountdown timer;

	public AWSTimer(String instanceId) {
		this.instanceId = instanceId;
		this.init = true;
	}

	public void start() {
		if (init) {
			System.out.println("Starting Galaxy");
			timer = new AWSInstanceCountdown(instanceId);
			init = false;
		}
		timer.start();
	}

	public String getPublicIPaddress() {
		return timer.getPublicIPaddress();
	}

	public String getInstanceState() {
		return timer.getInstanceState();

	}

	public void stopInstance() {
		timer.stopInstance();

	}

}
