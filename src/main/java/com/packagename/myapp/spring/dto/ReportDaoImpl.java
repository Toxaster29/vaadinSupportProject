package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.report.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

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

    private static String GET_CATALOG_INFORMATION_FOR_ALL_PUBLISHER = "select foo.id,foo.legal_hid, foo.title, foo.publication_code, foo.catalogue_for_period_id, foo.is_local, foo.place, \n" +
            "sum(case foo.\"month\" when 0 then 1 else 0 end) as Jan,  sum(case foo.\"month\" when 1 then 1 else 0 end) as FEB,\n" +
            "sum(case foo.\"month\" when 2 then 1 else 0 end) as Mar, sum(case foo.\"month\" when 3 then 1 else 0 end) as APR,\n" +
            "sum(case foo.\"month\" when 4 then 1 else 0 end) as MAY, sum(case foo.\"month\" when 5 then 1 else 0 end) as JUN,\n" +
            "sum(case foo.\"month\" when 6 then 1 else 0 end) as JUL, sum(case foo.\"month\" when 7 then 1 else 0 end) as AUG,\n" +
            "sum(case foo.\"month\" when 8 then 1 else 0 end) as SEP, sum(case foo.\"month\" when 9 then 1 else 0 end) as OCT,\n" +
            "sum(case foo.\"month\" when 10 then 1 else 0 end) as NOV, sum(case foo.\"month\" when 11 then 1 else 0 end) as \"dec\", foo.social, foo.discount\n" +
            "from (\n" +
            "SELECT se.id ,se.legal_hid, se.title, se.publication_code, se.catalogue_for_period_id, pi.is_local, pi.place, rd.\"month\", se.social, pi.discount \n" +
            "FROM public.subscription_element se \n" +
            "join regional_option ro on se.id = ro.subscription_element_id\n" +
            "join regional_version rv on rv.id = ro.regional_version_id\n" +
            "join reg_version_details rd on rv.id = rd.reg_version_id\n" +
            "join publication_info pi on pi.id = se.id\n" +
            "where  se.catalogue_for_period_id in (%s) \n" +
            "and status = 'APPROVED'\n" +
            "and not se.legal_hid = ''\n" +
            "group by se.id, se.legal_hid, se.title, se.publication_code, se.catalogue_for_period_id,pi.is_local, pi.place, rd.id, se.social, pi.discount)as foo\n" +
            "group by foo.id ,foo.legal_hid, foo.title, foo.publication_code, foo.catalogue_for_period_id, foo.is_local, foo.place, foo.social, foo.discount";

    private static String GET_PUBLOCATIONS_FOR_PUBLISHER = "select catalogue_for_period_id, publication_code, title from " +
            "subscription_element as se where legal_hid = '%s'\n" +
            "group by catalogue_for_period_id, publication_code, title";

    private static String GET_CATALOG_PERIOD_LIST = "SELECT year, \"half\", id FROM public.catalog_period\" +\n" +
            "            \" where year > 2016 and year < 2020 order by year, \"half\"";

    private static String GET_CATALOG_PERIOD_LIST_FOR_YEAR = "SELECT year, \"half\", id FROM public.catalog_period" +
            " where year = %s order by year, \"half\"";

    private static String GET_SUBSCRIPTION_BY_PUBLISHER = "SELECT b.region_code, s.publisher_id, s.publication_code, " +
            "s.catalogue_id, s.min_subs_period, alloc_by_msp\n" +
            "FROM public.subscriptions s join bookings b on s.booking_id = b.booking_id\n" +
            "where s.publication_code = \'%s\' and s.publisher_id = \'%s\' and s.type = 'PRIMARY' and s.catalogue_id = %s";

    private static String GET_SUBSCRIPTION_OUTPUT = "select alloc_by_msp FROM public.subscriptions_for_reports where publisher_id " +
            "= '%s' and publication_code = '%s' and catalogue_id = %s and \"index\" = '%s'";

    private static String GET_CATALOG_PRICE = "SELECT si.code, min_price, publisher_selling_price, catalogue_msp_price_no_vat, cp.selling_issue_price_no_vat, ro.vat, pg.subs_element_id " +
            "FROM public.price_group pg\n" +
            "join catalogue_prices cp on pg.id = cp.price_group_id\n" +
            "join regional_option ro on pg.id = ro.price_group_id\n" +
            "join subscription_index si on cp.index_id = si.id\n" +
            "where pg.subs_element_id = %s\n" +
            "group by pg.subs_element_id, si.code, min_price, publisher_selling_price, catalogue_msp_price_no_vat, cp.selling_issue_price_no_vat, ro.vat";

    private static String GET_ALL_REPORT_ROWS = "select distinct legal_hid from report";
    private static String GET_ALL_REPORT_ROWS_IDS = "select distinct id from report";

    private static String UPDATE_REPORT_DATA = "UPDATE public.report\n" +
            "SET delivery_type='%s' \n" +
            "where legal_hid = '%s';";
    private static String UPDATE_REPORT_DATA_REGION = "UPDATE public.report\n" +
            "SET region='%s' \n" +
            "where id = '%s';";

    private static String GET_REGION_PLACE_BY_ID = "SELECT place, is_local FROM public.publication_info where id = %s";

    private static String GET_DELIVERY_TYPES = "SELECT * FROM public.delivery_info WHERE legal_hid = '%s'";

    private static String contractUrl = "jdbc:postgresql://localhost:5432/contract";
    private static String urlCatalog = "jdbc:postgresql://localhost:5432/catalogue-service";
    private static String urlSub = "jdbc:postgresql://localhost:5432/sub_subscription_service";
    private static String subsContextUrl = "jdbc:postgresql://localhost:5432/subscontext";
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

    @Override
    public List<CatalogPeriod> getPeriodList(Integer year, Integer half) {
        List<CatalogPeriod> catalogPeriods = new ArrayList<>();
        String halfAndYear = half == null ? String.valueOf(year) : year + "and \"half\" = " + half;
        try (Connection con = DriverManager.getConnection(urlCatalog, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(GET_CATALOG_PERIOD_LIST_FOR_YEAR, halfAndYear));
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
    public List<CatalogPublicationEntity> getCatalogPublicationInfo(String periods) {
        List<CatalogPublicationEntity> catalogPublicationEntities = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(urlCatalog, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(GET_CATALOG_INFORMATION_FOR_ALL_PUBLISHER, periods));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Integer[] output = {rs.getInt(8), rs.getInt(9), rs.getInt(10), rs.getInt(11),
                        rs.getInt(12), rs.getInt(13), rs.getInt(14), rs.getInt(15),
                        rs.getInt(16), rs.getInt(17), rs.getInt(18), rs.getInt(19)};
                catalogPublicationEntities.add(new CatalogPublicationEntity(rs.getInt(1), rs.getString(2),
                        rs.getString(3), rs.getString(4), rs.getInt(5),output, null,
                        null, new PlaceType(rs.getString(7), rs.getBoolean(6)), rs.getBoolean(20),
                        rs.getDouble(21)));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return catalogPublicationEntities;
    }

    @Override
    public List<String> getSubscriptionOutputListForPublication(String legalHid, String publicationCode, Integer periodId,
                                                                String index) {
        List<String> outputs = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(urlSub, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(GET_SUBSCRIPTION_OUTPUT, legalHid, publicationCode, periodId, index));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                outputs.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return outputs;
    }

    @Override
    public List<CatalogPrice> getCatalogPricesByElementId(Integer id) {
        List<CatalogPrice> prices = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(urlCatalog, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(GET_CATALOG_PRICE, id));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                prices.add(new CatalogPrice(rs.getString(1), rs.getDouble(2),
                        rs.getDouble(3), rs.getDouble(4), rs.getDouble(5),
                        rs.getString(6), rs.getInt(7), null));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return prices;
    }

    @Override
    public void insertCatalogData(CatalogPublicationEntity entity, CatalogPrice price, Integer half) {

        String sql = "INSERT INTO public.report\n" +
                "(legal_hid, \"index\", id, publisher_name, half, msp_price_without_vat, msp_price_with_vat, issue_price_without_vat, issue_price_with_vat, total_count, \"output\")\n" +
                "VALUES('%s','%s',%s,'%s',%s, %s,'%s',%s,%s,%s,%s);\n";
        try {
            Connection con = DriverManager.getConnection(contractUrl, user, passwd);
            Statement st = con.createStatement();
            if (price.getMspPriceNoVat() > 0) {
                st.execute(String.format(sql, entity.getLegalHid(), price.getIndex(), entity.getId(), entity.getTitle(), half,
                        price.getMspPriceNoVat(), getPriceWithVat(price.getMspPriceNoVat(), price.getVat()), price.getIssuePriceNoVat(),
                        getPriceWithVat(price.getIssuePriceNoVat(), price.getVat()), entity.getCirculation(), entity.getOutputCount()));
            } else {
                st.execute(String.format(sql, entity.getLegalHid(), price.getIndex(), entity.getId(), entity.getTitle(), half,
                         getPriceWithoutVat(price.getMspPrice(), price.getVat()), price.getMspPrice(),
                        getPriceWithoutVat(price.getIssuePrice(), price.getVat()), price.getIssuePrice(), entity.getCirculation(), entity.getOutputCount()));
            }
            con.close();
        }  catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @Override
    public List<String> getReportPublishers() {
        List<String> reportData = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(contractUrl, user, passwd);
             PreparedStatement pst = con.prepareStatement(GET_ALL_REPORT_ROWS);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
               reportData.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return reportData;
    }

    @Override
    public void addReportParams(String publisher) {
        List<DeliveryInfo> deliveryList = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(urlCatalog, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(GET_DELIVERY_TYPES, publisher));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                deliveryList.add(new DeliveryInfo(rs.getString(1), rs.getInt(2), rs.getString(3)));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        try (Connection con = DriverManager.getConnection(urlCatalog, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(GET_DELIVERY_TYPES, publisher));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                deliveryList.add(new DeliveryInfo(rs.getString(1), rs.getInt(2), rs.getString(3)));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        String deliveryType = deliveryList.stream().filter(info -> info.getPeriodId().equals(Integer.valueOf(173)) ||
                info.getPeriodId().equals(Integer.valueOf(184))).findFirst().orElse(new DeliveryInfo("1111", 111, "")).getType();
        //173,184 -2019
        //87,102 -2018
        try {
            Connection con = DriverManager.getConnection(contractUrl, user, passwd);
            Statement st = con.createStatement();
            st.execute(String.format(UPDATE_REPORT_DATA, getDelivery(deliveryType),publisher));
            con.close();
        }  catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @Override
    public List<Integer> getReportElements() {
        List<Integer> ids = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(contractUrl, user, passwd);
             PreparedStatement pst = con.prepareStatement(GET_ALL_REPORT_ROWS_IDS);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                ids.add(rs.getInt(1));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return ids;
    }

    @Override
    public void addReportParamsRegion(Integer id) {
        PlaceType type = new PlaceType();
        try {
            Connection con = DriverManager.getConnection(urlCatalog, user, passwd);
            PreparedStatement pst = con.prepareStatement(String.format(GET_REGION_PLACE_BY_ID, id));
            ResultSet rs = pst.executeQuery();
            rs.next();
            type.setType(rs.getString(1));
            type.setIsLocal(rs.getBoolean(2));
            con.close();
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        try {
            Connection con = DriverManager.getConnection(contractUrl, user, passwd);
            Statement st = con.createStatement();
            st.execute(String.format(UPDATE_REPORT_DATA_REGION, getPayer(type), id));
            con.close();
        }  catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @Override
    public List<ChildrenSubscriptionEntity> getSubscriptionDataForChildrenReport() {
        List<ChildrenSubscriptionEntity> subscriptionEntities = new ArrayList<>();
        String sql = "SELECT publication_code, \"index\", sum(price_for_period) FROM public.subscriptions s\n" +
                "join bookings b on b.booking_id = s.booking_id\n" +
                "where b.source_type = 0 and s.created_date between '2019-11-01 00:00:00' \n" +
                "and '2019-12-31 23:59:59' and b.state in (2,4)and s.catalogue_id in %s\n" +
                "group by publication_code, \"index\"";
        String catalogIds2019y1h = "('173','220','172','185','186','180','179','219')";
        String catalogIds2020y1h = "('263','269','268','272','267','266','264')";
        try (Connection con = DriverManager.getConnection(urlSub, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(sql, catalogIds2020y1h));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                subscriptionEntities.add(new ChildrenSubscriptionEntity(rs.getString(1), rs.getString(2),
                        BigDecimal.valueOf(rs.getDouble(3)), 0));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return subscriptionEntities;
    }

    @Override
    public void setSubscriptionMonthCount(ChildrenSubscriptionEntity entity) {
        List<String> allocs = new ArrayList<>();
        String sql = "SELECT alloc_by_msp FROM public.subscriptions s\n" +
                "join bookings b on b.booking_id = s.booking_id\n" +
                "where b.source_type = 0 and s.publication_code = '%s' and s.\"index\" = '%s'\n" +
                "and s.created_date between '2019-11-01 00:00:00' and '2019-12-31 23:59:59' and b.state in (2,4)\n" +
                "and s.catalogue_id in %s";
        String catalogIds2019y1h = "('173','220','172','185','186','180','179','219')";
        String catalogIds2020y1h = "('263','269','268','272','267','266','264')";
        try (Connection con = DriverManager.getConnection(urlSub, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(sql, entity.getPublicationCode(), entity.getIndex(), catalogIds2020y1h));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                allocs.add(rs.getString(1));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        Integer count = 0;
        for (String output : allocs) {
            String out = output.substring(1, output.length() - 1);
            String[] alloc = out.split(",");
            for (int i = 0; i < alloc.length; i++) count+= Integer.valueOf(alloc[i]);
        }
        entity.setTotalCount(count);
    }

    @Override
    public List<OnlineReportEntity> getOnlineReportEntities() {
        List<OnlineReportEntity> onlineReportEntities = new ArrayList<>();
        String sql = "select \"index\", publication_code, price_for_period, alloc_by_msp,b.region_code, address_string, b.online_order_id, catalogue_id\n" +
                "FROM subscriptions AS s join bookings AS b ON (s.booking_id = b.booking_id)\n" +
                "where s.\"type\" = 'PRIMARY' and b.source_type = 0 and s.created_date between '2019-01-01 00:00:01' and '2019-06-30 23:59:59'";
        try (Connection con = DriverManager.getConnection(urlSub, user, passwd);
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                Integer count = getCountByAlloc(rs.getString(4));
                BigDecimal sum  = rs.getBigDecimal(3);
                onlineReportEntities.add(new OnlineReportEntity(rs.getString(1), rs.getString(2),
                        null, sum, count, rs.getString(5), rs.getString(6), null,
                        rs.getInt(7), rs.getInt(8)));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return onlineReportEntities;
    }

    @Override
    public Map<Integer, String> getOnlineOrderHids(Set<Integer> set) {
        Map<Integer, String> hids = new HashMap<>();
        String sql = "SELECT order_id, hid FROM public.orders where order_id in (%s)";
        String filer = StringUtils.join(set,",");
        try (Connection con = DriverManager.getConnection(subsContextUrl, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(sql, filer));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                hids.put(rs.getInt(1), rs.getString(2));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return hids;
    }

    @Override
    public Map<String, List<CatalogElement>> getPublicationMap(Set<String> publicationSet) {
        Map<String, List<CatalogElement>> map = new HashMap<>();
        String sql = "SELECT publication_code, catalogue_for_period_id, title FROM public.subscription_element where publication_code = '%s'";
        for (String code : publicationSet) {
            List<CatalogElement> catalogElements = new ArrayList<>();
            try (Connection con = DriverManager.getConnection(urlCatalog, user, passwd);
                 PreparedStatement pst = con.prepareStatement(String.format(sql, code));
                 ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    catalogElements.add(new CatalogElement(rs.getString(1), rs.getInt(2), rs.getString(3)));
                }
            } catch (SQLException ex) {
                System.err.println(ex.getMessage());
            }
            map.put(code, catalogElements);
        }
        return map;
    }

    @Override
    public List<Subscription> getAllSubscriptionsForPeriod(String periods) {
        List<Subscription> subscriptions = new ArrayList<>();
        String sql = "SELECT s.subscription_id, b.region_code, s.publisher_id, s.publication_code, s.\"index\", s.catalogue_id, s.min_subs_period, s.alloc_by_msp \n" +
                "FROM public.subscriptions s join bookings b on b.booking_id = s.booking_id\n" +
                "where b.state in (2,4) and s.catalogue_id in (%s)";
        try (Connection con = DriverManager.getConnection(urlSub, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(sql, periods));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                subscriptions.add(new Subscription(rs.getInt(1), rs.getInt(2), rs.getString(3),
                        rs.getString(4), rs.getString(5), rs.getInt(6), rs.getInt(7),
                        getAllocationsFromString(rs.getString(8)), null));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return subscriptions;
    }

    @Override
    public List<CatalogPrice> getAllCatalogPricesForPublications(List<CatalogPublicationEntity> publicationEntities) {
        List<CatalogPrice> prices = new ArrayList<>();
        String sql = "SELECT si.code, min_price, publisher_selling_price, catalogue_msp_price_no_vat, cp.selling_issue_price_no_vat," +
                " ro.vat, pg.subs_element_id, ARRAY_AGG (ro.region_id) " +
                "FROM public.price_group pg\n" +
                "join catalogue_prices cp on pg.id = cp.price_group_id\n" +
                "join regional_option ro on pg.id = ro.price_group_id\n" +
                "join subscription_index si on cp.index_id = si.id\n" +
                "where pg.subs_element_id in (%s)\n" +
                "group by pg.subs_element_id, si.code, min_price, publisher_selling_price, catalogue_msp_price_no_vat, cp.selling_issue_price_no_vat, ro.vat";
        List<Integer> integerIds = publicationEntities.stream().map(CatalogPublicationEntity::getId).collect(Collectors.toList());
        String ids = StringUtils.join(integerIds, ",");
        try (Connection con = DriverManager.getConnection(urlCatalog, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(sql, ids));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                prices.add(new CatalogPrice(rs.getString(1), rs.getDouble(2),
                        rs.getDouble(3), rs.getDouble(4), rs.getDouble(5),
                        rs.getString(6), rs.getInt(7), getAllocationsFromString(rs.getString(8))));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return prices;

    }

    private int[] getAllocationsFromString(String string) {
        String out = string.substring(1, string.length() - 1);
        String[] alloc = out.split(",");
        return  Arrays.asList(alloc).stream().mapToInt(Integer::parseInt).toArray();
    }

    private Integer getCountByAlloc(String string) {
        Integer count = 0;
        String out = string.substring(1, string.length() - 1);
        String[] alloc = out.split(",");
        for (int i = 0; i < alloc.length; i++) {
                count += Integer.parseInt(alloc[i]);
        }
        return count;
    }

    public String getPayer(PlaceType type) {
        String value = "";
        switch (type.getType()) {
            case "FEDERAL":
                value = "Федеральное";
                break;
            case "REGIONAL":
                if(type.getIsLocal()) {
                    value = "Районное";
                } else value = "Региональное";
                break;
        }
        return value;
    }

    @Override
    public List<CatalogPriceWithService> getAllCatalogPricesForPublicationsWithServicePrice(List<CatalogPublicationEntity> publicationEntities) {

        String sql = "SELECT se.id,si.code,ip.price_min_catalogue,ip.publisher_selling_price,ip.catalogue_msp_price_no_vat,ip.selling_issue_price_no_vat,\n" +
                "zp.service_home,zp.service_msp_price_no_vat_home,ro.vat,ip.region_id \n" +
                "FROM subscription_element se\n" +
                "join regional_option ro on se.id = ro.subscription_element_id \n" +
                "join subscription_index si on se.id = si.subs_element_id \n" +
                "join index_price ip on ip.subs_index_id = si.id and ro.region_id = ip.region_id \n" +
                "join zone_price zp on zp.index_price_id = ip.id \n" +
                "where se.id in (%s)\n" +
                "group by se.id , si.code, ro.vat,zp.service_home, zp.service_msp_price_no_vat_home,ip.price_min_catalogue,ip.publisher_selling_price,\n" +
                "ip.catalogue_msp_price_no_vat,ip.selling_issue_price_no_vat,ip.region_id\n" +
                "order by si.code,ip.region_id";
        List<CatalogPriceWithService> priceWithServices = new ArrayList<>();
        List<Integer> integerIds = publicationEntities.stream().map(CatalogPublicationEntity::getId).collect(Collectors.toList());
        String ids = StringUtils.join(integerIds, ",");
        try (Connection con = DriverManager.getConnection(urlCatalog, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(sql, ids));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                priceWithServices.add(new CatalogPriceWithService(rs.getInt(1), rs.getString(2), rs.getDouble(3),
                        rs.getDouble(4), rs.getDouble(5), rs.getDouble(6), rs.getDouble(7),
                        rs.getDouble(8), rs.getString(9), rs.getString(10)));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return priceWithServices;
    }

    public String getDelivery(String deliveryType) {
        String value = "";
        switch (deliveryType) {
            case "CENTRALIZED":
                value = "централизованная";
                break;
            case "DEFAULT":
                value = "децентрализованная";
                break;
        }
        return value;
    }

    public Double getPriceWithoutVat(Double issuePrice, String vat) {
        Double totalPrice = new Double(issuePrice);
        switch (vat) {
            case "ZERO":
                break;
            case "TEN":
                totalPrice = (totalPrice / 100) * 90;
                break;
            case "EIGHTEEN":
                totalPrice = (totalPrice / 100) * 82;
                break;
            case "TWENTY":
                totalPrice = (totalPrice / 100) * 80;
                break;
        }
        return totalPrice;
    }

    @Override
    public List<DeliveryInfo> getDeliveryInfoForPeriod(String periods) {
        List<DeliveryInfo> deliveryInfos = new ArrayList<>();
        String sql = "SELECT * FROM public.delivery_info where cfp_id in (%s)";
        try (Connection con = DriverManager.getConnection(urlCatalog, user, passwd);
             PreparedStatement pst = con.prepareStatement(String.format(sql, periods));
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()) {
                deliveryInfos.add(new DeliveryInfo(rs.getString(1), rs.getInt(2), rs.getString(3)));
            }
        } catch (SQLException ex) {
            System.err.println(ex.getMessage());
        }
        return deliveryInfos;
    }

    public Double getPriceWithVat(Double mspPrice, String vat) {
        Double totalPrice = new Double(mspPrice);
        switch (vat) {
            case "ZERO":
                break;
            case "TEN":
                totalPrice = totalPrice/10 + totalPrice;
                break;
            case "EIGHTEEN":
                totalPrice = (totalPrice/100) * 18 + totalPrice;
                break;
            case "TWENTY":
                totalPrice = totalPrice/5 + totalPrice;
                break;
        }
        return totalPrice;
    }

}
