package com.packagename.myapp.spring.entity.parser.oldFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SCountIn {

    private Integer catalogId;
    private String sectionId;
    private String publicationId;

    public SCountIn(List<String> params) {
        this(Integer.parseInt(params.get(0)), params.get(1), params.get(2));
    }
}
