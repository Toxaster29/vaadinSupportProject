package com.packagename.myapp.spring.dto.report;

import com.packagename.myapp.spring.entity.report.CatalogPeriod;
import com.packagename.myapp.spring.entity.report.online.OnlineOrderInfo;
import com.packagename.myapp.spring.entity.report.online.OnlineSubscription;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface OnlineReportDao {
    List<OnlineSubscription> getAllSubscriptionByDate(String startDate, String endDate, int year);

    List<CatalogPeriod> getPeriodList(String s);

    List<OnlineOrderInfo> getOnlineOrderInfo(Set<Integer> orderIdSet);

    Map<String, Integer> getAllTreatmentByZipCodes(String join);
}
