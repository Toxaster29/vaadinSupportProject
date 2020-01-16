package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.excelParser.PublisherFromExcel;

public interface ExcelParserDao {
    String getHidByPublisherParams(String inn, String publisherName);

    boolean setNmcToPublisher(PublisherFromExcel publisher);
}
