package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.LogShpiAction;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Repository
public class SeuvDaoImpl implements SeuvDao {

    private static final String GET_ACTIONS_SQL = "SELECT r.code_shpi, r.create_date, r.system_id, r.server_id, a.\"name\"," +
            " a.status, r.description FROM public.log_record r, public.log_action a where r.code_shpi = \'%s\' and r.action_id = a.id";

    private static String url = "";
    private static String user = "";
    private static String passwd = "";

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
        url = props.getProperty("seuv.url");
        user = props.getProperty("seuv.username");
        passwd = props.getProperty("seuv.password");
    }

    public List<LogShpiAction> getActionList(String shpiCode) {
        List<LogShpiAction> actions = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(url, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(GET_ACTIONS_SQL, shpiCode));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                LogShpiAction action = new LogShpiAction(rs.getString(1), rs.getTimestamp(2).toLocalDateTime(),
                        rs.getString(3), rs.getByte(4), rs.getString(5),
                        rs.getBoolean(6), rs.getString(7));
                actions.add(action);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return actions;
    }

}
