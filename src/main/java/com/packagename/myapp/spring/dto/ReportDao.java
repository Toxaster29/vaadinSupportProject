package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.report.*;

import java.sql.SQLException;
import java.util.List;

public interface ReportDao {

    List<SubscriptionsReportPart> getSubscriptionListForReport();

    List<CatalogPublicationDate> getCatalogDates(SubscriptionsReportPart element);

    void addDateListToDatabase(SubscriptionsReportPart element) throws SQLException;

    List<CatalogPublicationList> getCatalogPublications(String publisherId);

    List<PublicationInfoList> getPublicationOutputInfo(String publisherId, Integer periodId, String publicationCode);

    List<CatalogPeriodEntity> getCatalogPeriodList();

    List<SubscriptionByPublisher> getSubscriptionList(String publisherId, String publicationCode, Integer periodId);
}
