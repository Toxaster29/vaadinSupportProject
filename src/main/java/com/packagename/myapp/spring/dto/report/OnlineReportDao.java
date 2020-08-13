package com.packagename.myapp.spring.dto.report;

import com.packagename.myapp.spring.entity.report.CatalogPeriod;
import com.packagename.myapp.spring.entity.report.online.CatalogOnlineEntity;
import com.packagename.myapp.spring.entity.report.online.OnlineOrderInfo;
import com.packagename.myapp.spring.entity.report.online.OnlineSubscription;
import com.packagename.myapp.spring.entity.report.online.OrderElement;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface OnlineReportDao {

    List<CatalogPeriod> getPeriodList(String s);

    List<OnlineOrderInfo> getOnlineOrderInfo(Set<Integer> orderIdSet);

    Map<String, Integer> getAllTreatmentByZipCodes(String join);

    List<OnlineSubscription> getAllSubscriptionByPeriods(String period, int year, boolean equals);

    List<OnlineSubscription> getOnlineSubsByTime(String startDate, String endDate);

    List<OrderElement> getOnlineOrderElements(Set<Integer> orderIdSet);

    List<CatalogOnlineEntity> getOnlineCatalog(String periods);
}
