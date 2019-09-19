package com.qding.bigdata.monitorjobClient.component;

import com.qding.bigdata.monitorjobClient.common.Constant;
import com.qding.bigdata.monitorjobClient.common.MonitorJobStatus;
import com.qding.bigdata.monitorjobClient.config.WechatAlarmConfig;
import com.qding.bigdata.monitorjobClient.dao.ds.EtlJobDao;
import com.qding.bigdata.monitorjobClient.dao.ds.MonitorJobDao;
import com.qding.bigdata.monitorjobClient.dao.ds.MonitorJobExecuteLogDao;
import com.qding.bigdata.monitorjobClient.dao.ds.MonitorJobQueueDao;
import com.qding.bigdata.monitorjobClient.model.EtlJob;
import com.qding.bigdata.monitorjobClient.model.MonitorJob;
import com.qding.bigdata.monitorjobClient.model.MonitorJobExecuteLog;
import com.qding.bigdata.monitorjobClient.model.MonitorJobQueue;
import com.qding.bigdata.monitorjobClient.utils.HttpClientUtils;
import com.qding.bigdata.monitorjobClient.utils.UUIDUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class MonitorJobExecuteLogHandler {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private MonitorJobDao monitorJobDao;
    @Autowired
    private EtlJobDao etlJobDao;
    @Autowired
    private MonitorJobExecuteLogDao monitorJobExecuteLogDao;
    @Autowired
    private MonitorJobQueueDao monitorJobQueueDao;

    @Autowired
    WechatAlarmConfig wechatAlarmConfig;

    public void addLog(String monitorQueueId, String monitorLog) {
        MonitorJobExecuteLog monitorJobExecuteLog = new MonitorJobExecuteLog(UUIDUtil.createId(), monitorQueueId, monitorLog);
        monitorJobExecuteLog.setCreateTime(new Date());
        monitorJobExecuteLogDao.addLog(monitorJobExecuteLog);
    }

    public void updateJobStatus(MonitorJobQueue monitorJobQueue, MonitorJobStatus monitorJobStatus,
                                MonitorJob monitorJob, Double realValue, boolean addLog) {
        monitorJobQueue.setStatus(monitorJobStatus.name());
        monitorJobQueue.setUpdateTime(new Date());
        monitorJobQueueDao.updateJobStatus(monitorJobQueue);

        if (addLog) {
            //检验失败情况处理
            if (monitorJobStatus.name().equals(MonitorJobStatus.Failed.name())) {
                this.addLog(monitorJobQueue.getId(), "作业状态变为:" + monitorJobStatus.name() + ",监控失败:报警阀值【" + monitorJob.getValueMin() + "," + monitorJob.getValueMax() + "】,实际结果【" + realValue + "】");
            } else {
                this.addLog(monitorJobQueue.getId(), "作业状态变为:" + monitorJobStatus.name() + (realValue == null ? "" : " 结果:" + realValue));
            }
        }
    }

    public void wechatAlarm(MonitorJobQueue queue, String errorMsg){
        String msg = "表名称:%s.%s, 监控规则:%s, 错误信息:%s, 负责人:%s";
       try {
           String tableName = queue.getTableName();
           MonitorJob monitorJob = monitorJobDao.getMonitorJobById(queue.getMonitorJobId());
           EtlJob param = new EtlJob();
           param.setId(monitorJob.getMetaTableId());
           EtlJob etlJob = etlJobDao.getById(param);
           msg = String.format(msg, queue.getType(),tableName, monitorJob.getMonitorName(), errorMsg, etlJob.getOwner());
           logger.info("发送微信报警:" + msg);
           Map<String, String> params = new HashMap<>();
           params.put("mobiles", getMobiles(etlJob.getOwner()));
           params.put("title", "监控报表");
           params.put("content", msg);
           HttpClientUtils.doPost(Constant.ALARM_WECHAT, params);
       }catch (Exception e){
           logger.error("微信报警发送失败:{}", e);
           e.printStackTrace();
       }
    }

    public String getMobiles(String ownerStr){
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : wechatAlarmConfig.getCommon().entrySet()) {
            sb.append(entry.getValue()).append("|");
        }
        boolean unknow = true;
        if(StringUtils.isNotBlank(ownerStr)) {
            final String[] owners = ownerStr.split(",");
            for (String owner : owners) {
                if (wechatAlarmConfig.getOwners().containsKey(owner) || wechatAlarmConfig.getCommon().containsKey(owner)) {
                    unknow = false;
                    if(!wechatAlarmConfig.getCommon().containsKey(owner)) {
                        sb.append(wechatAlarmConfig.getOwners().get(owner)).append("|");
                    }
                }
            }
        }

        if (unknow){
            for (Map.Entry<String, String> entry : wechatAlarmConfig.getOwners().entrySet()) {
                if(!wechatAlarmConfig.getCommon().containsKey(entry.getKey())) {
                    sb.append(entry.getValue()).append("|");
                }
            }
        }

        sb.setLength(sb.length() - 1);
        return sb.toString();
    }

}
