package com.packagename.myapp.spring.entity.report;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CatalogPriceWithService {

    private Integer elementId;
    private String index;
    private Double mspPrice;
    private Double issuePrice;
    private Double mspPriceNoVat;
    private Double issuePriceNoVat;
    private Double servicePrice;
    private Double servicePriceNotVat;
    private String vat;
    private String regionId;
}
