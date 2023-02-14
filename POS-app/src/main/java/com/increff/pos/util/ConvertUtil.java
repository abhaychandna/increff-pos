package com.increff.pos.util;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class ConvertUtil {

	public static <T, R> R convert(T fromClass, Class<R> toClass) {
		ObjectMapper mapper = new ObjectMapper();
		mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		mapper.registerModule(new JavaTimeModule());
		R newObject = mapper.convertValue(fromClass, toClass);
		return newObject;
	}

	public static <T, R> List<R> convert(List<T> fromClass, Class<R> toClass) {
		List<R> newObject = new ArrayList<R>();
		for(T t : fromClass) {
			newObject.add(convert(t, toClass));
		}
		return newObject;
	}


}
