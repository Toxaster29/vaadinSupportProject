package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.excelParser.PublisherFromExcel;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ExcelParserDaoImpl implements ExcelParserDao {

    private static String GET_PUBLISHER_HID = "SELECT legal_hid, \"name\", publisher_name FROM public.publisher where inn = '%s'";
    private static String GET_PUBLISHER_HID_BY_NAME = "SELECT legal_hid FROM public.publisher where \"name\" = '%s' or publisher_name = '%s'";

    private static String partnerstUrl = "jdbc:postgresql://localhost:5432/subs_partners";
    private static String contractUrl = "jdbc:postgresql://localhost:5432/contract";
    private static String user = "postgres";
    private static String passwd = "123";

    @Override
    public String getHidByPublisherParams(String inn, String publisherName) {
        List<PublisherFromExcel> publishers = new ArrayList<>();
        String hid = getHidByPublisherName(publisherName);
        if (hid != null) {
            return hid;
        }
        try (Connection con = DriverManager.getConnection(partnerstUrl, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(GET_PUBLISHER_HID, inn));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                publishers.add(new PublisherFromExcel(rs.getString(2), inn, null, rs.getString(1), rs.getString(3)));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        if (publishers.size() == 1) {
            return publishers.get(0).getHid();
        } else if(publishers.size() > 1) {
            return publishers.stream().filter(pb -> pb.getPublisherName().equals(publisherName.trim())
                    || pb.getSecondName().equals(publisherName.trim())).findFirst()
                    .orElse(new PublisherFromExcel()).getHid();
        }
        return null;
    }

    @Override
    public boolean setNmcToPublisher(PublisherFromExcel publisher) {
        return false;
    }

    private String getHidByPublisherName(String publisherName) {
        String hid = null;
        try (Connection con = DriverManager.getConnection(partnerstUrl, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(GET_PUBLISHER_HID_BY_NAME, publisherName.trim(),
                     publisherName.trim()));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                hid = rs.getString(1);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return hid;
    }
}
