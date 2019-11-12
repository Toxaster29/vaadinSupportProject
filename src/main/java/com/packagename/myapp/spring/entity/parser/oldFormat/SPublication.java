package com.packagename.myapp.spring.entity.parser.oldFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SPublication {

    private String id;
    private String name;
    private String shortName;
    private String annotation;
    private String phone;
    private String country;
    private String language;
    private String output;
    private String type;
    private String mailing;
    private Integer outputCountForPeriod;
    private String period;
    private String outputCountHalfYear;
    private String outputCountYear;
    private String outputDaysOfWeekFormat;
    private String outputDaysOfWeekBand;
    private String weightDaysOfWeek;
    private String dates;
    private String description;

    public SPublication(List<String> params) {
        this(params.get(0), params.get(1), params.get(2), params.get(3), params.get(4), params.get(5), params.get(6),
                params.get(7), params.get(8), params.get(9), Integer.parseInt(params.get(10)), params.get(11),
                params.get(12), params.get(13), params.get(14), params.get(15), params.get(16), params.get(17), params.get(18));
    }
}
