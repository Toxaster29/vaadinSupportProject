package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.schedule.PublisherWithContract;
import com.packagename.myapp.spring.entity.subscription.Subscription;
import com.packagename.myapp.spring.entity.treatment.TreatmentEntity;
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

    private static String GET_SP5_FOR_ANNULMENT = "select s.sp5_number FROM public.subscriptions s\n" +
            "join bookings b on b.booking_id = s.booking_id\n" +
            "where s.publisher_id = '16e1ba3e-bb9a-4b9d-bd38-013a5797f200' and s.catalogue_id in ('255', '254', '261', '256', '262', '265', '183', '253', '184') and b.source_type = 2\n" +
            "and s.wagon = %s and s.place = %s and s.\"type\" = 'ANNULMENT'";

    private static String GET_SUBSCRIPTIONS_BY_TREATMENT = "select s.index, s.min_subs_period, s.alloc_oct, s.alloc_nov, s.alloc_dec, delivery_type, a.postal_code, a.region, a.district, \n" +
            "a.city, a.street, a.house, a.housing, a.building, a.flat,s.surname, s.\"name\", s.patronymic, s.org_name, s.start_date, s.alloc_jul,s.alloc_aug,s.alloc_sep FROM public.subscriptions s\n" +
            "join bookings b on b.booking_id = s.booking_id\n" +
            "join addresses a on a.address_id = s.address_id\n" +
            "where s.publisher_id = '16e1ba3e-bb9a-4b9d-bd38-013a5797f200' and s.catalogue_id in ('255', '254', '261', '256', '262', '265', '183', '253', '184') and b.source_type = 2\n" +
            "and s.wagon = %s and s.place = %s %s";

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

    @Override
    public List<Subscription> getSubscriptionWithoutAnnulment(TreatmentEntity entity) {
        List<Subscription> subscriptions = new ArrayList<>();
        String sp5List = "and sp5_number not in (";
        boolean hasAnnulment = false;
        try (Connection con = DriverManager.getConnection(urlSub, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(GET_SP5_FOR_ANNULMENT, entity.getWagon(), entity.getPlace()));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                sp5List += "\'" + rs.getString(1) + "\',";
                hasAnnulment = true;
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        String sql = String.format(GET_SUBSCRIPTIONS_BY_TREATMENT, entity.getWagon(),
                entity.getPlace(), hasAnnulment ? sp5List.substring(0, sp5List.length() - 1) + ")" : "");
        try (Connection con = DriverManager.getConnection(urlSub, user, passwd);
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Integer deliveryType = rs.getInt(6) == 0 ? 1 : 3;
                Integer sumAlloc = rs.getInt(3) + rs.getInt(4) + rs.getInt(5);
                if (sumAlloc > 0) {
                    subscriptions.add(new Subscription(rs.getString(1), rs.getInt(2), 0, 0, 0, 0,
                            0, 0, 0,0,0, rs.getInt(3), rs.getInt(4),
                            rs.getInt(5), sumAlloc, deliveryType, rs.getString(7), rs.getString(8),
                            rs.getString(9), rs.getString(10), "", rs.getString(11),
                            rs.getString(12), rs.getString(13), rs.getString(14),
                            rs.getString(15), rs.getString(16), rs.getString(17),
                            rs.getString(18), rs.getString(19), rs.getDate(20).toLocalDate()));
                }
            }
        } catch (SQLException ex) {
                System.err.println(ex.getMessage());
        }
        return subscriptions;
    }
}
