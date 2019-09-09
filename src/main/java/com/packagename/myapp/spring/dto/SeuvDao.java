package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.LogShpiAction;

import java.util.List;

public interface SeuvDao {

    List<LogShpiAction> getActionList(String value);

}
