package com.qding.bigdata.monitorjobClient.model;

import lombok.Data;

@Data
public class EtlJob extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2126807000966151639L;
	private String id;
	private String name;
	private String alias;
	private String dbType;
	private String type;
	private String owner;
	private int enableEtl;
	private String etlJobType;
	private String etlJobSql;
	private Integer etlJobScheduleHour;
	private Integer etlJobScheduleMinute;
	private String dateDate;
	private Integer priority;
}
