package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.EntityFromTable;
import com.packagename.myapp.spring.service.SQLGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.List;
import java.util.Properties;

@Repository
public class TaskDao {

    @Autowired
    private static SQLGeneratorService sqlGeneratorService = new SQLGeneratorService();

    private static String url;
    private static String user;
    private static String passwd;

    public void getContractFromBDUFPS(List<EntityFromTable> entityFromTables) {
        readProperties();
        try (Connection con = DriverManager.getConnection(url, user, passwd);
             PreparedStatement pst = con.prepareStatement(sqlGeneratorService.generateSqlForContractGetQueryUFPS(entityFromTables));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                System.out.println(rs.getInt(1));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    public void getContractFromBDAUP(List<EntityFromTable> entityFromTables) {
        readProperties();
    }

    private static void readProperties() {
        Properties props = new Properties();
        Path myPath = Paths.get("src/main/resources/application.properties");
        try {
            BufferedReader bf = Files.newBufferedReader(myPath, StandardCharsets.UTF_8);
            props.load(bf);
        } catch (IOException ex) {
            System.err.println("Ошибка при получении свойств бд");
        }
        url = props.getProperty("spring.datasource.url");
        user = props.getProperty("spring.datasource.username");
        passwd = props.getProperty("spring.datasource.password");
    }
}
