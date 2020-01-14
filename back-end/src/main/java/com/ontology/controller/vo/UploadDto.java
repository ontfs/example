package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UploadDto {

    @ApiModelProperty(name="password",value = "password",required = true)
    @NotBlank
    private String password;

    @ApiModelProperty(name="ontId",value = "ontId",required = true)
    @NotBlank
    private String ontId;

    @ApiModelProperty(name="file",value = "file",required = true)
    @NotNull
    private MultipartFile file;
}
