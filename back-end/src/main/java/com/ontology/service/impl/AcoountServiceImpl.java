package com.ontology.service.impl;

import com.github.ontio.common.Helper;
import com.ontology.controller.vo.UserDto;
import com.ontology.entity.User;
import com.ontology.exception.OntFsException;
import com.ontology.mapper.UserMapper;
import com.ontology.secure.AESService;
import com.ontology.service.AccountService;
import com.ontology.utils.*;
import io.ont.addon.signing.sdk.CentralizationOntId;
import io.ont.addon.signing.sdk.SigningSdk;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.*;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class AcoountServiceImpl implements AccountService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private ConfigParam configParam;

    private SigningSdk signingSdk;

    private CentralizationOntId ontIdInstance;

    @PostConstruct
    public void init() {
        signingSdk = SigningSdk.getInstance("");
        signingSdk.setPayer(configParam.PAYER_ADDRESS, configParam.PAYER_WIF);
        signingSdk.setBlockChainUrl(configParam.RESTFUL_URL);

        //ontId
        ontIdInstance = CentralizationOntId.getInstance();
        ontIdInstance.init(configParam.ONTID_WIF_NO1, configParam.ONTID_WIF_NO2);
    }

    @Override
    public Map<String, Object> register(String action, UserDto req) throws Exception {
        String userName = req.getUserName();
        String password = req.getPassword();
        byte[] bytes = AESService.AES_CBC_Decrypt(Helper.hexToBytes(password.toLowerCase()));
        String decrypted = new String(bytes);
        User user = new User();
        user.setUserName(userName);
        User record = userMapper.selectOne(user);
        if (record != null) {
            throw new OntFsException(action, ErrorInfo.USER_ALREADY_EXIST.descCN(), ErrorInfo.USER_ALREADY_EXIST.descEN(), ErrorInfo.USER_ALREADY_EXIST.code());
        }

        user.setPassword(HelperUtil.sha256(decrypted));
        user.setCreateTime(new Date());
        try {
            userMapper.insertSelective(user);
        } catch (DuplicateKeyException e) {
            log.error("register error:", e);
            throw new OntFsException(action, ErrorInfo.USER_ALREADY_EXIST.descCN(), ErrorInfo.USER_ALREADY_EXIST.descEN(), ErrorInfo.USER_ALREADY_EXIST.code());
        }


        Integer id = user.getId();
        String ontId = ontIdInstance.registerOntId(id);
        user.setOntId(ontId);
        userMapper.updateByPrimaryKeySelective(user);

        Map<String, Object> result = new HashMap<>();
        result.put("userName", userName);
        result.put("ontId", ontId);
        return result;
    }


    @Override
    public Map<String, Object> login(String action, UserDto req) throws Exception {
        String userName = req.getUserName();
        String password = req.getPassword();
        byte[] bytes = AESService.AES_CBC_Decrypt(Helper.hexToBytes(password.toLowerCase()));
        String decrypted = new String(bytes);

        User record = new User();
        record.setUserName(userName);

        record = userMapper.selectOne(record);
        if (record == null) {
            throw new OntFsException(action, ErrorInfo.NOT_FOUND.descCN(), ErrorInfo.NOT_FOUND.descEN(), ErrorInfo.NOT_FOUND.code());
        }
        if (!record.getPassword().equals(HelperUtil.sha256(decrypted))) {
            throw new OntFsException(action, ErrorInfo.INVALID_PASSWORD.descCN(), ErrorInfo.INVALID_PASSWORD.descEN(), ErrorInfo.INVALID_PASSWORD.code());
        }

        Map<String, Object> result = new HashMap<>();
        result.put("userName", userName);
        result.put("ontId", record.getOntId());
        return result;
    }

}
