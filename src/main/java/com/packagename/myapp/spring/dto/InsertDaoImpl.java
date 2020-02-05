package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.schedule.PublisherWithContract;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class InsertDaoImpl implements InsertDao {

    private static String contractUrl = "jdbc:postgresql://localhost:5432/contract";
    private static String urlCatalog = "jdbc:postgresql://localhost:5432/catalogue-service";
    private static String urlSub = "jdbc:postgresql://localhost:5432/sub_subscription_service";
    private static String tictacUrl = "jdbc:postgresql://localhost:5432/subs_tictac";
    private static String user = "postgres";
    private static String passwd = "123";

    private static String GET_ALL_PUBLISHERS = "SELECT distinct legal_hid FROM public.subscription_element where catalogue_for_period_id in (271,270)";

    private static String GET_CONTRACT_IF_FOR_PUBLISHER = "SELECT * FROM public.contract where legal_hid = '%s'\n" +
            "and \"year\" = 2020 and half = 2 and doc_type = 'DELIVERY' and status = 'ACTIVE'";

    private static String GET_IS_LOCAL_PARAMETER = "SELECT pi.place,pi.is_local FROM public.subscription_element se\n" +
            "join publication_info pi on se.id = pi.id\n" +
            "where se.catalogue_for_period_id in (271,270)\n" +
            "and se.legal_hid = '%s'";

    private static String GET_PUBLISHERS_WITH_SCHEDULE = "SELECT distinct publisher_id FROM public.contract_schedule where \"year\" = 2020 and half_year = 2";

    @Override
    public List<PublisherWithContract> getAllPublisherByYearAndHalf(List<String> publisherWithSchedule) {
        List<PublisherWithContract> publisherWithContracts = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(urlCatalog, user, passwd);
             PreparedStatement pst = con.prepareStatement(GET_ALL_PUBLISHERS);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                String hid = rs.getString(1);
                if (publisherWithSchedule.stream().noneMatch(id -> id.equals(hid))) {
                    publisherWithContracts.add(new PublisherWithContract(hid, null, null));
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return publisherWithContracts;
    }

    @Override
    public void setContractIdForPublisher(PublisherWithContract publisher) {
        try (Connection con = DriverManager.getConnection(contractUrl, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(GET_CONTRACT_IF_FOR_PUBLISHER, publisher.getHid()));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                publisher.setContractId(rs.getInt(1));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        try (Connection con = DriverManager.getConnection(urlCatalog, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(GET_IS_LOCAL_PARAMETER, publisher.getHid()));
             ResultSet rs = pst.executeQuery()) {
            publisher.setIsLocal(true);
            while (rs.next()) {
                if (rs.getString(1).equals("FEDERAL") || !rs.getBoolean(2)) {
                    publisher.setIsLocal(false);
                    break;
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @Override
    public List<String> getPublishersWithSchedule() {
        List<String> ids = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(tictacUrl, user, passwd);
             PreparedStatement pst = con.prepareStatement(GET_PUBLISHERS_WITH_SCHEDULE);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                ids.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return ids;
    }
}
