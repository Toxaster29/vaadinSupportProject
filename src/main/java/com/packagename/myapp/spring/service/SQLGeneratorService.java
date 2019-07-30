package com.packagename.myapp.spring.service;

import com.packagename.myapp.spring.entity.EntityFromTable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SQLGeneratorService {

    public SQLGeneratorService() {
    }

    public String generateSqlForContractGetQueryUFPS(List<EntityFromTable> fromTableList) {
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
        return String.valueOf(prepareSql + ";");
    }

    public String generateSqlForContractGetQueryAUP(List<EntityFromTable> fromTableList) {
        if (fromTableList.size() > 1) {
            for (int x = 1; x < fromTableList.size(); x++) {

            }
        }
        return "";
    }

}
