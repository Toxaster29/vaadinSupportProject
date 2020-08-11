package com.packagename.myapp.spring.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ContractSchedule {

    private Integer id;
    private String legalHid;
    private Integer year;
    private Integer half;
    private String contractId;

}
