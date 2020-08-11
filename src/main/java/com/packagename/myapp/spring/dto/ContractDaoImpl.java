package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.ContractSchedule;
import com.packagename.myapp.spring.entity.contract.ContractEntity;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class ContractDaoImpl implements ContractDao {

    private static String contractUrl = "jdbc:postgresql://localhost:5432/contract";
    private static String tictacUrl = "jdbc:postgresql://localhost:5432/subs_tictac";
    private static String user = "postgres";
    private static String passwd = "123";

    @Override
    public List<ContractEntity> getContractsWithActiveStatus() {
        List<ContractEntity> contractEntities = new ArrayList<>();
        String sql = "select id, legal_hid, \"year\", half from (SELECT * FROM public.contract " +
                "where (\"year\" = 2020 and half = 2) or (\"year\" = 2021 and half = 1)) as sel \n" +
                "where doc_type = 'DELIVERY'and status = 'ACTIVE'";
        try (Connection con = DriverManager.getConnection(contractUrl, user, passwd);
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                contractEntities.add(new ContractEntity(rs.getInt(1), rs.getString(2),
                       rs.getInt(3), rs.getInt(4)));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return contractEntities;
    }

    @Override
    public Map<String, ContractSchedule> getContractSchedulersForPeriod() {
        Map<String, ContractSchedule> contractScheduleMap = new HashMap<>();
        String sql = "SELECT * FROM public.contract_schedule where (\"year\" = 2020 and half_year = 2) or (\"year\" = 2021 and half_year = 1)";
        try (Connection con = DriverManager.getConnection(tictacUrl, user, passwd);
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {

                contractScheduleMap.put(rs.getString(5), new ContractSchedule(rs.getInt(1), rs.getString(2),
                        rs.getInt(3), rs.getInt(4), rs.getString(5)));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return contractScheduleMap;
    }
}
