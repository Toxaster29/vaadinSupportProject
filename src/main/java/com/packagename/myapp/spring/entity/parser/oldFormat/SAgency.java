package com.packagename.myapp.spring.entity.parser.oldFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SAgency {

    private Integer id;
    private String name;
    private String shortName;
    private String legalAddress;
    private  String address;
    private String phone;
    private String email;
    private String bankData;
    private  String inn;
    private String okonh;
    private  String okpo;
    private  String description;

    public SAgency(List<String> params) {
        this(Integer.parseInt(params.get(0)), params.get(1), params.get(2), params.get(3), params.get(4),
                params.get(5), params.get(6), params.get(7), params.get(8), params.get(9), params.get(10), params.get(11));
    }
}
