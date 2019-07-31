package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.ContractEntity;
import com.packagename.myapp.spring.entity.EntityFromTable;
import com.packagename.myapp.spring.entity.UfpsEntity;
import com.packagename.myapp.spring.service.ResourceService;
import com.packagename.myapp.spring.service.SQLGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

@Repository
public class TaskDao {

    @Autowired
    private static SQLGeneratorService sqlGeneratorService = new SQLGeneratorService();
    @Autowired
    private ResourceService resourceService;

    private static String url = "";
    private static String user = "";
    private static String passwd = "";
    private List<UfpsEntity> ufpsList = new ArrayList<>();

    public String getContractFromBDUFPS(List<EntityFromTable> entityFromTables) {
       return sqlGeneratorService.generateSqlForUFPS(getContractIDFromBD(entityFromTables, true));
    }

    public String getContractFromBDAUP(List<EntityFromTable> entityFromTables) {
       return sqlGeneratorService.generateSqlForAUP(getContractIDFromBD(entityFromTables, false));
    }

    private List<ContractEntity> getContractIDFromBD(List<EntityFromTable> entityFromTables, boolean isUfps) {
        List<ContractEntity> list = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(url, user, passwd);
             PreparedStatement pst = con.prepareStatement(sqlGeneratorService.generateSqlForContractGetQuery(entityFromTables));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                ContractEntity contract = new ContractEntity(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getInt(4),
                        rs.getString(6), rs.getInt(11), rs.getDate(7));
                if (isUfps) {
                    contract.setUfpsNumber(getUfpsNumberByHidAndDocNumber(contract.getLegalHid(),
                            contract.getDocNumber(), entityFromTables));
                }
                searchProblemData(contract, entityFromTables);
                list.add(contract);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return list;
    }

    private void searchProblemData(ContractEntity contract, List<EntityFromTable> entityFromTables) {
        entityFromTables.forEach(entity -> {
            if (entity.getDocNumber().equals(contract.getDocNumber()) && entity.getId().equals(contract.getLegalHid())) {
                DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
                try {
                    if (!dateFormat.parse(entity.getDocDate()).equals(contract.getDocDate())) {
                        entity.setHaveProblem(true);
                    } else entity.setHaveProblem(false);
                } catch (ParseException e) {
                    System.err.println(e.getMessage());
                }
            }
        });
    }

    private Integer getUfpsNumberByHidAndDocNumber(String legalHid, String docNumber, List<EntityFromTable> entityFromTables) {
        final String[] ufpsName = {""};
        final Integer[] ufpsNumber = {0};
        entityFromTables.forEach(entity -> {
            if (entity.getId().equals(legalHid) && entity.getDocNumber().equals(docNumber)) ufpsName[0] = entity.getPayer();
        });
        if (!ufpsName[0].isEmpty()) {
            ufpsList.forEach(ufpsEntity -> {
                if(ufpsEntity.getDescription().equals(ufpsName[0])) ufpsNumber[0] = Integer.parseInt(ufpsEntity.getId());
            });
        }
        return ufpsNumber[0];
    }

    @PostConstruct
    private void readProperties() {
        Properties props = new Properties();
        Path myPath = Paths.get("src/main/resources/application.properties");
        try {
            BufferedReader bf = Files.newBufferedReader(myPath, StandardCharsets.UTF_8);
            props.load(bf);
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
        url = props.getProperty("spring.datasource.url");
        user = props.getProperty("spring.datasource.username");
        passwd = props.getProperty("spring.datasource.password");
        ufpsList = resourceService.getUfpsEntityList();
    }
}
