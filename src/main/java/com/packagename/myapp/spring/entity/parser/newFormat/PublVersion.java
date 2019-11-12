package com.packagename.myapp.spring.entity.parser.newFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PublVersion {

    private Long id;
    private String name;
    private Integer[] regions;
    private Integer[] terrains;
    private Integer standard;
    private Integer weight;
    private Integer pages;
    private Integer formatId;
    private Integer height;
    private Integer width;
    private Integer timeId;
    private Integer count;
    private List<Issue> issue;

}
