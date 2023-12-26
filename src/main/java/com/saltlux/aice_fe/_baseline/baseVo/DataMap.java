package com.saltlux.aice_fe._baseline.baseVo;

import org.apache.commons.collections4.map.ListOrderedMap;

import com.saltlux.aice_fe._baseline.commons.Common;

/**
 * DB Mapper resultType<br/>
 * 대문자로 변환
 *
 */
public class DataMap extends ListOrderedMap {

	private static final long serialVersionUID = -7554281736198210813L;

	@Override
	public Object put(Object key, Object value) {
		String str = (String) key;
		return super.put(Common.convertCamelCase(str), value);
	}
}
