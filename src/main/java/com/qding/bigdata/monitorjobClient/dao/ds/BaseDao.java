
package com.qding.bigdata.monitorjobClient.dao.ds;

import java.util.List;

import com.qding.bigdata.monitorjobClient.model.BaseModel;

public interface BaseDao<T extends BaseModel> {
	List<T> list(T t);
	void save(T t);
	T getById(T t);

}
