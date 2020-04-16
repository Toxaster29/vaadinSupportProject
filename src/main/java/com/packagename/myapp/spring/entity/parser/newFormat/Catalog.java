package com.packagename.myapp.spring.entity.parser.newFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Catalog {

    private String index;
    private String name;
    private String comment;
    private Integer agencyid;
    private Integer distributionid;
    private Integer expeditionid;
    private Integer clientid;
    private Byte cellophane;
    private List<Term> term;
    private List<SubsVersion> subsversion;
    private List<SubsVariant> subsvariant;

}
