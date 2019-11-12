package com.packagename.myapp.spring.entity.parser.newFormat;

import lombok.Data;

import java.util.List;

@Data
public class Terrain {

    private Long id;
    private String name;
    private Long regionId;
    private List<String> zipcodes;

}
