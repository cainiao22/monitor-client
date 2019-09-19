package com.qding.bigdata.monitorjobClient.dao.mysql;

import org.apache.ibatis.annotations.Param;

public interface MysqlMonitorJobExecuteDao {
	Double execute(@Param("sql") String sql);
}
