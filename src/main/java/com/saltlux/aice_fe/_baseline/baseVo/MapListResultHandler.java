package com.saltlux.aice_fe._baseline.baseVo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.ReflectorFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.session.ResultContext;
import org.apache.ibatis.session.ResultHandler;

import com.saltlux.aice_fe._baseline.commons.Common;


public class MapListResultHandler<K, V> implements ResultHandler {
	private final Map<Object, List<Object>> mappedResults;
	private final String mapKey;
	private final ObjectFactory objectFactory;
	private final ObjectWrapperFactory objectWrapperFactory;
	private final ReflectorFactory reflectorFactory;

	public MapListResultHandler(String mapKey, ObjectFactory objectFactory,
			ObjectWrapperFactory objectWrapperFactory, ReflectorFactory reflectorFactory) {
		this.objectFactory = objectFactory;
		this.objectWrapperFactory = objectWrapperFactory;
		this.reflectorFactory = reflectorFactory;
		this.mappedResults = ((Map) objectFactory.create(Map.class));
		this.mapKey = mapKey;
	}

	public void handleResult(ResultContext context) {
		handleResult(context, 0);
	}

	public void handleResult(ResultContext context, int type) {
		Object value = context.getResultObject();
		MetaObject mo = MetaObject.forObject(value, this.objectFactory,	this.objectWrapperFactory, this.reflectorFactory);

		Object key = mo.getValue(this.mapKey);
		if(type == 1) key = Common.parseInt(key);
		List<Object> valList = null;
		if(value != null) {
			valList = this.mappedResults.get(key);
			if(valList == null) valList = new ArrayList<Object>();
			if(value instanceof DataMap) {
				((DataMap)value).remove(this.mapKey);
			}
			valList.add(value);
		}
		this.mappedResults.put(key, valList);
	}

	public Map<Object, List<Object>> getMappedResults() {
		return this.mappedResults;
	}

}