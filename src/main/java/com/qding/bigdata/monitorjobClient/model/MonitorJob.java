package com.qding.bigdata.monitorjobClient.model;

import lombok.Data;

@Data
public class MonitorJob extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6784972064865731929L;
	private String id;
	private String metaTableId;
	private String monitorSql;
	private Double valueMin;
	private Double valueMax;
	private int enableMonitor;
	private Integer monitorJobScheduleHour;
	private Integer monitorJobScheduleMinute;
	private Integer priority;
	private String monitorName;
}
