package com.packagename.myapp.spring.entity;

public class EntityFromTable {

    public EntityFromTable(String id, String payer, String provider, String docNumber, String docDate) {
        this.id = id;
        this.payer = payer;
        this.provider = provider;
        this.docNumber = docNumber;
        this.docDate = docDate;
    }

    public EntityFromTable() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getDocNumber() {
        return docNumber;
    }

    public void setDocNumber(String docNumber) {
        this.docNumber = docNumber;
    }

    public String getDocDate() {
        return docDate;
    }

    public void setDocDate(String docDate) {
        this.docDate = docDate;
    }

    public String id;
    public String payer;
    public String provider;
    public String docNumber;
    public String docDate;
}
