package com.packagename.myapp.spring.dto.report;

import com.packagename.myapp.spring.entity.report.CatalogPeriod;
import com.packagename.myapp.spring.entity.report.CatalogPublicationEntity;
import com.packagename.myapp.spring.entity.report.PlaceType;
import com.packagename.myapp.spring.entity.report.online.CatalogOnlineEntity;
import com.packagename.myapp.spring.entity.report.online.OnlineOrderInfo;
import com.packagename.myapp.spring.entity.report.online.OnlineSubscription;
import com.packagename.myapp.spring.entity.report.online.OrderElement;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.*;

@Repository
public class OnlineReportDaoImpl implements OnlineReportDao {

    private static String urlCatalog = "jdbc:postgresql://localhost:5432/catalogue-service";
    private static String urlSub = "jdbc:postgresql://localhost:5432/sub_subscription_service";
    private static String subsContextUrl = "jdbc:postgresql://localhost:5432/subscontext";
    private static String treatmentUrl = "jdbc:postgresql://localhost:5432/subs_treatment_service";
    private static String user = "postgres";
    private static String passwd = "123";

    private int[] getAllocationsFromString(String string) {
        String out = string.substring(1, string.length() - 1);
        String[] alloc = out.split(",");
        return  Arrays.asList(alloc).stream().mapToInt(Integer::parseInt).toArray();
    }

    @Override
    public List<CatalogPeriod> getPeriodList(String s) {
        String sql = "SELECT year, \"half\", id FROM public.catalog_period where %s";
        List<CatalogPeriod> catalogPeriods = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(urlCatalog, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(sql, s));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                catalogPeriods.add(new CatalogPeriod(rs.getInt(1),rs.getInt(2),rs.getInt(3)));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return catalogPeriods;
    }

    @Override
    public List<OnlineOrderInfo> getOnlineOrderInfo(Set<Integer> orderIdSet) {
        String sql = "SELECT order_id, hid FROM public.orders where order_id in (%s)";
        String ids = StringUtils.join(orderIdSet, ",");
        List<OnlineOrderInfo> onlineOrderInfoList = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(subsContextUrl, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(sql, ids));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                onlineOrderInfoList.add(new OnlineOrderInfo(rs.getInt(1), rs.getString(2), null));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return onlineOrderInfoList;
    }

