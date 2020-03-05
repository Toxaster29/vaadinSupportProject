package com.packagename.myapp.spring.entity.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CatalogPrice {

    private String index;
    private Double mspPrice;
    private Double issuePrice;
    private Double mspPriceNoVat;
    private Double issuePriceNoVat;
    private String vat;
    private Integer elementId;
    private int[] regionId;

}
