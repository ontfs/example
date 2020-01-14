package com.ontology.controller.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserDto {
    @ApiModelProperty(name="domain",value = "domain",required = true)
    @NotBlank
    private String userName;

    @ApiModelProperty(name="signature",value = "signature",required = true)
    @NotBlank
    private String password;

}
