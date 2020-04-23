package com.packagename.myapp.spring.entity.contract;

import lombok.Getter;

import java.util.Date;

@Getter
public class ContractEntity {
    Integer id;
    String legalHid;
    Integer year;
    Integer half;
    String docType;
    String docNumber;
    Date docDate;
    String status;
    Integer template;
    Integer ufpsNumber;

    public ContractEntity(Integer id, String legalHid, Integer year, Integer half, String docNumber, Integer template, Date docDate) {
        this.id = id;
        this.legalHid = legalHid;
        this.year = year;
        this.half = half;
        this.docNumber = docNumber;
        this.template = template;
        this.docDate = docDate;
    }

    public ContractEntity(Integer id, String legalHid, String docNumber, Date docDate, String status) {
        this.id = id;
        this.legalHid = legalHid;
        this.docNumber = docNumber;
        this.docDate = docDate;
        this.status = status;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setUfpsNumber(Integer ufpsNumber) {
        this.ufpsNumber = ufpsNumber;
    }

}
