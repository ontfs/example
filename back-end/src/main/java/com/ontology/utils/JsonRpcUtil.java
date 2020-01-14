package com.ontology.utils;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;


public class JsonRpcUtil {
private static final Logger log = LoggerFactory.getLogger(JsonRpcUtil.class);
	
	public static final String RPC_ACTION = "method";
	/**
	 * 单例模式
	 */
	private static JsonRpcUtil rpcUtil=new JsonRpcUtil();
	
	/**获得工具实例
	 * @return
	 */
	public static JsonRpcUtil getRpcUtil() {
		return rpcUtil;
	}

	private JsonRpcHttpClient rpcClient;

	/**设置headper
	 * @param url
	 * @param headers
	 * @return
	 * @throws MalformedURLException
	 */
	public JsonRpcUtil setHeaders(String url, Map<String, String> headers) throws MalformedURLException {
		rpcClient = new JsonRpcHttpClient(new URL(url));
		if (null != headers && headers.size() > 0) 
		{
			rpcClient.setHeaders(headers);
		} 
		return this;
	}

	/**远程调用
	 * @param action
	 * @param argument
	 * @param returnClass
	 * @return
	 * @throws Throwable
	 */
	public <T> T execute(String action, Object argument, Class<T> returnClass) throws Throwable {
		return this.rpcClient.invoke(action, argument, returnClass);
	}
	
	/**
	 * 远程过程调用
	 * 
	 * @param url 接口url
	 * @param headers 要加入的header
	 * @param argument 此处为浏览器控制台里参数里的params的值
	 * @param returnClass 返回值类型
	 * @return
	 * @throws MalformedURLException
	 * @throws Throwable
	 */
	public static <T> T rpcExecute(String method,String url, Map<String, String> headers, Object argument, Class<T> returnClass)
			throws MalformedURLException, Throwable {
		try {
//			return JsonRpcUtil.getRpcUtil().setHeaders(url, headers).execute(RPC_ACTION, argument, returnClass);
			JsonRpcUtil util=JsonRpcUtil.getRpcUtil().setHeaders(url, headers);
			return util.execute(method, argument, returnClass);
			
		} finally {
			log.debug("api url:" + url);
			log.debug("api header:" + JsonUtil.ParseJson(headers));
			log.debug("api params:" + JsonUtil.ParseJson(argument));
			//this.clearParas();
		}
	}
	
}

