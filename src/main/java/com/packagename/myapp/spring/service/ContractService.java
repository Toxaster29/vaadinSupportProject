package com.packagename.myapp.spring.service;

import com.packagename.myapp.spring.dto.ContractDao;
import com.packagename.myapp.spring.entity.ContractSchedule;
import com.packagename.myapp.spring.entity.contract.ContractEntity;
import com.packagename.myapp.spring.service.report.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class ContractService {

    @Autowired
    private ReportService reportService;
    @Autowired
    private ContractDao contractDao;

    public void reviewContractForSchedulers() {
        List<ContractEntity> contractEntities = contractDao.getContractsWithActiveStatus();
        Map<String, ContractSchedule> contractSchedules = contractDao.getContractSchedulersForPeriod();
        List<String> dataToFile = new ArrayList<>();
        contractEntities.forEach(contractEntity -> {
            ContractSchedule contractSchedule = contractSchedules.get(contractEntity.getId().toString());
            if (contractSchedule == null) dataToFile.add(contractEntity.getLegalHid() + "\t" + contractEntity.getYear() + "-" + contractEntity.getHalf());
        });
        reportService.writeTextToFile(dataToFile, "NoneSchedulers");
        System.out.println("Good");
    }
}
