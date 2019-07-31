package com.packagename.myapp.spring.service;

import com.packagename.myapp.spring.entity.ContractEntity;
import com.packagename.myapp.spring.entity.EntityFromTable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SQLGeneratorService {

    public SQLGeneratorService() {
    }

    public String generateSqlForContractGetQuery(List<EntityFromTable> fromTableList) {
        StringBuilder prepareSql = new StringBuilder();
        prepareSql.append("Select * from contract where ");
        if (fromTableList.size() > 1) {
            for (int x = 1; x < fromTableList.size(); x++) {
                EntityFromTable entity = fromTableList.get(x);
                if (entity.docNumber != null && entity.getId() != null) {
                    if (x > 1) prepareSql.append(" or ");
                    prepareSql.append("legal_hid = \'" + entity.getId()
                            + "\' and doc_number = \'" + entity.docNumber + "\'");
                }
            }
        }
        String headerNumber = fromTableList.get(0).getDocNumber();
        prepareSql.append(" and year = \'" + headerNumber.substring(headerNumber.length() - 1)
                + "\' and half = \'" + headerNumber.substring(headerNumber.length() -7, headerNumber.length() - 2) + "\';");
        return String.valueOf(prepareSql);
    }

    public String generateSqlForUFPS(List<ContractEntity> contractEntities) {
        if (!contractEntities.isEmpty()) {
            StringBuilder updateSql = new StringBuilder("update contract_params set value=\'TCFPS\' where\n" +
                    "contract_id IN (");
            StringBuilder insertSql = new StringBuilder();
            contractEntities.forEach(contractEntity -> {
                int id = contractEntity.getId();
                int ufpsNumber = contractEntity.getUfpsNumber() != null ? contractEntity.getUfpsNumber() : 0;
                updateSql.append("\'" + id + "\', ");
                insertSql.append("insert into contract_params (contract_id,name,value) values (\'" + id
                        + "\' , \'TCFPS_PAYER\', \'" + ufpsNumber + "\');\n");
            });
            updateSql.deleteCharAt(updateSql.length() - 1);
            updateSql.append(") AND name = \'CONTRACT_PAYER\';");
            return String.valueOf(updateSql.append("\n" + insertSql));
        }
        return "Нет подходящих договоров в базе 8===>";
    }

    public String generateSqlForAUP(List<ContractEntity> contractEntities) {
        if (!contractEntities.isEmpty()) {
            StringBuilder updateSql = new StringBuilder("update contract_params set value=\'DEFAULT\' where contract_id IN (");
            StringBuilder deleteSql = new StringBuilder("delete from contract_params where contract_id IN (");
            contractEntities.forEach(contractEntity -> {
                int id = contractEntity.getId();
                updateSql.append("\'" + id + "\', ");
                deleteSql.append("\'" + id + "\', ");
            });
            updateSql.deleteCharAt(updateSql.length() - 1);
            updateSql.append(") and name = \'CONTRACT_PAYER\';");
            deleteSql.deleteCharAt(deleteSql.length() - 1);
            deleteSql.append(") and name = \'TCFPS_PAYER\';");
            return String.valueOf(updateSql + "\n" + deleteSql);
        }
        return "Нет подходящих договоров в базе 8===>";
    }
}
