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
    private Integer agencyId;
    private Integer distributionId;
    private Integer expeditionId;
    private Integer clientId;
    private Byte cellophane;
    private List<Term> term;
    private List<SubsVersion> subsVersion;
    private List<SubsVariant> subsVariant;

}
