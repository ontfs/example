package com.ontology.entity;

import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tbl_user")
@Data
public class User {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    private String ontId;
    private String userName;
    private String password;
    private Date createTime;
}
