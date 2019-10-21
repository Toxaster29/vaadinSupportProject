package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.report.CatalogPublicationDate;
import com.packagename.myapp.spring.entity.report.SubscriptionsReportPart;
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

}
