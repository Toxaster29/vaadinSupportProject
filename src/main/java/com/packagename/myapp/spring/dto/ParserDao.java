package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.parser.DirectoryData;
import com.packagename.myapp.spring.entity.parser.newFormat.Accept;
import com.packagename.myapp.spring.entity.parser.newFormat.ConnectionThematic;

import java.util.List;

public interface ParserDao {

    List<DirectoryData> getDictionaryData();

    List<Accept> getAcceptList();

    List<ConnectionThematic> getConnectivityThematicEntities();

    void uploadConnectionData(List<ConnectionThematic> connectionThematicList);
}
