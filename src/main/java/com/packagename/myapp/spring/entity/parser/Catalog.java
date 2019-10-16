package com.packagename.myapp.spring.entity.parser;

import lombok.Data;

import java.util.List;

@Data
public class Catalog {

    private String index;
    private String name;
    private String comment;
    private Long agencyId;
    private Long distributionId;
    private Long expeditionId;
    private Long clientId;
    private Byte cellophane;
    private List<Term> term;
    private List<SubsVersion> subsVersion;
    private List<SubsVariant> subsVariant;

}
