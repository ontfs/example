package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DownloadDto {
    @ApiModelProperty(name="password",value = "password",required = true)
    @NotBlank
    private String password;

    @ApiModelProperty(name="fileHash",value = "fileHash",required = true)
    @NotBlank
    private String fileHash;
}
