package com.qding.bigdata.monitorjobClient.model;

import lombok.Data;

@Data
public class MonitorJobExecuteLog extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2102337816305668215L;
	private String id;
	private String monitorQueueId;
	private String monitorLog;
	
	public MonitorJobExecuteLog(String id, String monitorQueueId, String monitorLog) {
		super();
		this.id = id;
		this.monitorQueueId = monitorQueueId;
		this.monitorLog = monitorLog;
	}
	
	
}
