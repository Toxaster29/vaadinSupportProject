package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.excelParser.PublisherFromExcel;

import java.util.List;

public interface ExcelParserDao {
    List<Integer> setNmcToPublisher(PublisherFromExcel publisher);

    void updateNmc(PublisherFromExcel publisher, List<Integer> ids);
}
