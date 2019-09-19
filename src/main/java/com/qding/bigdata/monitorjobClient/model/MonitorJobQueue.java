package com.qding.bigdata.monitorjobClient.model;

import java.util.Date;

import lombok.Data;

@Data
public class MonitorJobQueue extends BaseModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8487315414612013859L;
	/**
	 * 
	 */
	private String id;
	private String executeResultId;
	private String metaTableId;
	private String dbType;
	private String type;
	private String tableName;	
	private String tableAlias;
	private String sql;
	private Date scheduleTime;
	private String jobUniqueId;
	private String status;
	private String monitorJobId;
	private Integer priority;
	private Date dataDate;
	private Integer hashCode;

}
