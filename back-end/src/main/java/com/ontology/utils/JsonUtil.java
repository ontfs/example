package com.ontology.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.parser.Feature;

public class JsonUtil {
	
		public static String ParseJson(Object obj) {
			return JSON.toJSONString(obj);
		}

		public static <T> T parseObject(String json,Class<T> clas) {  
			return (T) JSON.parseObject(json, clas) ;
		}
		
		public static <T> T parseObject(String json, TypeReference<T> type, Feature... features){
			return (T) JSON.parseObject(json, type,features) ;
			
		}
	
}
