package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.euv.EuvStatisticEntity;
import com.packagename.myapp.spring.entity.euv.LogShpiAction;
import com.packagename.myapp.spring.entity.euv.ShpiTableEntity;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@Repository
public class SeuvDaoImpl implements SeuvDao {

    private static final String GET_ACTIONS_SQL = "SELECT r.code_shpi, r.create_date, r.system_id, r.server_id, a.\"name\"," +
            " a.status, r.description FROM public.log_record r, public.log_action a where r.code_shpi = \'%s\' and r.action_id = a.id";
    private static final  String GET_EUV_OPERATION_COUNT = "select count(*) from (select code_shpi from " +
            "log_record where create_date > \'%s\' and create_date < \'%s\' and system_id = \'EUV\' group by code_shpi) as shpi";
    private static final  String GET_EO_OPERATION_COUNT = "select count(*) from (select code_shpi from " +
            "log_record where create_date > \'%s\' and create_date < \'%s\' and system_id = \'EO\' group by code_shpi) as shpi";
    private static final  String GET_LOGIN_COUNT = "SELECT count(*) FROM public.audit_record where " +
            "action_id = 1 and created_date > \'%s\' and created_date < \'%s\'";
    private static final String GET_SHPI_WITHOUT_SYSTEM_ID = "select code_shpi, count(code_shpi), CASE WHEN" +
            " SUM(case when action_id in (2,3,4,6,8,9,12) then 1 else 0 end) > 0 THEN true ELSE false END AS status\n" +
            "from log_record  where create_date > \'%s\' and create_date < \'%s\' group by code_shpi";
    private static final String GET_SHPI_WITH_SYSTEM_ID = "select code_shpi, count(code_shpi), CASE WHEN " +
            "SUM(case when action_id in (2,3,4,6,8,9,12) then 1 else 0 end) > 0 THEN true ELSE false END AS status\n" +
            "from log_record  where create_date > \'%s\' and create_date < \'%s\' and system_id = \'%s\' group by code_shpi";

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

    @Override
    public EuvStatisticEntity getStatistic() {
        LocalDateTime start = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime end = LocalDateTime.now().plusDays(1).toLocalDate().atStartOfDay();

        return new EuvStatisticEntity(getEuvOperationCount(start, end),
                getEoOperationCount(start, end), getUserCount(start, end));
    }

    @Override
    public List<ShpiTableEntity> searchShpiByParams(LocalDate start, LocalDate end, Boolean euv, Boolean eo) {
        String sqlRequest = "";
        List<ShpiTableEntity> list = new ArrayList<>();
        if (!euv && !eo || euv && eo) {
            sqlRequest = String.format(GET_SHPI_WITHOUT_SYSTEM_ID, start.atStartOfDay(), end.plusDays(1).atStartOfDay());
        } else {
            sqlRequest = String.format(GET_SHPI_WITH_SYSTEM_ID, start.atStartOfDay(), end.plusDays(1).atStartOfDay(),
                    euv ? "EUV" : "EO");
        }
        try (Connection con = DriverManager.getConnection(url, user, passwd);
             PreparedStatement pst = con.prepareStatement(sqlRequest);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                list.add(new ShpiTableEntity(rs.getString(1), rs.getInt(2), rs.getBoolean(3)));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return list;
    }

    private Integer getUserCount(LocalDateTime start, LocalDateTime end) {
        Integer count = 0;
        try (Connection con = DriverManager.getConnection(url, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(GET_LOGIN_COUNT, start, end));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return count;
    }

    private Integer getEoOperationCount(LocalDateTime start, LocalDateTime end) {
        Integer count = 0;
        try (Connection con = DriverManager.getConnection(url, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(GET_EO_OPERATION_COUNT, start, end));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return count;
    }

    private Integer getEuvOperationCount(LocalDateTime start, LocalDateTime end) {
        Integer count = 0;
        try (Connection con = DriverManager.getConnection(url, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(GET_EUV_OPERATION_COUNT, start, end));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return count;
    }

}
