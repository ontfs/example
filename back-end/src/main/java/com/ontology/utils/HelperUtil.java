package com.ontology.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.crypto.Digest;
import com.ontology.exception.OntFsException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.config.SocketConfig;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultConnectionKeepAliveStrategy;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zhouq
 * @date 2018/02/27
 */
@Component
@Slf4j
public class HelperUtil {

    private static HttpClient httpClient;

    static {
        //HttpClient4.5版本后的参数设置
        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom();
        //客户端和服务器建立连接的timeout
        requestConfigBuilder.setConnectTimeout(30000);
        //从连接池获取连接的timeout
        requestConfigBuilder.setConnectionRequestTimeout(30000);
        //连接建立后，request没有回应的timeout。
        requestConfigBuilder.setSocketTimeout(60000);

        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        clientBuilder.setDefaultRequestConfig(requestConfigBuilder.build());
        //连接建立后，request没有回应的timeout
        clientBuilder.setDefaultSocketConfig(SocketConfig.custom().setSoTimeout(60000).build());
        clientBuilder.setKeepAliveStrategy(new DefaultConnectionKeepAliveStrategy());

        PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
        cm.setMaxTotal(200);
        cm.setDefaultMaxPerRoute(30);
        httpClient = clientBuilder.setConnectionManager(cm).build();
    }

    /**
     * check the param whether is null or ''
     *
     * @param params
     * @return boolean
     */
    public static Boolean isEmptyOrNull(Object... params) {
        if (params != null) {
            for (Object val : params) {
                if ("".equals(val) || val == null) {
                    return true;
                }
            }
            return false;
        }
        return true;
    }


    /**
     * merge byte[] head and byte[] tail ->byte[head+tail] rs
     *
     * @param head
     * @param tail
     * @return byte[]
     */
    public static byte[] byteMerrage(byte[] head, byte[] tail) {
        byte[] temp = new byte[head.length + tail.length];
        System.arraycopy(head, 0, temp, 0, head.length);
        System.arraycopy(tail, 0, temp, head.length, tail.length);
        return temp;
    }


    /**
     * judge whether the string is in json format.
     *
     * @param str
     * @return
     */
    public static Boolean isJSONStr(String str) {
        try {
            JSONObject obj = JSONObject.parseObject(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 日期格式字符串转换成时间戳
     *
     * @param dateStr 字符串日期
     * @param format  如：yyyy-MM-dd HH:mm:ss
     * @return
     */
    public static long Date2TimeStamp(String dateStr, String format) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.parse(dateStr).getTime() / 1000L;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * get current method name
     *
     * @return
     */
    public static String currentMethod() {
        return new Exception("").getStackTrace()[1].getMethodName();
    }


    //length用户要求产生字符串的长度
    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }

    public static String httpClientPost(String url, String reqBodyStr, Map<String, Object> headerMap) throws Exception {

        String responseStr = "";

        StringEntity stringEntity = new StringEntity(reqBodyStr, Charset.forName("UTF-8"));
        stringEntity.setContentType(ContentType.APPLICATION_JSON.getMimeType());

        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(stringEntity);
        //设置请求头
        for (Map.Entry<String, Object> entry :
                headerMap.entrySet()) {
            httpPost.setHeader(entry.getKey(), entry.getValue().toString());
        }

        CloseableHttpResponse response = null;
        try {
            response = (CloseableHttpResponse) httpClient.execute(httpPost);
            HttpEntity httpEntity = response.getEntity();
            responseStr = EntityUtils.toString(httpEntity);
        } catch (IOException e) {
            log.error("{} error...", HelperUtil.currentMethod(), e);
            throw new OntFsException("callback", ErrorInfo.COMM_NET_FAIL.descCN(), ErrorInfo.COMM_NET_FAIL.descEN(), ErrorInfo.COMM_NET_FAIL.code());
        }
        if (response.getStatusLine().getStatusCode() == 200) {
            log.info("send requestbody:{} to {},response 200:{}", reqBodyStr, url, responseStr);
            return responseStr;
        } else {
            log.error("send requestbody:{} to {},response {}:{}", reqBodyStr, url, response.getStatusLine().getStatusCode(), responseStr);
            throw new OntFsException("callback", ErrorInfo.COMM_NET_FAIL.descCN(), ErrorInfo.COMM_NET_FAIL.descEN(), ErrorInfo.COMM_NET_FAIL.code());
        }
    }


    public static String httpClientGet(String uri, Map<String, Object> paramMap, Map<String, Object> headerMap) throws Exception {

        String responseStr = "";

        CloseableHttpResponse response = null;
        URIBuilder uriBuilder = null;
        try {
            //拼完整的请求url
            uriBuilder = new URIBuilder(uri);
            List<NameValuePair> params = new ArrayList<>();
            for (Map.Entry<String, Object> entry :
                    paramMap.entrySet()) {
                params.add(new BasicNameValuePair(entry.getKey(), entry.getValue().toString()));
            }
            uriBuilder.setParameters(params);

            HttpGet httpGet = new HttpGet(uriBuilder.build());
            //设置请求头
            for (Map.Entry<String, Object> entry :
                    headerMap.entrySet()) {
                httpGet.setHeader(entry.getKey(), entry.getValue().toString());
            }
            response = (CloseableHttpResponse) httpClient.execute(httpGet);
            HttpEntity httpEntity = response.getEntity();
            responseStr = EntityUtils.toString(httpEntity);
        } catch (Exception e) {
            log.error("{} error...", HelperUtil.currentMethod(), e);
            throw new OntFsException("callback", ErrorInfo.COMM_NET_FAIL.descCN(), ErrorInfo.COMM_NET_FAIL.descEN(), ErrorInfo.COMM_NET_FAIL.code());
        }
        if (response.getStatusLine().getStatusCode() == 200) {
            log.info("send to {},response 200:{}", uriBuilder.toString(), responseStr);
            return responseStr;
        } else {
            log.error("send to {},response {}:{}", uriBuilder.toString(), response.getStatusLine().getStatusCode(), responseStr);
            throw new OntFsException("callback", ErrorInfo.COMM_NET_FAIL.descCN(), ErrorInfo.COMM_NET_FAIL.descEN(), ErrorInfo.COMM_NET_FAIL.code());
        }
    }


    /**
     * hash
     */
    public static String sha256(String data) {
        byte[] bytes = Digest.sha256(data.getBytes());
        return com.github.ontio.common.Helper.toHexString(bytes);
    }


    /**
     * 拼接JSON格式参数
     */
    public static String constructParam(String action, String contractHash, String method, List<Map<String, Object>> argsList, String payer, Long gasLimit, Long gasPrice) {
        Map<String, Object> map = new HashMap();
        Map<String, Object> invokeConfig = new HashMap();
        List<Map<String, Object>> functions = new ArrayList();
        Map<String, Object> function = new HashMap();
        function.put("operation", method);
        function.put("args", argsList);
        functions.add(function);
        invokeConfig.put("contractHash", contractHash);
        invokeConfig.put("functions", functions);
        invokeConfig.put("payer", payer);
        invokeConfig.put("gasLimit", gasLimit == null ? 20000L : gasLimit);
        invokeConfig.put("gasPrice", gasPrice == null ? 500L : gasPrice);
        map.put("action", action);
        map.put("params", invokeConfig);
        return JSON.toJSONString(map);
    }
}
