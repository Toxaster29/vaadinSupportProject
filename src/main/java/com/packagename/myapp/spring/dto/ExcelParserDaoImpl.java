package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.contract.ContractEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class ExcelParserDaoImpl implements ExcelParserDao {

    private static String UPDATE_NMC_VALUE = "UPDATE public.contract_params \n" +
            "SET value='%s' \n" +
            "WHERE contract_id in (%s) and name = 'NMC'";

    private static String contractUrl = "jdbc:postgresql://localhost:5432/contract";
    private static String user = "postgres";
    private static String passwd = "123";

    @Override
    public void updateNmc(Integer price, Set<Integer> ids) {
        String lineIds = StringUtils.join(ids, ",");
        try {
            Connection con = DriverManager.getConnection(contractUrl, user, passwd);
            Statement st = con.createStatement();
            st.execute(String.format(UPDATE_NMC_VALUE, price + ".00", lineIds));
            con.close();
        }  catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @Override
    public List<ContractEntity> getContractForNmcUpdate(int year, int half) {
        List<ContractEntity> contractList = new ArrayList<>();
        String sql = "SELECT id, legal_hid,doc_number,doc_date,status FROM public.contract\n" +
                "where \"year\" = %s and half = %s and doc_type = 'DELIVERY'";
        try {
            Connection con = DriverManager.getConnection(contractUrl, user, passwd);
            PreparedStatement pst = con.prepareStatement(String.format(sql, year, half));
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                contractList.add(new ContractEntity(rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getDate(4), rs.getString(5)));
            }
            con.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return contractList;
    }
}
