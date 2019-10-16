package com.packagename.myapp.spring.entity.parser;

import lombok.Data;

import java.util.List;

@Data
public class PublVersion {

    private Long id;
    private String name;
    private Integer[] regions;
    private Integer[] terrains;
    private Integer standard;
    private Integer weight;
    private Integer pages;
    private Long formatId;
    private Integer height;
    private Integer width;
    private Long timeId;
    private Integer count;
    private List<Issue> issue;

}
