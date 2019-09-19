package com.qding.bigdata.monitorjobClient.dao.hive;

import org.apache.ibatis.annotations.Param;

public interface HiveMonitorJobExecuteDao {
	Double execute(@Param("sql") String sql);
}
