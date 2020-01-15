package com.ontology.controller;

import com.ontology.bean.Result;
import com.ontology.controller.vo.*;
import com.ontology.entity.OntFile;
import com.ontology.service.DataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.*;


@Api(tags = "数据接口")
@Slf4j
@RestController
@RequestMapping("/api/v1/data")
@CrossOrigin
public class DataController {
    @Autowired
    private DataService dataService;

    @ApiOperation(value = "query file", notes = "query file", httpMethod = "GET")
    @GetMapping("/{ontId}")
    public Result getFile(@PathVariable String ontId) throws Exception {
        String action = "getFile";
        log.info("========start:{}", action);
        List<OntFile> result = dataService.getFile(action, ontId);
        return new Result(action, 0, "SUCCESS", result);
    }


    @ApiOperation(value = "upload file", notes = "upload file", httpMethod = "POST")
    @PostMapping("/upload")
    public Result upload(@Valid UploadDto req ) throws Throwable {
        String action = "upload";
        log.info("========start:{}", action);
        Map<String, Object> result = dataService.upload(action, req);
        return new Result(action, 0, "SUCCESS", result);
    }

    @ApiOperation(value = "download file", notes = "download file", httpMethod = "POST")
    @GetMapping("/download-url")
    public Result downloadUrl(@Valid DownloadDto req, HttpServletResponse resp) throws Throwable {
        String action = "downloadUrl";
        log.info("========start:{}", action);
        String fileHash = req.getFileHash();
        String password = req.getPassword();
        String url = dataService.downloadUrl(action, fileHash, password);
        return new Result(action, 0, "SUCCESS", url);
    }

    @ApiOperation(value = "download file", notes = "download file", httpMethod = "POST")
    @GetMapping("/download")
    public void download(@RequestParam String path, HttpServletResponse resp) throws Throwable {
        String action = "download";
        log.info("========start:{}", action);
        dataService.download(action, path, resp);
    }

}