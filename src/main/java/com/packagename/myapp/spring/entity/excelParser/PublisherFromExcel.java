package com.packagename.myapp.spring.entity.excelParser;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

}
