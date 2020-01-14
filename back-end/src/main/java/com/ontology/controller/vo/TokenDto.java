package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class TokenDto {
    @ApiModelProperty(name="domain",value = "domain",required = true)
    @NotBlank
    private String domain;

    @ApiModelProperty(name="signature",value = "signature",required = true)
    @NotBlank
    private String signature;

    @ApiModelProperty(name="ontid",value = "ontid",required = true)
    @NotNull
    private Integer tokenId;
}
