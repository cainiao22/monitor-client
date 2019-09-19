package com.qding.bigdata.monitorjobClient.schedule;

import java.util.Date;
import java.util.List;

import com.qding.bigdata.monitorjobClient.common.Constant;
import com.qding.bigdata.monitorjobClient.component.MonitorExecuteResultHandler;
import com.qding.bigdata.monitorjobClient.component.MonitorJobExecuteLogHandler;
import com.qding.bigdata.monitorjobClient.dao.hive.HiveMonitorJobExecuteDao;
import com.qding.bigdata.monitorjobClient.dao.mysql.MysqlMonitorJobExecuteDao;
import com.qding.bigdata.monitorjobClient.utils.MultiJobRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.qding.bigdata.monitorjobClient.common.MonitorJobStatus;
import com.qding.bigdata.monitorjobClient.dao.ds.MonitorJobDao;
import com.qding.bigdata.monitorjobClient.dao.ds.MonitorJobQueueDao;
import com.qding.bigdata.monitorjobClient.model.MonitorJob;
import com.qding.bigdata.monitorjobClient.model.MonitorJobQueue;

@Component
public class MonitorJobExecute extends MultiJobRunner {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());
	@Autowired
	private MonitorJobDao monitorJobDao;
	@Autowired
	private HiveMonitorJobExecuteDao hiveMonitorJobExecuteDao;
	@Autowired
	private MysqlMonitorJobExecuteDao mysqlMonitorJobExecuteDao;
	@Autowired
	private MonitorJobQueueDao monitorJobQueueDao;
	@Autowired
	private MonitorJobExecuteLogHandler monitorJobExecuteLogHandler;

	@Autowired
	private MonitorExecuteResultHandler monitorExecuteResultHandler;

	@Scheduled(fixedDelay=1000*10,initialDelay=3000)
	public void cronJob() {
		/*if(!Constant.isMaster()){
			return;
		}*/
		execute();
	}


	@Override
	protected void execute() {
		List<MonitorJobQueue> pendingJobList = monitorJobQueueDao.listPendingQueue(getCurrentNodeIndex(), getTotalNodeCount());
		MonitorJob monitorJob;
		Double realValue = null;

		logger.info("execute MonitorJobExecute{}",new Date());
		for (MonitorJobQueue monitorJobQueue : pendingJobList) {
			logger.info("执行监控规则:{}", monitorJobQueue.getTableName());
			MonitorJob monitorJobParam = new MonitorJob();
			monitorJobParam.setId(monitorJobQueue.getMonitorJobId());
			monitorJob = monitorJobDao.getById(monitorJobParam);

			monitorJobExecuteLogHandler.updateJobStatus(monitorJobQueue, MonitorJobStatus.Running,monitorJob,realValue, true);
			try {

				realValue = this.execute(monitorJobQueue);
				//检验值是否合理
				if(realValue<=monitorJob.getValueMax().doubleValue() &&  realValue >=monitorJob.getValueMin().doubleValue()){
					monitorJobExecuteLogHandler.updateJobStatus(monitorJobQueue, MonitorJobStatus.Success,monitorJob,realValue, true);
				}else{
					monitorJobExecuteLogHandler.updateJobStatus(monitorJobQueue, MonitorJobStatus.Failed,monitorJob,realValue, true);
					monitorJobExecuteLogHandler.wechatAlarm(monitorJobQueue, "[实际结果:" + realValue + "; 报警阀值:" + monitorJob.getValueMin() + "~" + monitorJob.getValueMax() + "]");
				}
			} catch (Exception e) {
				logger.error("作业执行异常:{}", e);
				monitorJobExecuteLogHandler.addLog(monitorJobQueue.getId(), "作业执行异常:"+e.getMessage());
				monitorJobExecuteLogHandler.updateJobStatus(monitorJobQueue, MonitorJobStatus.Failed,monitorJob,realValue, false);
				monitorJobExecuteLogHandler.wechatAlarm(monitorJobQueue, e.getMessage());
			}

			try{
				monitorExecuteResultHandler.createOrUpdateExecuteJobResultStatus(monitorJobQueue);
			}catch (Exception e){
				logger.error("更新任务执行状态出现错误, getMonitorJobId:{},{}", monitorJobQueue.getMonitorJobId(), e);
				monitorJobExecuteLogHandler.wechatAlarm(monitorJobQueue,e.getMessage());
			}
		}
	}


	private Double execute(MonitorJobQueue monitorJobQueue) throws Exception {

		if(Constant.MYSQL.equals(monitorJobQueue.getDbType())){
			return mysqlMonitorJobExecuteDao.execute(monitorJobQueue.getSql());
		}else if(Constant.HIVE.equals(monitorJobQueue.getDbType())){
			return hiveMonitorJobExecuteDao.execute(monitorJobQueue.getSql());
		}

		throw new Exception("不支持的数据库类型:" + monitorJobQueue.getDbType());
	}
}
