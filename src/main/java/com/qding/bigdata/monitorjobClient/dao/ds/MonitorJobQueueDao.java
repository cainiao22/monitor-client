package com.qding.bigdata.monitorjobClient.dao.ds;

import java.util.List;

import com.qding.bigdata.monitorjobClient.dao.ds.BaseDao;
import com.qding.bigdata.monitorjobClient.model.MonitorJobQueue;
import org.apache.ibatis.annotations.Param;

public interface MonitorJobQueueDao extends BaseDao< MonitorJobQueue> {

	void addMonitorJobQueue(MonitorJobQueue queue);

	int checkJobInQueue(MonitorJobQueue queue);

	List<MonitorJobQueue> listPendingQueue(@Param("currentIndex") int currentIndex, @Param("totalCount") int totalCount);

	void updateJobStatus(MonitorJobQueue monitorJobQueue);

	List<MonitorJobQueue> queryByCondition(MonitorJobQueue monitorJobQueue);

	void updateExecutorResultId(MonitorJobQueue monitorJobQueue);
}
