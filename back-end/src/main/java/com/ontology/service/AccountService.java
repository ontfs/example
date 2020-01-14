package com.ontology.service;

import com.ontology.controller.vo.UserDto;

import java.util.Map;


public interface AccountService {

    Map<String, Object> register(String action, UserDto req) throws Exception;

    Map<String, Object> login(String action, UserDto req) throws Exception;

}
