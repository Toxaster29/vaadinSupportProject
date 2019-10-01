package com.packagename.myapp.spring.entity.contract;

import java.time.LocalDate;

public class TableMainData {

    String id;
    String documentNumber;
    LocalDate documentDate;
    String firstPeriod;
    String secondPeriod;


    public TableMainData() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public LocalDate getDocumentDate() {
        return documentDate;
    }

    public void setDocumentDate(LocalDate documentDate) {
        this.documentDate = documentDate;
    }

    public String getFirstPeriod() {
        return firstPeriod;
    }

    public void setFirstPeriod(String firstPeriod) {
        this.firstPeriod = firstPeriod;
    }

    public String getSecondPeriod() {
        return secondPeriod;
    }

    public void setSecondPeriod(String secondPeriod) {
        this.secondPeriod = secondPeriod;
    }
}
