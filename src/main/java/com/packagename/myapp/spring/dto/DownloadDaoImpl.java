package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.avatar.Document;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class DownloadDaoImpl implements DownloadDao {

    private static String GET_ALL_DOCUMENT_FOR_OPERATION = "SELECT * FROM public.documents where request_id = '%s'";

    private static String urlAvatar = "jdbc:postgresql://localhost:5432/avatar";
    private static String user = "postgres";
    private static String passwd = "123";

    @Override
    public List<Document> getAllDocumentForOperationId(String operationId) {
        List<Document> documents = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(urlAvatar, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(GET_ALL_DOCUMENT_FOR_OPERATION, operationId));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                documents.add(new Document(rs.getString(1), rs.getString(2), rs.getString(3)));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return documents;
    }
}
