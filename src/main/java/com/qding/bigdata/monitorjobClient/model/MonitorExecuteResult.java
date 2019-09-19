package com.qding.bigdata.monitorjobClient.model;


import lombok.Data;

import java.util.Date;

@Data
public class MonitorExecuteResult extends BaseModel {
    private String id;

    private String metaTableId;

    private Date scheduleTime;

    private String currentStatus;

    private Date dataDate;

}