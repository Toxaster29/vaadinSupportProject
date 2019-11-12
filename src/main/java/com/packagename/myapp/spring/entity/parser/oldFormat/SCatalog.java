package com.packagename.myapp.spring.entity.parser.oldFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SCatalog {

    private Integer id;
    private String name;
    private String sectionId;
    private String sectionName;

    public SCatalog(List<String> params) {
        this(Integer.parseInt(params.get(0)), params.get(1), params.get(2), params.get(3));
    }
}
