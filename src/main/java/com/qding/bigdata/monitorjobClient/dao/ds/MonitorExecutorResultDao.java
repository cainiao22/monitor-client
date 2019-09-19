package com.qding.bigdata.monitorjobClient.dao.ds;


import com.qding.bigdata.monitorjobClient.model.MonitorExecuteResult;

import java.util.List;

/**
 * Created by yanpf on 2017/7/19.
 */
public interface MonitorExecutorResultDao {

    List<MonitorExecuteResult> query(MonitorExecuteResult param);

    void updateById(MonitorExecuteResult monitorExecuteResult);

    void save(MonitorExecuteResult monitorExecuteResult);
}
