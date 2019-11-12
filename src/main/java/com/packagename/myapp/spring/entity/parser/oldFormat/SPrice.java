package com.packagename.myapp.spring.entity.parser.oldFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SPrice {

    private String indexId;
    private String type;
    private Integer privilegeId;
    private Integer msp;
    private String priceAndNDS;
    private String multiplePrice;
    private String ndsMultiplePrice;
    private String tariffType;
    private Integer areaId;
    private String currency;
    private String subscriptionAcceptance;
    private String description;
    private Integer catalogId;

    public SPrice(List<String> params) {
        this(params.get(0), params.get(1), Integer.parseInt(params.get(2)), Integer.parseInt(params.get(3)),
                params.get(4), params.get(5), params.get(6), params.get(7), Integer.parseInt(params.get(8)),
                params.get(9), params.get(10), params.get(11), Integer.parseInt(params.get(12)));
    }
}
