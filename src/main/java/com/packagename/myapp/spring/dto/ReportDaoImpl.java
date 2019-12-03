package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.report.*;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ReportDaoImpl implements ReportDao {

    private static String GET_SUBSCRIPTIONS_REQUEST = "select s.subscription_id, b.region_code, s.publisher_id , " +
            "s.publication_code, s.catalogue_id, s.min_subs_period, s.alloc_jan, s.alloc_feb,\n" +
            "s.alloc_mar, s.alloc_apr, s.alloc_may, s.alloc_jun FROM public.subscriptions s join bookings b on s.booking_id = b.booking_id \n" +
            "where b.online = false and not s.delivery_type = 2 and catalogue_id in (220, 219,180, 179,173,172) and not s.\"type\"= \'ANNULMENT\';";

    private static String GET_CATALOG_DATES_REQUEST = "SELECT rd.\"month\", rd.\"day\", rd.\"number\" FROM public.subscription_element se \n" +
            "join regional_option ro on se.id = ro.subscription_element_id \n" +
            "join regional_version rv on rv.id = ro.regional_version_id\n" +
            "join reg_version_details rd on rv.id = rd.reg_version_id\n" +
            "where se.legal_hid = \'%s\' and se.publication_code = \'%s\' and se.catalogue_for_period_id = %s " +
            "and ro.region_id = \'%s\' and rd.\"month\" < 6";

    private static String INSERT_REPORT_DATA_TO_DATABASE = "INSERT INTO public.report_table\n" +
            "(id, region_code, publisher_id, publication_code, catalog_id, count)\n" +
            "VALUES(%s, %s, \'%s\', \'%s\', %s, %s);";

    private static String GET_CATALOG_INFORMATION_FOR_PUBLICATION = "SELECT ro.region_id,\n" +
            "sum(case rd.\"month\" when 0 then 1 else 0 end) as Jan,  sum(case rd.\"month\" when 1 then 1 else 0 end) as FEB,\n" +
            "sum(case rd.\"month\" when 2 then 1 else 0 end) as Mar, sum(case rd.\"month\" when 3 then 1 else 0 end) as APR,\n" +
            "sum(case rd.\"month\" when 4 then 1 else 0 end) as MAY, sum(case rd.\"month\" when 5 then 1 else 0 end) as JUN,\n" +
            "sum(case rd.\"month\" when 6 then 1 else 0 end) as JUL, sum(case rd.\"month\" when 7 then 1 else 0 end) as AUG,\n" +
            "sum(case rd.\"month\" when 8 then 1 else 0 end) as SEP, sum(case rd.\"month\" when 9 then 1 else 0 end) as OCT,\n" +
            "sum(case rd.\"month\" when 10 then 1 else 0 end) as NOV, sum(case rd.\"month\" when 11 then 1 else 0 end) as \"dec\"\n" +
            "FROM public.subscription_element se\n" +
            "join regional_option ro on se.id = ro.subscription_element_id\n" +
            "join regional_version rv on rv.id = ro.regional_version_id\n" +
            "join reg_version_details rd on rv.id = rd.reg_version_id\n" +
            "where se.legal_hid = \'%s\' and  se.catalogue_for_period_id = %s and se.publication_code = \'%s\'\n" +
            "group by ro.region_id";

    private static String GET_PUBLOCATIONS_FOR_PUBLISHER = "select catalogue_for_period_id, publication_code, title from " +
            "subscription_element as se where legal_hid = '%s'\n" +
            "group by catalogue_for_period_id, publication_code, title";

    private static String GET_CATALOG_PERIOD_LIST = "SELECT year, \"half\", id FROM public.catalog_period" +
            " where year > 2016 and year < 2020 order by year, \"half\"";

    private static String GET_SUBSCRIPTION_BY_PUBLISHER = "SELECT b.region_code, s.publisher_id, s.publication_code, " +
            "s.catalogue_id, s.min_subs_period, alloc_by_msp\n" +
            "FROM public.subscriptions s join bookings b on s.booking_id = b.booking_id\n" +
            "where s.publication_code = \'%s\' and s.publisher_id = \'%s\' and s.type = 'PRIMARY' and s.catalogue_id = %s";

    private static String urlCatalog = "jdbc:postgresql://localhost:5432/catalogue-service";
    private static String urlSub = "jdbc:postgresql://localhost:5432/sub_subscription_service";
    private static String user = "postgres";
    private static String passwd = "123";

    @Override
    public List<SubscriptionsReportPart> getSubscriptionListForReport() {
        List<SubscriptionsReportPart> list = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(urlSub, user, passwd);
             PreparedStatement pst = con.prepareStatement(GET_SUBSCRIPTIONS_REQUEST);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                SubscriptionsReportPart subscription = new SubscriptionsReportPart(rs.getLong(1), rs.getInt(2),
                        rs.getString(3), rs.getString(4), rs.getInt(5), rs.getInt(6),
                        new Integer[]{rs.getInt(7), rs.getInt(8), rs.getInt(9), rs.getInt(10),
                                rs.getInt(11), rs.getInt(12)}, null);
                list.add(subscription);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return list;
    }

    @Override
    public List<CatalogPublicationDate> getCatalogDates(SubscriptionsReportPart element) {
        List<CatalogPublicationDate> list = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(urlCatalog, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(GET_CATALOG_DATES_REQUEST, element.getPublisherId(),
                     element.getPublicationCode(), element.getCatalogId(), element.getRegionCode()));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                CatalogPublicationDate publicationDate = new CatalogPublicationDate(rs.getInt(2), rs.getInt(1), rs.getString(3));
                list.add(publicationDate);
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return list;
    }

    @Override
    public void addDateListToDatabase(SubscriptionsReportPart element) throws SQLException {
        Connection con = DriverManager.getConnection(urlSub, user, passwd);
        Statement st = con.createStatement();
        st.execute(String.format(INSERT_REPORT_DATA_TO_DATABASE, element.getSubscriptionId(), element.getRegionCode(),
                element.getPublisherId(), element.getPublicationCode(), element.getCatalogId(), element.getCount()));
        con.close();
    }

    @Override
    public List<CatalogPublicationList> getCatalogPublications(String publisherId) {
        List<CatalogPublicationList> publicationList = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(urlCatalog, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(GET_PUBLOCATIONS_FOR_PUBLISHER, publisherId));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                publicationList.add(new CatalogPublicationList(rs.getString(2), rs.getInt(1),
                        rs.getString(3), null, null));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return publicationList;
    }

    @Override
    public List<PublicationInfoList> getPublicationOutputInfo(String publisherId, Integer periodId, String publicationCode) {
        List<PublicationInfoList> publicationInfoList = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(urlCatalog, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(GET_CATALOG_INFORMATION_FOR_PUBLICATION,
                     publisherId, periodId, publicationCode));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Integer[] output = {rs.getInt(2), rs.getInt(3), rs.getInt(4), rs.getInt(5),
                        rs.getInt(6), rs.getInt(7), rs.getInt(8), rs.getInt(9),
                        rs.getInt(10), rs.getInt(11), rs.getInt(12), rs.getInt(13)};
                publicationInfoList.add(new PublicationInfoList(rs.getInt(1), output));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return publicationInfoList;
    }

    @Override
    public List<CatalogPeriodEntity> getCatalogPeriodList() {
        List<CatalogPeriodEntity> periodEntityList = new ArrayList<>();
        Integer year = 2017;
        Integer half = 1;
        List<Integer> periodIds = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(urlCatalog, user, passwd);
             PreparedStatement pst = con.prepareStatement(GET_CATALOG_PERIOD_LIST);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                if (year == rs.getInt(1) && half == rs.getInt(2)) {
                    periodIds.add(rs.getInt(3));
                } else {
                    Integer[] periods = periodIds.toArray(new Integer[periodIds.size()]);
                    periodEntityList.add(new CatalogPeriodEntity(year, half, periods, new ArrayList<>()));
                    year = rs.getInt(1);
                    half = rs.getInt(2);
                    periodIds.clear();
                    periodIds.add(rs.getInt(3));
                }
            }
            if (!periodIds.isEmpty()) {
                Integer[] periods = periodIds.toArray(new Integer[periodIds.size()]);
                periodEntityList.add(new CatalogPeriodEntity(year, half, periods, new ArrayList<>()));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return periodEntityList;
    }

    @Override
    public List<SubscriptionByPublisher> getSubscriptionList(String publisherId, String publicationCode, Integer periodId) {
        List<SubscriptionByPublisher> list = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(urlSub, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(GET_SUBSCRIPTION_BY_PUBLISHER, publicationCode,
                     publisherId, periodId));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Integer[] allocations = new Integer[12];
                String allocByMsp = rs.getString(6);
                String[] allocs = allocByMsp.substring(1, allocByMsp.length() - 1).split(",");
                for (int i = 0; i < 12; i++) {
                    allocations[i] = Integer.parseInt(allocs[i]);
                }
                list.add(new SubscriptionByPublisher(rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getInt(4), rs.getInt(5), allocations));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return list;
    }

}
