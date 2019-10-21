package com.packagename.myapp.spring.entity.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class SubscriptionsReportPart {

    private Long subscriptionId;
    private Integer regionCode;
    private String publisherId;
    private String publicationCode;
    private Integer catalogId;
    private Integer minSubsPeriod;
    private Integer[] allocation;
    private Integer count;
}
