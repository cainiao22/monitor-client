package com.qding.bigdata.monitorjobClient.model;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

@Data
public class BaseModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8914377902078393304L;
	private Date createTime;
	private Date updateTime;

}