    @Override
    public Map<String, Integer> getAllTreatmentByZipCodes(String zips) {
        Map<String, Integer> treatmentMap = new HashMap<>();
        String sql = "SELECT zl.zip_code, tn.tcfps_code FROM public.zip_links zl\n" +
                "join treatment_nodes  tn on zl.treatment_node_id = tn.treatment_node_id \n" +
                "where zl.zip_code in (%s) and zl.publication_type = 0";
        try (Connection con = DriverManager.getConnection(treatmentUrl, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(sql, zips));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                treatmentMap.put(rs.getString(1), rs.getInt(2));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return treatmentMap;
    }

    @Override
    public List<OnlineSubscription> getAllSubscriptionByPeriods(String period, int year, boolean equals) {
        List<OnlineSubscription> subscriptions = new ArrayList<>();
        String sql = "SELECT s.subscription_id,b.region_code,s.publisher_id,s.publication_code,s.\"index\",s.catalogue_id,s.min_subs_period,s.min_subs_price,\n" +
                "alloc_jan,alloc_feb,alloc_mar,alloc_apr,alloc_may,alloc_jun,alloc_jul,alloc_aug,alloc_sep,alloc_oct,alloc_nov,alloc_dec,s.alloc_by_msp, b.online_order_id\n" +
                "FROM public.subscriptions s\n" +
                "join bookings  b on b.booking_id = s.booking_id\n" +
                "where state in (2,4) and %s and s.catalogue_id in (%s)";
        try (Connection con = DriverManager.getConnection(urlSub, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(sql, equals ? "b.online" : "b.source_type = 0", period));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                subscriptions.add(new OnlineSubscription(rs.getInt(1), rs.getInt(2), rs.getString(3),
                        rs.getString(4), rs.getString(5), rs.getInt(6), rs.getInt(7),
                        rs.getDouble(8), new int[]{rs.getInt(9), rs.getInt(10), rs.getInt(11),
                        rs.getInt(12), rs.getInt(13), rs.getInt(14), rs.getInt(15),
                        rs.getInt(16), rs.getInt(17), rs.getInt(18), rs.getInt(19),
                        rs.getInt(20)}, getAllocationsFromString(rs.getString(21)), rs.getInt(22),
                        null, null, null, year, null, null, null, null, null));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return subscriptions;
    }

    @Override
    public List<OnlineSubscription> getOnlineSubsByTime(String startDate, String endDate) {
        List<OnlineSubscription> subscriptions = new ArrayList<>();
        String sql = "SELECT s.subscription_id,b.region_code,s.publisher_id,s.publication_code,s.\"index\",s.catalogue_id,s.min_subs_period,s.min_subs_price,\n" +
                "alloc_jan,alloc_feb,alloc_mar,alloc_apr,alloc_may,alloc_jun,alloc_jul,alloc_aug,alloc_sep,alloc_oct,alloc_nov,alloc_dec,s.alloc_by_msp," +
                "b.online_order_id, s.address_string , s.surname , s.\"name\" , s.patronymic,s.created_date\n" +
                "FROM public.subscriptions s\n" +
                "join bookings  b on b.booking_id = s.booking_id\n" +
                "where state in (2,4) and s.\"type\" = 'PRIMARY' and b.online = true and s.created_date between '%s' and '%s'";
        try (Connection con = DriverManager.getConnection(urlSub, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(sql, startDate, endDate));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                subscriptions.add(new OnlineSubscription(rs.getInt(1), rs.getInt(2), rs.getString(3),
                        rs.getString(4), rs.getString(5), rs.getInt(6), rs.getInt(7),
                        rs.getDouble(8), new int[]{rs.getInt(9), rs.getInt(10), rs.getInt(11),
                        rs.getInt(12), rs.getInt(13), rs.getInt(14), rs.getInt(15),
                        rs.getInt(16), rs.getInt(17), rs.getInt(18), rs.getInt(19),
                        rs.getInt(20)}, getAllocationsFromString(rs.getString(21)), rs.getInt(22),
                        null, null, null, 2020, null, null, rs.getString(23),
                        rs.getString(24) + " " + rs.getString(25) + " " + rs.getString(26), rs.getDate(27)));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return subscriptions;
    }

    @Override
    public List<OrderElement> getOnlineOrderElements(Set<Integer> orderIdSet) {
        String sql = "SELECT delivery_type,tcfps_code,publisher_id,publication_code,person_id,order_id,campaign_marker \n" +
                "FROM public.order_elements where order_id in (%s)";
        String ids = StringUtils.join(orderIdSet, ",");
        List<OrderElement> orderElements = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(subsContextUrl, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(sql, ids));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                orderElements.add(new OrderElement(rs.getInt(1), rs.getString(2), rs.getString(3),
                        rs.getString(4), rs.getString(5), rs.getInt(6), rs.getString(7)));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return orderElements;
    }

    @Override
    public List<CatalogOnlineEntity> getOnlineCatalog(String periods) {
        List<CatalogOnlineEntity> onlineEntities = new ArrayList<>();
        String sql = "SELECT catalogue_for_period_id,publication_code,title FROM public.subscription_element where catalogue_for_period_id in (%s)";
        try (Connection con = DriverManager.getConnection(urlCatalog, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(sql, periods));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
               onlineEntities.add(new CatalogOnlineEntity(rs.getInt(1), rs.getString(2), rs.getString(3)));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return onlineEntities;
    }
}
