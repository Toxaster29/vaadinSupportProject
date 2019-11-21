package com.packagename.myapp.spring.service;

import com.packagename.myapp.spring.entity.contract.ContractEntity;
import com.packagename.myapp.spring.entity.contract.EntityFromTable;
import com.packagename.myapp.spring.entity.contract.TableMainData;
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
            for (int x = 0; x < fromTableList.size(); x++) {
                EntityFromTable entity = fromTableList.get(x);
                if (entity.getDocNumber() != null && entity.getId() != null) {
                    if (x > 0) prepareSql.append(" or ");
                    prepareSql.append("legal_hid = \'" + entity.getId()
                            + "\' and doc_number = \'" + entity.getDocNumber() + "\'"
                            + " and year = " + entity.getYear() + " and half = " + entity.getHalf());
                }
            }
        }
        return String.valueOf(prepareSql);
    }

    public String generateSqlForContract(List<TableMainData> fromTableList, int year, int half) {
        StringBuilder prepareSql = new StringBuilder();
        prepareSql.append("Select * from contract where ");
        if (fromTableList.size() > 1) {
            for (int x = 0; x < fromTableList.size(); x++) {
                TableMainData entity = fromTableList.get(x);
                if (entity.getDocumentNumber() != null && entity.getId() != null) {
                    if (x > 0) prepareSql.append(" or ");
                    prepareSql.append("legal_hid = \'" + entity.getId()
                            + "\' and doc_number = \'" + entity.getDocumentNumber() + "\'"
                            + " and year = " + year + " and half = " + half);
                }
            }
        }
        return String.valueOf(prepareSql);
    }

    public String generateSqlForUFPS(List<ContractEntity> contractEntities) {
        if (!contractEntities.isEmpty()) {
            StringBuilder updateSql = new StringBuilder("update contract_params set value=\'TCFPS\' where\n" +
                    "contract_id IN (");
            StringBuilder insertSql = new StringBuilder("insert into contract_params (contract_id,name,value) values ");
            contractEntities.forEach(contractEntity -> {
                int id = contractEntity.getId();
                int ufpsNumber = contractEntity.getUfpsNumber() != null ? contractEntity.getUfpsNumber() : 0;
                updateSql.append("\'" + id + "\', ");
                insertSql.append("(\'" + id + "\' , \'TCFPS_PAYER\', \'" + ufpsNumber + "\'),");
            });
            updateSql.deleteCharAt(updateSql.length() - 2);
            updateSql.append(") AND name = \'CONTRACT_PAYER\';");
            return String.valueOf(updateSql.append("\n" + insertSql.substring(0, insertSql.length() - 1)));
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
            updateSql.deleteCharAt(updateSql.length() - 2);
            updateSql.append(") and name = \'CONTRACT_PAYER\';");
            deleteSql.deleteCharAt(deleteSql.length() - 2);
            deleteSql.append(") and name = \'TCFPS_PAYER\';");
            return String.valueOf(updateSql + "\n" + deleteSql);
        }
        return "Нет подходящих договоров в базе 8===>";
    }
}
