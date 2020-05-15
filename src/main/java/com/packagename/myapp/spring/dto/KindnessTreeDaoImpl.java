package com.packagename.myapp.spring.dto;

import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class KindnessTreeDaoImpl implements KindnessTreeDao {

    private static String url = "jdbc:postgresql://localhost:5432/kindnesstree";
    private static String user = "postgres";
    private static String passwd = "123";

    private static String orphanageType = "and acceptor_type = 'ORPHANAGE'";
    private static String schoolType = "and acceptor_type in ('LIBRARY','BOARDING_SCHOOL')";
    private static String agedType = "and acceptor_type in ('VETERAN_HOME','AGED_HOME')";
    private static String militaryType = "and acceptor_type = 'MILITARY'";

    private static String GET_ORGANIZATION_LIST_BY_TYPE_SQL = "SELECT acceptor_id from public.acceptors\n"+
            "where  active = true %s";
    private static String GET_BIDS_IDS = "SELECT bid_id FROM public.bids where state in (1,2) and \"year\" = 2020  and a_jul = 0";

    @Override
    public List<Integer> getOrganizationIdsByType(int i) {
        String typeSql = "";
        switch (i) {
            case 0:
                typeSql = orphanageType;
                break;
            case 1:
                typeSql = schoolType;
                break;
            case 2:
                typeSql = agedType;
                break;
            case 3:
                typeSql = militaryType;
                break;
        }
        List<Integer> ids = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(url, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(GET_ORGANIZATION_LIST_BY_TYPE_SQL, typeSql));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                ids.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return ids;
    }

    @Override
    public List<Integer> getActiveBidIds() {
        List<Integer> ids = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(url, user, passwd);
             PreparedStatement pst = con.prepareStatement(GET_BIDS_IDS);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                ids.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return ids;
    }
}
