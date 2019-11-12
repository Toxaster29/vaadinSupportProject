package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.parser.DirectoryData;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ParserDaoImpl implements ParserDao {

    private static String url = "jdbc:postgresql://localhost:5432/parser";
    private static String user = "postgres";
    private static String passwd = "123";

    @Override
    public List<DirectoryData> getDictionaryData() {
        List<DirectoryData> directoryData = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(url, user, passwd);
             PreparedStatement pst = con.prepareStatement("SELECT * FROM directory_data");
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                directoryData.add(new DirectoryData(rs.getInt(1), rs.getString(2), rs.getInt(3)));
            }

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return directoryData;
    }
}
