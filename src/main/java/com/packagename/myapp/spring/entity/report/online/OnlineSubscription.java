package com.packagename.myapp.spring.entity.report.online;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class OnlineSubscription {

    private Integer subscriptionId;
    private Integer regionCode;
    private String publisherId;
    private String publicationCode;
    private String publicationIndex;
    private Integer catalogId;
    private Integer minSubsPeriod;
    private Double minSubPrice;
    private int[] allocation;
    private int[] mspAlloc;
    private Integer onlineOrderId;
    private Integer count;
    private Double subPrice;
    private String postalCode;
    private int year;
    private Integer regionBuyCode;

}
