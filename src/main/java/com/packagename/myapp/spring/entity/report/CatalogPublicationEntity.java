package com.packagename.myapp.spring.entity.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CatalogPublicationEntity {

    private Integer id;
    private String legalHid;
    private String title;
    private String publicationCode;
    private Integer periodId;
    private Integer[] outputMonthCount;
    private Integer outputCount;
    private Integer circulation;
}
