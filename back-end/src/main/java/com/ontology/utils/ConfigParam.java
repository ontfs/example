package com.ontology.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service("ConfigParam")
public class ConfigParam {

	/**
	 *  SDK参数
	 */
	@Value("${service.restfulUrl}")
	public String RESTFUL_URL;

	@Value("${fs.client.url}")
	public String FS_CLIENT_URL;

	@Value("${payer.addr}")
	public String PAYER_ADDRESS;

	@Value("${payer.wif}")
	public String PAYER_WIF;

	@Value("${ontid.wif.no1}")
	public String ONTID_WIF_NO1;

	@Value("${ontid.wif.no2}")
	public String ONTID_WIF_NO2;

	/**
	 *  jwt颁发者
	 */
	@Value("${jwt.issuer.ontid}")
	public String JWT_ISSUER_ONTID;
	@Value("${jwt.issuer.publickey}")
	public String JWT_ISSUER_PUBLICKEY;
	@Value("${jwt.issuer.privatekey}")
	public String JWT_ISSUER_PRIVATEKEY;

	@Value("${base.file.path}")
	public String BASE_FILE_PATH;

	@Value("${download.url}")
	public String DOWNLOAD_URL;

	@Value("${ontfs.first.pdp}")
	public Boolean ONTFS_FIRST_PDP;
}