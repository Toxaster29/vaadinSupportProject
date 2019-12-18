package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.treatment.TreatmentEntity;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TreatmentDaoImpl implements TreatmentDao {

    private static String url = "jdbc:postgresql://localhost:5432/subs_treatment_service";
    private static String user = "postgres";
    private static String passwd = "123";

    private static String GET_TREATMENT_BY_PARAMS = "select * from zip_links where treatment_node_id in " +
            "(select treatment_node_id from treatment_nodes where wagon = %s and place = %s) and publication_type = 0;";

    @Override
    public List<Integer> getTreatmentByParams(TreatmentEntity treatmentEntity) {
        List<Integer> codes = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(url, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(GET_TREATMENT_BY_PARAMS, treatmentEntity.getWagon(),
                     treatmentEntity.getPlace()));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                codes.add(Integer.parseInt(rs.getString(3)));
            }

        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return codes;
    }
}
