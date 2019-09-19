package com.qding.bigdata.monitorjobClient.dao.dw;

import org.apache.ibatis.annotations.Param;

public interface MonitorJobExecuteDao {
	Double execute(@Param("sql")String sql);
}
