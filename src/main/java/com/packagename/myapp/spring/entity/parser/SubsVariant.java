package com.packagename.myapp.spring.entity.parser;

import lombok.Data;

@Data
public class SubsVariant {

    private Long acceptId;
    private Integer[] regions;
    private Integer msp;
    private Integer price;
    private Long vatId;
    private Byte state;

}
