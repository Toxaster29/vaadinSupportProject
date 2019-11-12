package com.packagename.myapp.spring.entity.parser.newFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubsVariant {

    private Long acceptId;
    private Integer[] regions;
    private Integer msp;
    private Integer price;
    private Integer vatId;
    private Byte state;

}
