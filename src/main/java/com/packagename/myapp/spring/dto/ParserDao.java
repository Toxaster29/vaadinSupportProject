package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.parser.DirectoryData;
import com.packagename.myapp.spring.entity.parser.newFormat.Accept;

import java.util.List;

public interface ParserDao {

    List<DirectoryData> getDictionaryData();

    List<Accept> getAcceptList();
}
