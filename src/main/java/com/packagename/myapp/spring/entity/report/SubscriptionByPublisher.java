package com.packagename.myapp.spring.entity.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SubscriptionByPublisher {

    private Integer regionCode;
    private String publisherId;
    private String publicationCode;
    private Integer catalogId;
    private Integer minSubsPeriod;
    private Integer[] allocation;

}
