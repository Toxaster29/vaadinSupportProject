package com.packagename.myapp.spring.dto.report;

import com.packagename.myapp.spring.entity.report.CatalogPeriod;
import com.packagename.myapp.spring.entity.report.online.OnlineSubscription;

import java.util.List;

public interface OnlineReportDao {
    List<OnlineSubscription> getAllSubscriptionByDate(String startDate, String endDate, int year);

    List<CatalogPeriod> getPeriodList(String s);
}
