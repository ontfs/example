package com.ontology.entity;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.ontology.utils.DateSerializer;
import lombok.Data;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "tbl_ont_file")
@Data
public class OntFile {
    @Id
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    private String ontId;
    private String fileName;
    private String fileHash;
    private String txHash;
    @JsonSerialize(using = DateSerializer.class)
    private Date createTime;
}
