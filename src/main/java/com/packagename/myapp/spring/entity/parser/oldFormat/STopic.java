package com.packagename.myapp.spring.entity.parser.oldFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class STopic {

    private Integer catalogId;
    private Integer rubricId;
    private String rubricName;

    public STopic(List<String> params) {
        this(Integer.parseInt(params.get(0)), Integer.parseInt(params.get(1)), params.get(2));
    }
}
