package com.packagename.myapp.spring.entity.report;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CatalogElement {

    private String publicationCode;
    private Integer catalogPeriod;
    private String Name;

}
