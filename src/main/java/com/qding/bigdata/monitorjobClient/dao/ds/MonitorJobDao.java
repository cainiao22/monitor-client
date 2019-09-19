package com.qding.bigdata.monitorjobClient.dao.ds;

import java.util.List;

import com.qding.bigdata.monitorjobClient.dao.ds.BaseDao;
import com.qding.bigdata.monitorjobClient.model.MonitorJob;

public interface MonitorJobDao extends BaseDao<MonitorJob> {

	List<MonitorJob> listScheduledMonitorJob(MonitorJob monitorJob);
	MonitorJob getMonitorJobById(String monitorJobId);
	List<MonitorJob> getEnabledMonitorJobByTableId(String tableId);
}
