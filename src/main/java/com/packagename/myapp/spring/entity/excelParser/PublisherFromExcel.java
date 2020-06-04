package com.packagename.myapp.spring.entity.excelParser;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@NoArgsConstructor
@Getter
@Setter
public class PublisherFromExcel {

    private String publisherName;
    private String inn;
    private Integer price;
    private String hid;
    private String manager;
    private String contractNumber;
    private Integer contractId;
    private Date endDate;

}
