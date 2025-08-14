package org.irri.iric.portal.aws;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2ClientBuilder;
import com.amazonaws.services.ec2.model.DescribeInstancesRequest;
import com.amazonaws.services.ec2.model.DescribeInstancesResult;
import com.amazonaws.services.ec2.model.Instance;
import com.amazonaws.services.ec2.model.Reservation;
import com.amazonaws.services.ec2.model.StartInstancesRequest;
import com.amazonaws.services.ec2.model.StartInstancesResult;
import com.amazonaws.services.ec2.model.StopInstancesRequest;
import com.amazonaws.services.ec2.model.StopInstancesResult;
import com.amazonaws.services.elasticmapreduce.model.InstanceState;

public class AWSInstanceCountdown {

	static Timer timer;
	static int interval;

	static int max;
	static String instanceId;
	static AmazonEC2 ec2;
	static String publicIpAddress;

	public AWSInstanceCountdown(String instance_id, int max_time) {
		interval = 0;
		max = max_time;
		instanceId = instance_id;

		ec2 = AmazonEC2ClientBuilder.standard().withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
				.withRegion(Regions.AP_SOUTHEAST_1).build();
		
	}

	public AWSInstanceCountdown(String instance_id) {
		interval = 0;

		max = 86400;

		instanceId = instance_id;

		ec2 = AmazonEC2ClientBuilder.standard().withCredentials(DefaultAWSCredentialsProviderChain.getInstance())
				.withRegion(Regions.AP_SOUTHEAST_1).build();
		
	}

	public void start() {
		if (!getInstanceState().toUpperCase().equals(InstanceState.RUNNING.toString().toUpperCase())) {

			timer = new Timer();
			int delay = 1000;
			int period = 1000;
			timer = new Timer();
			interval = max;
			instantiateIntance();
			timer.scheduleAtFixedRate(new TimerTask() {

				public void run() {
					setInterval();
				}
			}, delay, period);

			try {
				int tries = 1;
				while (!getInstanceState().toUpperCase().equals(InstanceState.RUNNING.toString().toUpperCase()) && tries <= 20) {

					Thread.sleep(5000);
					tries++;

				}
				System.out.println("STARTED INSTANCE: " + instanceId);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			reset();

		}

	}

	public final int setInterval() {
		if (interval == 1) {
			stopInstance();
		}
		return --interval;
	}

	public void stopInstance() {
		System.out.println("Instance Stopping");
		if (getInstanceState().toUpperCase().equals(InstanceState.RUNNING.toString().toUpperCase())) {
			timer.cancel();
			timer = null;
			StopInstancesRequest request = new StopInstancesRequest().withInstanceIds(instanceId);
			StopInstancesResult ins = ec2.stopInstances(request);
			publicIpAddress = null;
		}
	}

//	public String getStatus() {
//		getInstanceState();
////		if (interval == 0)
////			return "TERMINATED";
////		return "RUNNING";
//
//	}

	public void reset() {
		interval = max;
	}

	private void instantiateIntance() {

		if (!getInstanceState().toUpperCase().equals(InstanceState.RUNNING.toString().toUpperCase())) {
			StartInstancesRequest request = new StartInstancesRequest().withInstanceIds(instanceId);
			StartInstancesResult ins = ec2.startInstances(request);

			// insert code for waiting to be instantiated
		}

	}

	public String getInstanceState() {

		String status = "";

		List<String> instances = new ArrayList<String>();

		instances.add(instanceId);

		DescribeInstancesRequest descRequest = new DescribeInstancesRequest();
		descRequest.setInstanceIds(instances);

		DescribeInstancesResult response = ec2.describeInstances(descRequest);

		for (Reservation reservation : response.getReservations()) {
			for (Instance instance : reservation.getInstances()) {
				status = instance.getState().getName().toUpperCase();
				if (status.equals(InstanceState.RUNNING.toString().toUpperCase())) {
					publicIpAddress = instance.getPublicIpAddress();
					System.out.println("RUNNING IP ADDRESS : " + publicIpAddress);
				}

			}
		}

		return status;

	}

	public String getPublicIPaddress() {
		System.out.println("Retrieving Public IP_ADDREE" + publicIpAddress);
		return publicIpAddress;
	}

}
