package com.packagename.myapp.spring.entity.parser.oldFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SArea {

    private Integer id;
    private String name;
    private String type;
    private String center;
    private String territory;
    private Integer population;
    private Integer ruralPopulation;

    public SArea(List<String> params) {
        this(Integer.parseInt(params.get(0)), params.get(1), params.get(2), params.get(3), params.get(4),
                params.get(5) != null ? Integer.parseInt(params.get(5)) : null,
                params.get(6) != null ? Integer.parseInt(params.get(6)) : null);
    }
}
