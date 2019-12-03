package com.packagename.myapp.spring.entity.report;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CatalogPeriodEntity {

    private Integer year;
    private Integer half;
    private Integer [] periodIds;
    List<CatalogPublicationList> publicationList;

    public void addToPublicationList(CatalogPublicationList catalogPublication) {
        this.publicationList.add(catalogPublication);
    }

    public String getName() {
        return this.year + "-" + this.half;
    }
}
