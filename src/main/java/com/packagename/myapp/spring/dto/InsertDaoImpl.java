package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.schedule.PublisherData;
import com.packagename.myapp.spring.entity.schedule.PublisherWithContract;
import com.packagename.myapp.spring.entity.subscription.Subscription;
import com.packagename.myapp.spring.entity.treatment.TreatmentEntity;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class InsertDaoImpl implements InsertDao {

    private static String contractUrl = "jdbc:postgresql://localhost:5432/contract";
    private static String urlCatalog = "jdbc:postgresql://localhost:5432/catalogue-service";
    private static String urlSub = "jdbc:postgresql://localhost:5432/sub_subscription_service";
    private static String tictacUrl = "jdbc:postgresql://localhost:5432/tic_tac";
    private static String partnersUrl = "jdbc:postgresql://localhost:5432/partners";
    private static String user = "postgres";
    private static String passwd = "123";

    private static String GET_ALL_PUBLISHERS = "SELECT distinct legal_hid FROM public.subscription_element where catalogue_for_period_id in (277,276)";

    private static String GET_ALL_PUBLISHERS_BY_PERIODS =
            "SELECT distinct legal_hid FROM public.subscription_element where catalogue_for_period_id in (%s) and status = 'APPROVED'";

    private static String GET_CONTRACT_IF_FOR_PUBLISHER_OLD = "SELECT legal_hid FROM public.contract where legal_hid = '%s' \n" +
            "and \"year\" = %s and half = %s and doc_type = 'DELIVERY' and status = 'ACTIVE'";

    private static String GET_CONTRACT_IF_FOR_PUBLISHER = "SELECT legal_hid FROM public.contract where legal_hid in (%s)\n" +
            "and \"year\" = %s and half = %s and doc_type = 'DELIVERY' and status = 'ACTIVE'";

    private static String GET_IS_LOCAL_PARAMETER = "SELECT pi.place,pi.is_local FROM public.subscription_element se\n" +
            "join publication_info pi on se.id = pi.id\n" +
            "where se.catalogue_for_period_id in (277,276)\n" +
            "and se.legal_hid = '%s'";

    private static String GET_PUBLISHERS_WITH_SCHEDULE = "SELECT distinct publisher_id FROM public.contract_schedule where \"year\" = 2021 and half_year = 1";

    private static String GET_PUBLISHERS_WITH_SCHEDULE_BY_YEAR_AND_HALF =
            "SELECT distinct publisher_id FROM public.contract_schedule where \"year\" = %s and half_year = %s";

    private static String GET_PUBLISHERS_WITH_EMPTY_SCHEDULE_BY_YEAR_AND_HALF =
            "SELECT distinct publisher_id FROM public.contract_schedule where \"year\" = %s and half_year = %s and contract_id = '-'";

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

    private static String GET_ALL_PUBLICATION_CODE_FOR_PUBLISHER =
            "SELECT publication_code FROM public.subscription_element where catalogue_for_period_id in (%s) and status = 'APPROVED' and legal_hid = '%s'";

    private static String GET_PUBLISHER_DATA = "SELECT legal_hid, \"name\", manager FROM public.publisher where legal_hid = '%s'";

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
        int year = 2021;
        int half = 1;
        try (Connection con = DriverManager.getConnection(contractUrl, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(GET_CONTRACT_IF_FOR_PUBLISHER_OLD, publisher.getHid(), year, half));
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

    @Override
    public List<String> getPublishersWithScheduleByYearAndHalf(int year, int half) {
        List<String> publisherHids = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(tictacUrl, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(GET_PUBLISHERS_WITH_SCHEDULE_BY_YEAR_AND_HALF, year, half));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                publisherHids.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return publisherHids;
    }

    @Override
    public List<String> getAllWithoutScheduleByPeriod(String periods, List<String> publisherWithSchedule) {
        List<String> publisherHids = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(urlCatalog, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(GET_ALL_PUBLISHERS_BY_PERIODS, periods));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                String hid = rs.getString(1);
                if (publisherWithSchedule.stream().noneMatch(id -> id.equals(hid))) {
                    publisherHids.add(hid);
                }
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return publisherHids;
    }

    @Override
    public List<String> getPublishersWithEmptyScheduleByYearAndHalf(int year, int half) {
        List<String> publisherHids = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(tictacUrl, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(GET_PUBLISHERS_WITH_EMPTY_SCHEDULE_BY_YEAR_AND_HALF, year, half));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                publisherHids.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return publisherHids;
    }

    @Override
    public List<String> getPublisherWithContract(List<String> publisherWithEmptySchedule, int year, int half) {
        List<String> hids = new ArrayList<>();
        List<String> hidsForQuery = new ArrayList<>();
        publisherWithEmptySchedule.forEach(hid -> hidsForQuery.add("\'" + hid + "\'"));
        String hidsLine = StringUtils.join(hidsForQuery,",");
        try (Connection con = DriverManager.getConnection(contractUrl, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(GET_CONTRACT_IF_FOR_PUBLISHER, hidsLine, year, half));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                hids.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return hids;
    }

    @Override
    public PublisherData getPublisherDataByHid(String hid) {
        PublisherData publisherData = new PublisherData();
        try (Connection con = DriverManager.getConnection(partnersUrl, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(GET_PUBLISHER_DATA, hid));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                publisherData.setHid(rs.getString(1));
                publisherData.setName(rs.getString(2));
                publisherData.setManagerHid(rs.getString(3));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return publisherData;
    }

    @Override
    public List<String> getAllIndexForPublisherByHid(String hid, String periods) {
        List<String> indexes = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(urlCatalog, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(GET_ALL_PUBLICATION_CODE_FOR_PUBLISHER, periods, hid));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                indexes.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return indexes;
    }

    @Override
    public List<String> getAllLocalPublisher(List<String> publisherWithSchedule) {
        List<String> hidsForQuery = new ArrayList<>();
        for (String hid : publisherWithSchedule) {
            boolean isLocal = true;
            try (Connection con = DriverManager.getConnection(urlCatalog, user, passwd);
                 PreparedStatement pst = con.prepareStatement(String.format(GET_IS_LOCAL_PARAMETER, hid));
                 ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    if (rs.getString(1).equals("FEDERAL") || !rs.getBoolean(2)) {
                        isLocal = false;
                        break;
                    }
                }
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
            if(!isLocal) hidsForQuery.add(hid);
        }
        return hidsForQuery;
    }
}
