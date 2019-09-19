package com.qding.bigdata.monitorjobClient.common;

public enum MonitorJobStatus {

	Pending("Pending"),	Running("Running"),Success("Success"),Failed("Failed");
	private String name;

	MonitorJobStatus(String name) {
		this.name = name;
	}
	
}
