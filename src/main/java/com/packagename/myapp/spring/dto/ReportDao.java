package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.report.*;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Set;

public interface ReportDao {

    List<SubscriptionsReportPart> getSubscriptionListForReport();

    List<CatalogPublicationDate> getCatalogDates(SubscriptionsReportPart element);

    void addDateListToDatabase(SubscriptionsReportPart element) throws SQLException;

    List<CatalogPublicationList> getCatalogPublications(String publisherId);

    List<PublicationInfoList> getPublicationOutputInfo(String publisherId, Integer periodId, String publicationCode);

    List<CatalogPeriodEntity> getCatalogPeriodList();

    List<SubscriptionByPublisher> getSubscriptionList(String publisherId, String publicationCode, Integer periodId);

    List<CatalogPeriod> getPeriodList();

    List<CatalogPublicationEntity> getCatalogPublicationInfo();

    List<String> getSubscriptionOutputListForPublication(String legalHid, String publicationCode, Integer periodId, String index);

   List<CatalogPrice> getCatalogPricesByElementId(Integer id);

    void insertCatalogData(CatalogPublicationEntity entity, CatalogPrice price, Integer half);

    List<String> getReportPublishers();

    void addReportParams(String publisher);

    List<Integer> getReportElements();

    void addReportParamsRegion(Integer id);

    List<ChildrenSubscriptionEntity> getSubscriptionDataForChildrenReport();

    void setSubscriptionMonthCount(ChildrenSubscriptionEntity entity);

    List<OnlineReportEntity> getOnlineReportEntities();

    Map<Integer, String> getOnlineOrderHids(Set<Integer> set);

    Map<String, List<CatalogElement>> getPublicationMap(Set<String> publicationSet);
}
