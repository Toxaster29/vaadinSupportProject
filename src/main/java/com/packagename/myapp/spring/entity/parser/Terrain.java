package com.packagename.myapp.spring.entity.parser;

import lombok.Data;

import java.util.List;

@Data
public class Terrain {

    private Long id;
    private String name;
    private Long regionId;
    private List<String> zipcodes;

}
