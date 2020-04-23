package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.contract.ContractEntity;

import java.util.List;
import java.util.Set;

public interface ExcelParserDao {

    void updateNmc(Integer price, Set<Integer> ids);

    List<ContractEntity> getContractForNmcUpdate(int year, int half);
}
