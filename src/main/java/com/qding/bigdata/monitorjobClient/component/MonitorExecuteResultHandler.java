package com.qding.bigdata.monitorjobClient.component;

import com.qding.bigdata.monitorjobClient.common.MonitorJobStatus;
import com.qding.bigdata.monitorjobClient.dao.ds.MonitorExecutorResultDao;
import com.qding.bigdata.monitorjobClient.dao.ds.MonitorJobDao;
import com.qding.bigdata.monitorjobClient.dao.dw.MonitorJobExecuteDao;
import com.qding.bigdata.monitorjobClient.dao.ds.MonitorJobQueueDao;
import com.qding.bigdata.monitorjobClient.model.MonitorExecuteResult;
import com.qding.bigdata.monitorjobClient.model.MonitorJob;
import com.qding.bigdata.monitorjobClient.model.MonitorJobQueue;
import com.qding.bigdata.monitorjobClient.utils.SynchronizedJobRunner;
import com.qding.bigdata.monitorjobClient.utils.UUIDUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by yanpf on 2017/7/19.
 */
@Component
public class MonitorExecuteResultHandler {

    @Autowired
    MonitorExecutorResultDao monitorExecutorResultDao;

    @Autowired
    MonitorJobExecuteDao monitorJobExecuteDao;

    @Autowired
    MonitorJobQueueDao monitorJobQueueDao;

    @Autowired
    MonitorJobDao monitorJobDao;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    public void createOrUpdateExecuteJobResultStatus(final MonitorJobQueue monitorJobQueue) {
        final MonitorJob monitorJob = monitorJobDao.getMonitorJobById(monitorJobQueue.getMonitorJobId());
        if (monitorJob == null) {
            return;
        }
        final String lock = "job_status_update_" + monitorJob.getMetaTableId();
        new SynchronizedJobRunner(lock, new SynchronizedJobRunner.SynchronizedJob() {
            @Override
            public void run() {
                String currentStatus = getMonitorExecutorJobStatus(monitorJobQueue, monitorJob.getMetaTableId());
                MonitorExecuteResult param = new MonitorExecuteResult();
                param.setDataDate(monitorJobQueue.getDataDate());
                param.setMetaTableId(monitorJob.getMetaTableId());

                MonitorExecuteResult monitorExecuteResult = null;
                List<MonitorExecuteResult> monitorExecuteResultList = monitorExecutorResultDao.query(param);
                if (monitorExecuteResultList == null || monitorExecuteResultList.size() == 0) {
                    logger.info("创建新的job记录");
                    monitorExecuteResult = new MonitorExecuteResult();
                    monitorExecuteResult.setId(UUIDUtil.createId());
                    monitorExecuteResult.setCurrentStatus(currentStatus);
                    monitorExecuteResult.setMetaTableId(monitorJob.getMetaTableId());
                    monitorExecuteResult.setScheduleTime(monitorJobQueue.getScheduleTime());
                    monitorExecuteResult.setDataDate(monitorJobQueue.getDataDate());
                    monitorExecuteResult.setCreateTime(new Date());
                    monitorExecuteResult.setUpdateTime(new Date());
                    monitorExecutorResultDao.save(monitorExecuteResult);
                } else {
                    logger.info("更新job记录");
                    monitorExecuteResult = monitorExecuteResultList.get(0);
                    monitorExecuteResult.setCurrentStatus(currentStatus);
                    monitorExecuteResult.setUpdateTime(new Date());
                    monitorExecutorResultDao.updateById(monitorExecuteResult);
                }
                monitorJobQueue.setExecuteResultId(monitorExecuteResult.getId());
                monitorJobQueueDao.updateExecutorResultId(monitorJobQueue);

                logger.info("job状态修改成功");
            }
        }).execute();
    }

    /**
     * 在这里计算job的执行状态
     *
     * @return
     */
    private String getMonitorExecutorJobStatus(MonitorJobQueue monitorJob, String metaTableId) {
        //这里要保证拿取list的时候是按照时间从大到小拿的
        MonitorJobQueue param = new MonitorJobQueue();
        param.setDataDate(monitorJob.getDataDate());
        param.setTableName(monitorJob.getTableName());
        param.setType(monitorJob.getType());
        param.setDataDate(monitorJob.getDataDate());
        param.setMetaTableId(metaTableId);
        List<MonitorJobQueue> monitorJobQueueList = monitorJobQueueDao.queryByCondition(param);
        List<MonitorJob> monitorJobList = monitorJobDao.getEnabledMonitorJobByTableId(metaTableId);
        //int lastVersion = 0;
        Map<String, List<MonitorJobQueue>> monitorJobQueueMap = new HashMap<>();
        for (MonitorJobQueue monitorJobQueue : monitorJobQueueList) {
            List<MonitorJobQueue> list = monitorJobQueueMap.get(monitorJobQueue.getMonitorJobId());
            if (list == null) {
                list = new ArrayList<>();
                monitorJobQueueMap.put(monitorJobQueue.getMonitorJobId(), list);
            }
            monitorJobQueueMap.get(monitorJobQueue.getMonitorJobId()).add(monitorJobQueue);
           /* if (list.size() > lastVersion) {
                lastVersion = list.size();
            }*/
        }
        int successCount = 0;
        int pendingCount = 0;
        for (List<MonitorJobQueue> list : monitorJobQueueMap.values()) {
            /*if (list.size() < lastVersion) {
                continue;
            }*/
            //以最新的为准
            String status = list.get(0).getStatus();
            if (MonitorJobStatus.Failed.name().equals(status)) {
                return MonitorJobStatus.Failed.name();
            } else if (MonitorJobStatus.Success.name().equals(status)) {
                successCount += 1;
            } else if (MonitorJobStatus.Pending.name().equals(status)) {
                pendingCount += 1;
            }
        }
        if (monitorJobList.size() == pendingCount) {
            return MonitorJobStatus.Pending.name();
        } else if (monitorJobList.size() == successCount) {
            return MonitorJobStatus.Success.name();
        } else {
            return MonitorJobStatus.Running.name();
        }
    }
}
