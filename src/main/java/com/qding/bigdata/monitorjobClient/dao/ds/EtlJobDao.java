package com.qding.bigdata.monitorjobClient.dao.ds;

import java.util.List;

import com.qding.bigdata.monitorjobClient.dao.ds.BaseDao;
import com.qding.bigdata.monitorjobClient.model.EtlJob;

public interface EtlJobDao extends BaseDao<EtlJob> {

	List<EtlJob> listScheduledEtlJob(EtlJob etlJob);
}
