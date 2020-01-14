package com.ontology.utils;

/**
 * 常量
 */
public class Constant {

    public static final String SPLIT = "/";

    /**
     * sdk手续费
     */
    public static final long GAS_PRICE = 0;
    public static final long GAS_LIMIT = 50000;

    /**
     * Token 类型
     */
    public static final String ACCESS_TOKEN = "access_token";

    /**
     * 在signing-server注册的action类型
     */
    public static final String METHOD_UPLOAD = "uploadfile";
    public static final String METHOD_DOWNLOAD = "downloadfile";

    /**
     * token 过期时间
     */
    public static final long REFRESH_TOKEN_EXPIRE = 7 * 24 * 60 * 60 * 1000;
    public static final long ACCESS_TOKEN_EXPIRE = 24 * 60 * 60 * 1000;

    public static final long FILE_EXPIRE = 365 * 24 * 60 * 60 * 1000L;
}
