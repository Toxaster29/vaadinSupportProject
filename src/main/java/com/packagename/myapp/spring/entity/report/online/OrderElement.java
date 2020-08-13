package com.packagename.myapp.spring.entity.report.online;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OrderElement {

    private Integer deliveryType;
    private String ufpsCode;
    private String publisherId;
    private String publicationCode;
    private String personHid;
    private Integer onlineOrderId;
    private String marker;

}
