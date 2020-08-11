package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.ContractSchedule;
import com.packagename.myapp.spring.entity.contract.ContractEntity;

import java.util.List;
import java.util.Map;

public interface ContractDao {
   List<ContractEntity> getContractsWithActiveStatus();

   Map<String, ContractSchedule> getContractSchedulersForPeriod();
}
