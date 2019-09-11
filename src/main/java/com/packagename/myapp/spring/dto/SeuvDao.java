package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.EuvStatisticEntity;
import com.packagename.myapp.spring.entity.LogShpiAction;
import com.packagename.myapp.spring.entity.ShpiTableEntity;

import java.time.LocalDate;
import java.util.List;

public interface SeuvDao {

    List<LogShpiAction> getActionList(String value);

    EuvStatisticEntity getStatistic();

    List<ShpiTableEntity> searchShpiByParams(LocalDate start, LocalDate end, Boolean euv, Boolean eo);
}
