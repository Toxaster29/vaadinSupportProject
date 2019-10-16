package com.packagename.myapp.spring.entity.parser;

import lombok.Data;

import java.util.List;

@Data
public class Campaign {

    private Byte year;
    private Byte yearsub;
    private List<Accept> accept;


}
