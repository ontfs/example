package com.ontology.service;

import com.ontology.controller.vo.UploadDto;
import com.ontology.controller.vo.TokenDto;
import com.ontology.entity.OntFile;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

public interface DataService {

    List<OntFile> getFile(String action, String ontId);

    Map<String, Object> upload(String action, UploadDto req) throws Throwable;

    String downloadUrl(String action, String fileHash, String password) throws Throwable;

    void download(String action, String path) throws Throwable;

}
