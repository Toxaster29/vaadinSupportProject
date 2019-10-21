package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.report.CatalogPublicationDate;
import com.packagename.myapp.spring.entity.report.SubscriptionsReportPart;

import java.sql.SQLException;
import java.util.List;

public interface ReportDao {

    List<SubscriptionsReportPart> getSubscriptionListForReport();

    List<CatalogPublicationDate> getCatalogDates(SubscriptionsReportPart element);

    void addDateListToDatabase(SubscriptionsReportPart element) throws SQLException;
}
