package com.packagename.myapp.spring.entity.parser.oldFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SDispatch {

    private Integer agencyId;
    private Integer parcelId;
    private String name;
    private String description;


    public SDispatch(List<String> params) {
        this(Integer.parseInt(params.get(0)), Integer.parseInt(params.get(1)),
                params.get(2), params.get(3));
    }
}
