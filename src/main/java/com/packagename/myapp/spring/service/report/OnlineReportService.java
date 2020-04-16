package com.packagename.myapp.spring.service.report;

import com.packagename.myapp.spring.dto.report.OnlineReportDao;
import com.packagename.myapp.spring.dto.report.ReportDao;
import com.packagename.myapp.spring.entity.report.CatalogPeriod;
import com.packagename.myapp.spring.entity.report.CatalogPublicationEntity;
import com.packagename.myapp.spring.entity.report.online.OnlineSubscription;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.groupingBy;

@Service
public class OnlineReportService {

    @Autowired
    private OnlineReportDao onlineReportDao;
    @Autowired
    private ReportDao reportDao;
    @Autowired
    private ReportService reportService;

    public void createReportByDate(String value) {
        Map<Integer, String> macroRegionMap = getMacroRegionListByValue(value);
        List<CatalogPeriod> catalogPeriods = onlineReportDao.getPeriodList("year in (2019,2020)");
        String periods = StringUtils.join(catalogPeriods.stream().map(CatalogPeriod::getPeriodId).collect(Collectors.toList()), ",");
        List<CatalogPublicationEntity> publicationEntities = reportDao.getCatalogPublicationInfo(periods);
        Map<Integer, Map<String, List<CatalogPublicationEntity>>> publicationMap = publicationEntities.stream()
                .collect(groupingBy(CatalogPublicationEntity::getPeriodId, groupingBy(CatalogPublicationEntity::getPublicationCode)));
        String startDate2019 = "2019-01-01 00:00:01";
        String endDate2019 = "2019-03-15 23:59:59";
        String startDate2020 = "2020-01-01 00:00:01";
        String endDate2020 = "2020-03-15 23:59:59";
        List<OnlineSubscription> subscriptions = onlineReportDao.getAllSubscriptionByDate(startDate2019, endDate2019, 2019);
        subscriptions.addAll(onlineReportDao.getAllSubscriptionByDate(startDate2020, endDate2020, 2020));
        subscriptions.forEach(onlineSubscription -> {
            Map<String, List<CatalogPublicationEntity>> catalogPublications = publicationMap.get(onlineSubscription.getCatalogId());
            if (catalogPublications != null) {
                List<CatalogPublicationEntity> publicationList = catalogPublications.get(onlineSubscription.getPublicationCode());
                if (publicationList != null) {
                    Integer[] output = publicationList.get(0).getOutputMonthCount();
                    Integer count = 0;
                    Integer MspCount = 0;
                    for (int i = 0; i < onlineSubscription.getAllocation().length; i++) {
                        MspCount += onlineSubscription.getAllocation()[i];
                        if (output[i] > 0) {
                            count += onlineSubscription.getMspAlloc()[i] * output[i];
                        }
                    }
                    onlineSubscription.setSubPrice(onlineSubscription.getMinSubPrice() * MspCount);
                    onlineSubscription.setCount(count);
                }
            }
        });
        Map<String, List<OnlineSubscription>> subscriptionMap = subscriptions.stream().collect(groupingBy(OnlineSubscription::getPostalCode));
        List<String> dataToFile = new ArrayList<>();
        for (Map.Entry<String, List<OnlineSubscription>> entry : subscriptionMap.entrySet()) {
            Set<Integer> orderIdSet2019 = new LinkedHashSet<>(entry.getValue().stream().filter(onlineSubscription -> onlineSubscription.getYear() == 2019)
                    .map(OnlineSubscription::getOnlineOrderId).collect(Collectors.toList()));
            Set<Integer> orderIdSet2020 = new LinkedHashSet<>(entry.getValue().stream().filter(onlineSubscription -> onlineSubscription.getYear() == 2020)
                    .map(OnlineSubscription::getOnlineOrderId).collect(Collectors.toList()));
            final Integer[] totalCount2019 = {0};
            final Double[] totalPrice2019 = {Double.valueOf(0)};
            Integer orderCount2019 = orderIdSet2019.size();
            final Integer[] totalCount2020 = {0};
            final Double[] totalPrice2020 = {Double.valueOf(0)};
            Integer orderCount2020 = orderIdSet2020.size();
            final Integer[] regionId = {null};
            entry.getValue().forEach(onlineSubscription -> {
                if (regionId[0] == null) regionId[0] = onlineSubscription.getRegionCode();
                if (onlineSubscription.getYear() == 2019) {
                    if (onlineSubscription.getCount() != null) {
                        totalCount2019[0] += onlineSubscription.getCount();
                        totalPrice2019[0] += onlineSubscription.getSubPrice();
                    }
                } else {
                    if (onlineSubscription.getCount() != null) {
                        totalCount2020[0] += onlineSubscription.getCount();
                        totalPrice2020[0] += onlineSubscription.getSubPrice();
                    }
                }
            });
            dataToFile.add(generateLine(entry.getKey(), regionId[0],totalCount2019[0], orderCount2019, totalPrice2019[0],
                    totalCount2020[0], orderCount2020, totalPrice2020[0], macroRegionMap));
        }
        reportService.writeTextToFile(dataToFile);
        System.out.println("Yeah");
    }

    private Map<Integer, String> getMacroRegionListByValue(String value) {
        Map<Integer, String> macroRegionMap = new HashMap<>();
        String[] lines = value.split("\n");
        for (String line : lines) {
            String[] words = line.split("\t");
            macroRegionMap.put(Integer.parseInt(words[1]), words[0]);
        }
        return macroRegionMap;
    }

    private String generateLine(String entryKey, Integer key, Integer totalCount, Integer orderCount, Double totalPrice,
                                Integer integer, Integer orderCount2020, Double aDouble, Map<Integer, String> macroRegionMap) {
        return entryKey + "\t" + key + "\t" + macroRegionMap.get(key) + "\t" + totalCount + "\t" + orderCount + "\t" + totalPrice
                + "\t" + integer + "\t" + orderCount2020 + "\t" + aDouble;
    }

}
