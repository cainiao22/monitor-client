package com.qding.bigdata.monitorjobClient.schedule;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.qding.bigdata.monitorjobClient.component.MonitorJobExecuteLogHandler;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.qding.bigdata.monitorjobClient.common.Constant;
import com.qding.bigdata.monitorjobClient.common.MonitorJobStatus;
import com.qding.bigdata.monitorjobClient.dao.ds.EtlJobDao;
import com.qding.bigdata.monitorjobClient.dao.ds.MonitorJobDao;
import com.qding.bigdata.monitorjobClient.dao.ds.MonitorJobQueueDao;
import com.qding.bigdata.monitorjobClient.model.EtlJob;
import com.qding.bigdata.monitorjobClient.model.MonitorJob;
import com.qding.bigdata.monitorjobClient.model.MonitorJobQueue;
import com.qding.bigdata.monitorjobClient.utils.DateUtil;
import com.qding.bigdata.monitorjobClient.utils.SqlUtil;
import com.qding.bigdata.monitorjobClient.utils.UUIDUtil;

@Component
public class MonitorScheduleQueueJob {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private EtlJobDao etlJobDao;
	@Autowired
	private MonitorJobDao monitorJobDao;
	@Autowired
	private MonitorJobQueueDao monitorJobQueueDao;
	@Autowired
	private MonitorJobExecuteLogHandler monitorJobExecuteLogHandler;

	@Scheduled(cron = "0 * * * * ?")
	public void cronJob() {
		if(!Constant.isMaster()){
			return;
		}
		Calendar ca = Calendar.getInstance();
		logger.info("[CronJob Execute]" + DateUtil.formatDateToFullString2(ca.getTime()));
		MonitorJob monitorJobParam = new MonitorJob();
		monitorJobParam.setMonitorJobScheduleHour(ca.get(Calendar.HOUR_OF_DAY));
		monitorJobParam.setMonitorJobScheduleMinute(ca.get(Calendar.MINUTE));
		logger.info("monitorJobParam:{}", monitorJobParam);
		List<MonitorJob> scheduledMonitorJobList = monitorJobDao.listScheduledMonitorJob(monitorJobParam);
		logger.info("scheduledMonitorJobList size:{}", scheduledMonitorJobList.size());
		for (MonitorJob monitorJob : scheduledMonitorJobList) {
			addMonitorJobQueue(monitorJob, ca);
		}
	}

	private void addMonitorJobQueue(MonitorJob monitorJob, Calendar ca) {
		MonitorJobQueue queue = new MonitorJobQueue();
		queue.setId(UUIDUtil.createId());
		queue.setMonitorJobId(monitorJob.getId());
		queue.setMetaTableId(monitorJob.getMetaTableId());
		
		//获取表名
		EtlJob etlJobParam = new EtlJob();
		etlJobParam.setId(monitorJob.getMetaTableId());
		EtlJob etlJob = etlJobDao.getById(etlJobParam);
		if(null != etlJob && !Constant.POSTGRESQL.equals(etlJob.getDbType())){
			queue.setDbType(etlJob.getDbType());
			queue.setType(etlJob.getType());
			queue.setTableName(etlJob.getName());
			queue.setTableAlias(etlJob.getAlias());
		}else{ //如果没有找到这张表或者是GP表，那么这个规则就作废，不再加入
			return;
		}
		
		queue.setCreateTime(ca.getTime());
		queue.setScheduleTime(ca.getTime());
		//获取前一天
		ca.set(Calendar.DATE, ca.get(Calendar.DATE) - 1);
		queue.setDataDate(ca.getTime());
		ca.set(Calendar.DATE, ca.get(Calendar.DATE) + 1);

		queue.setStatus(MonitorJobStatus.Pending.name());
		queue.setPriority(monitorJob.getPriority());
		queue.setMetaTableId(monitorJob.getMetaTableId());
		queue.setSql(SqlUtil.parse(monitorJob.getMonitorSql(), queue.getScheduleTime()));
		queue.setJobUniqueId(DigestUtils.md5Hex(queue.getMonitorJobId() + queue.getSql()));
		queue.setHashCode(Math.abs(queue.getId().hashCode() % 100));
		if (monitorJobQueueDao.checkJobInQueue(queue) == 0) {
			monitorJobQueueDao.addMonitorJobQueue(queue);
			monitorJobExecuteLogHandler.updateJobStatus(queue, MonitorJobStatus.Pending,null, null, true);
		} 
	}
}
