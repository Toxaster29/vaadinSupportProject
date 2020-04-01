package com.packagename.myapp.spring.entity.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Subscription {

    private Integer subscriptionId;
    private Integer regionCode;
    private String publisherId;
    private String publicationCode;
    private String publicationIndex;
    private Integer catalogId;
    private Integer minSubsPeriod;
    private int[] allocation;
    private Integer count;
    private int[] mspAllocation;

}
