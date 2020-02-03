package com.packagename.myapp.spring.entity.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@AllArgsConstructor
@Getter
@Setter
public class ChildrenSubscriptionEntity {

    private String publicationCode;
    private String index;
    private BigDecimal totalPrice;
    private Integer totalCount;

}
