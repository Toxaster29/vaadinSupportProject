package com.packagename.myapp.spring.entity.parser.oldFormat;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
@Getter
public class Complex {

    private String indexComplex;
    private String indexInclude;

    public Complex(List<String> params) {
        this(params.get(0), params.get(1));
    }

}
