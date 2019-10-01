package com.packagename.myapp.spring.entity.contract;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EntityFromTable {

    private String id;
    private String payer;
    private String docNumber;
    private Integer year;
    private Integer half;

}
