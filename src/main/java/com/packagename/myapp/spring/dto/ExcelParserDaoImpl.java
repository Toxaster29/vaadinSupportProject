package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.excelParser.PublisherFromExcel;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class ExcelParserDaoImpl implements ExcelParserDao {

    private static String GET_CONTRACT_IDS = "select id from(SELECT * FROM public.contract where legal_hid =" +
            " '%s' and doc_type = 'DELIVERY' and status = 'DRAFT') as foo\n" +
            "where \"year\" = 2019 and half = 2 or \"year\" = 2020 and half = 1";
    private static String UPDATE_NMC_VALUE = "UPDATE public.contract_params \n" +
            "SET value='%s' \n" +
            "WHERE contract_id in (%s) and name = 'NMC'";

    private static String contractUrl = "jdbc:postgresql://localhost:8686/contract";
    private static String user = "postgres";
    private static String passwd = "123";

    @Override
    public List<Integer> setNmcToPublisher(PublisherFromExcel publisher) {
        List<Integer> ids = new ArrayList<>();
        try {
            Connection con = DriverManager.getConnection(contractUrl, user, passwd);
            PreparedStatement pst = con.prepareStatement(String.format(GET_CONTRACT_IDS, publisher.getHid()));
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                ids.add(rs.getInt(1));
            }
            con.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return ids;
    }

    @Override
    public void updateNmc(PublisherFromExcel publisher, List<Integer> ids) {
        String lineIds = getIdLine(ids);
        try {
            Connection con = DriverManager.getConnection(contractUrl, user, passwd);
            Statement st = con.createStatement();
            st.execute(String.format(UPDATE_NMC_VALUE, publisher.getPrice() + ".00", lineIds));
            con.close();
        }  catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    private String getIdLine(List<Integer> ids) {
        return ids.stream().map(String::valueOf).collect(Collectors.joining(","));
    }
}
