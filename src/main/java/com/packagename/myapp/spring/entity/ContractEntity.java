package com.packagename.myapp.spring.entity;

import java.util.Date;

public class ContractEntity {
    Integer id;
    String legalHid;
    Integer year;
    Integer half;
    String doctype;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getLegalHid() {
        return legalHid;
    }

    public void setLegalHid(String legalHid) {
        this.legalHid = legalHid;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public Integer getUfpsNumber() {
        return ufpsNumber;
    }

    public void setUfpsNumber(Integer ufpsNumber) {
        this.ufpsNumber = ufpsNumber;
    }

    public Date getDocDate() {
        return docDate;
    }

    public void setDocDate(Date docDate) {
        this.docDate = docDate;
    }
}
