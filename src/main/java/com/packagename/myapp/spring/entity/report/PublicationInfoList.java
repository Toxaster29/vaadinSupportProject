package com.packagename.myapp.spring.entity.report;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PublicationInfoList {

    private Integer regionCode;
    private Integer[] monthOutput;

}
