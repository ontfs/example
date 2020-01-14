package com.ontology.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.ontio.common.Helper;
import com.ontology.controller.vo.*;
import com.ontology.entity.OntFile;
import com.ontology.exception.OntFsException;
import com.ontology.mapper.OntFileMapper;
import com.ontology.secure.AESService;
import com.ontology.service.DataService;
import com.ontology.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.*;

@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class DataServiceImpl implements DataService {
    @Autowired
    private ConfigParam configParam;
    @Autowired
    private OntFileMapper ontFileMapper;

    @Override
    public List<OntFile> getFile(String action, String ontId) {
        OntFile ontFile = new OntFile();
        ontFile.setOntId(ontId);
        return ontFileMapper.select(ontFile);
    }

    @Override
    public Map<String, Object> upload(String action, UploadDto req) throws Throwable {
        // 上传文件到本地
        String ontId = req.getOntId();
        MultipartFile file = req.getFile();
        long size = file.getSize();
        String password = req.getPassword();
        String originalFilename = file.getOriginalFilename();
        String path = configParam.BASE_FILE_PATH + "upload" + Constant.SPLIT + ontId.substring(8) + Constant.SPLIT + System.currentTimeMillis() + Constant.SPLIT + originalFilename;
        File localFile = new File(path);
        File parentFile = localFile.getParentFile();

        try {
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            if (!localFile.exists()) {
                localFile.createNewFile();
            }
            InputStream inputStream = file.getInputStream();

            OutputStream outputStream = new FileOutputStream(localFile);

            if (inputStream != null) {
                int len = -1;
                byte[] b = new byte[1024];
                while ((len = inputStream.read(b)) != -1) {
                    outputStream.write(b, 0, len);
                }
                inputStream.close();
                outputStream.close();
            }

            log.info("local upload finished");

            // 调用OntFS client 上传
            byte[] bytes = AESService.AES_CBC_Decrypt(Helper.hexToBytes(password.toLowerCase()));
            String decrypted = new String(bytes);
            long expireTime = (System.currentTimeMillis() + Constant.FILE_EXPIRE) / 1000L;

            Object obj = uploadFileToOntFs(path, size, expireTime, decrypted);
            JSONObject jsonObject = JSON.parseObject(obj.toString());
            Integer error = jsonObject.getInteger("error");
            if (error != 0) {
                String desc = jsonObject.getString("desc");
                throw new OntFsException(action, desc, desc, error);
            }
            JSONObject rpcResult = jsonObject.getJSONObject("result");
            String fileHash = rpcResult.getString("FileHash");
            String txHash = rpcResult.getString("Tx");

            // 存入映射关系
            OntFile ontFile = new OntFile();
            ontFile.setOntId(ontId);
            ontFile.setFileHash(fileHash);
            ontFile.setFileName(originalFilename);
            ontFile.setTxHash(txHash);
            ontFile.setCreateTime(new Date());
            ontFileMapper.insertSelective(ontFile);

            // 返回结果
            Map<String, Object> result = new HashMap<>();
            result.put("fileHash", fileHash);
            result.put("txHash", txHash);
            return result;

        } catch (OntFsException e) {
            log.error("catch OntFsException:", e);
            throw e;
        } catch (Exception e) {
            log.error("catch Exception:", e);
            throw e;
        } finally {
            // 删除本地文件
            try {
                localFile.delete();
                parentFile.delete();
                parentFile.getParentFile().delete();
                log.info("delete local file successfully!");
            } catch (Exception e) {
                log.error("delete file exception:", e);
            }
        }

    }

    @Override
    public String downloadUrl(String action, String fileHash, String password) throws Throwable {
        OntFile ontFile = new OntFile();
        ontFile.setFileHash(fileHash);
        ontFile = ontFileMapper.selectOne(ontFile);
        if (ontFile == null) {
            throw new OntFsException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }
        String fileName = ontFile.getFileName();
        String ontId = ontFile.getOntId();
        String path = configParam.BASE_FILE_PATH + "download" + Constant.SPLIT + ontId.substring(8) + Constant.SPLIT + System.currentTimeMillis() + Constant.SPLIT + fileName;
        File localFile = new File(path);
        File parentFile = localFile.getParentFile();

        try {
            if (!parentFile.exists()) {
                parentFile.mkdirs();
            }
            // 下载数据
            byte[] bytes = AESService.AES_CBC_Decrypt(Helper.hexToBytes(password.toLowerCase()));
            String decrypted = new String(bytes);

            Object obj = downloadFileFromOntFs(fileHash, path, decrypted);
            JSONObject jsonObject = JSON.parseObject(obj.toString());
            Integer error = jsonObject.getInteger("error");
            if (error != 0) {
                String desc = jsonObject.getString("desc");
                throw new OntFsException(action, desc, desc, error);
            }

            if (!localFile.exists()) {
                throw new OntFsException(action, ErrorInfo.COMM_NET_FAIL.descCN(), ErrorInfo.COMM_NET_FAIL.descEN(), ErrorInfo.COMM_NET_FAIL.code());
            }
            return configParam.DOWNLOAD_URL + localFile;
        } catch (OntFsException e) {
            log.error("catch OntFsException:", e);
            // 删除本地文件
            localFile.delete();
            parentFile.delete();
            parentFile.getParentFile().delete();
            throw e;
        }
    }

    @Override
    public Map<String, Object> download(String action, String path) throws Throwable {
        File localFile = new File(path);
        File parentFile = localFile.getParentFile();
        if (!localFile.exists()) {
            throw new OntFsException(action, ErrorInfo.NOT_EXIST.descCN(), ErrorInfo.NOT_EXIST.descEN(), ErrorInfo.NOT_EXIST.code());
        }

        InputStream inputStream = new FileInputStream(localFile);

        // 删除本地文件
        localFile.delete();
        parentFile.delete();
        parentFile.getParentFile().delete();

        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("inputStream", inputStream);
        result.put("fileName", localFile.getName());
        return result;
    }

    private Object uploadFileToOntFs(String path, long size, long expireTime, String password) throws Throwable {
        List<Map<String, Object>> paramList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("FilePath", path);
        param.put("FileDesc", "FileDesc");
        param.put("FileSize", size);
        param.put("TimeExpired", expireTime);
        param.put("CopyNum", 1);
        param.put("StorageType", 1);
        param.put("Encrypt", true);
        param.put("EncryptPassword", password);
        param.put("FirstPdp", configParam.ONTFS_FIRST_PDP);
        param.put("PdpInterval", 14400);
        param.put("WhiteList", new ArrayList<>());
        paramList.add(param);
        String paramStr = JSON.toJSONString(paramList);
        String url = configParam.FS_CLIENT_URL;
        String methodName = Constant.METHOD_UPLOAD;
        Object obj = JsonUtil.parseObject(paramStr, Object.class);
        return JsonRpcUtil.rpcExecute(methodName, url, null, obj, Object.class);
    }

    private Object downloadFileFromOntFs(String fileHash, String path, String password) throws Throwable {
        List<Map<String, Object>> paramList = new ArrayList<>();
        Map<String, Object> param = new HashMap<>();
        param.put("FileHash", fileHash);
        param.put("InOrder", true);
        param.put("MaxPeerCnt", 3);
        param.put("OutFilePath", path);
        param.put("DecryptPwd", password);
        paramList.add(param);
        String paramStr = JSON.toJSONString(paramList);
        String url = configParam.FS_CLIENT_URL;
        String methodName = Constant.METHOD_DOWNLOAD;
        Object obj = JsonUtil.parseObject(paramStr, Object.class);
        return JsonRpcUtil.rpcExecute(methodName, url, null, obj, Object.class);
    }
}
