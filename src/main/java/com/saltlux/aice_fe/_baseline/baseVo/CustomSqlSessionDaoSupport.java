package com.saltlux.aice_fe._baseline.baseVo;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.executor.result.DefaultResultContext;
import org.mybatis.spring.support.SqlSessionDaoSupport;


public class CustomSqlSessionDaoSupport extends SqlSessionDaoSupport {

	
	public <K, V> Map<K, V> selectMapList(List list, String mapKey) {
		return selectMapList(list, mapKey, 0);
	}
	
	public <K, V> Map<K, V> selectMapList(List list, String mapKey, int type) {
		MapListResultHandler mapResultHandler = new MapListResultHandler(
				mapKey, getSqlSession().getConfiguration().getObjectFactory(),
				getSqlSession().getConfiguration().getObjectWrapperFactory(), 
				getSqlSession().getConfiguration().getReflectorFactory());

		DefaultResultContext context = new DefaultResultContext();
		for (Iterator i$ = list.iterator(); i$.hasNext();) {
			Object o = i$.next();
			context.nextResultObject(o);
			mapResultHandler.handleResult(context, type);
		}
		Map selectedMap = mapResultHandler.getMappedResults();
		return selectedMap;
	}
	
	public <K, V> Map<K, V> selectMap(Map map, String mapKey) {
		return selectMap(map, mapKey);
	}
}
