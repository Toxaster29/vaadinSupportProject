package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.parser.DirectoryData;

import java.util.List;

public interface ParserDao {

    List<DirectoryData> getDictionaryData();

}
