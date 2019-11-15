package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.parser.DirectoryData;
import com.packagename.myapp.spring.entity.parser.newFormat.Accept;
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

    @Override
    public List<Accept> getAcceptList() {
        List<Accept> acceptList = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(url, user, passwd);
             PreparedStatement pst = con.prepareStatement("SELECT * FROM accept");
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                acceptList.add(new Accept(rs.getInt(1), rs.getInt(2), rs.getString(3),
                        rs.getDate(4).toLocalDate(), rs.getDate(5).toLocalDate(), rs.getBoolean(6),
                        rs.getDate(7) != null ? rs.getDate(7).toLocalDate() : null, rs.getByte(8)));
            }

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return acceptList;
    }
}
