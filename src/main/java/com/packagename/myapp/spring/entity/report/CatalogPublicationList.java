package com.packagename.myapp.spring.entity.report;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class CatalogPublicationList {

    private String publicationCode;
    private Integer periodId;
    private String title;
    private List<PublicationInfoList> publicationInfo;
    private Integer count;

}
