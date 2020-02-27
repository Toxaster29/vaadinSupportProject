package com.packagename.myapp.spring.entity.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class OnlineReportEntity {

    private String index;
    private String publicationCode;
    private String name;
    private BigDecimal sum;
    private Integer mspCount;
    private String deliveryRegion;
    private String deliveryAddress;
    private String buyerHid;
    private Integer onlineOrderId;
    private Integer catalogPeriod;

}
