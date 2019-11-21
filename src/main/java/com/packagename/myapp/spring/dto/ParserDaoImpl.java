package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.parser.DirectoryData;
import com.packagename.myapp.spring.entity.parser.newFormat.Accept;
import com.packagename.myapp.spring.entity.parser.newFormat.ConnectionThematic;
import com.packagename.myapp.spring.entity.parser.newFormat.ConnectivityThematicEntity;
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

    @Override
    public List<ConnectionThematic> getConnectivityThematicEntities() {
        List<ConnectionThematic> connectivityThematicEntities = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(url, user, passwd);
             PreparedStatement pst = con.prepareStatement("SELECT * FROM connection_thematic");
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                connectivityThematicEntities.add(new ConnectionThematic(rs.getInt(1), rs.getString(2), rs.getString(3)));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return connectivityThematicEntities;
    }

    @Override
    public void uploadConnectionData(List<ConnectionThematic> connectionThematicList) {
        String sql = "INSERT INTO public.connection_thematic\n" +
                "(oldi_d, old_name, new_id)\n" +
                "VALUES \n";
        for (ConnectionThematic thematic : connectionThematicList) {
            sql += String.format(("(\'%s\',\'%s\',\'%s\'),"), thematic.getOldId(), thematic.getOldName(), thematic.getNewIds());
        }
        sql = sql.substring(0, sql.length() - 1);
        try {
            Connection con = DriverManager.getConnection(url, user, passwd);
            Statement st = con.createStatement();
            st.execute(sql);
            con.close();
        }  catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }
}
