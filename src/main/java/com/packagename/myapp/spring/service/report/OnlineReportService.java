package com.packagename.myapp.spring.service.report;

import com.packagename.myapp.spring.dto.report.OnlineReportDao;
import com.packagename.myapp.spring.dto.report.ReportDao;
import com.packagename.myapp.spring.entity.report.CatalogPeriod;
import com.packagename.myapp.spring.entity.report.CatalogPublicationEntity;
import com.packagename.myapp.spring.entity.report.online.OnlineOrderEntity2019And2020;
import com.packagename.myapp.spring.entity.report.online.OnlineOrderInfo;
import com.packagename.myapp.spring.entity.report.online.OnlineSubscription;
import com.packagename.myapp.spring.entity.ufps.UfpsEntity;
import com.packagename.myapp.spring.service.ResourceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
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
    @Autowired
    private ResourceService resourceService;

    public void createReportByDate(String value) {
        Map<Integer, String> macroRegionMap = getMacroRegionListByValue(value);
        List<OnlineSubscription> subscriptions = prepareOnlineSubs();
        Map<String, List<OnlineSubscription>> subscriptionMap = subscriptions.stream().collect(groupingBy(OnlineSubscription::getPostalCode));
        List<String> dataToFile = new ArrayList<>();
        for (Map.Entry<String, List<OnlineSubscription>> entry : subscriptionMap.entrySet()) {
            OnlineOrderEntity2019And2020 onlineEntity = getEntityForOnlineReport(entry.getValue());
            dataToFile.add(generateLine(entry.getKey(), onlineEntity.getRegionId(), onlineEntity.getCount2019(),
                    onlineEntity.getOrderCount2019(), onlineEntity.getTotalPrice2019(),
                    onlineEntity.getCount2020(), onlineEntity.getOrderCount2020(), onlineEntity.getTotalPrice2020(), macroRegionMap));
        }
        reportService.writeTextToFile(dataToFile, "ReportData.txt");
        System.out.println("Yeah");
    }

    private OnlineOrderEntity2019And2020 getEntityForOnlineReport(List<OnlineSubscription> value) {
        Set<Integer> orderIdSet2019 = new LinkedHashSet<>(value.stream().filter(onlineSubscription -> onlineSubscription.getYear() == 2019)
                .map(OnlineSubscription::getOnlineOrderId).collect(Collectors.toList()));
        Set<Integer> orderIdSet2020 = new LinkedHashSet<>(value.stream().filter(onlineSubscription -> onlineSubscription.getYear() == 2020)
                .map(OnlineSubscription::getOnlineOrderId).collect(Collectors.toList()));
        final Integer[] totalCount2019 = {0};
        final Double[] totalPrice2019 = {Double.valueOf(0)};
        Integer orderCount2019 = orderIdSet2019.size();
        final Integer[] totalCount2020 = {0};
        final Double[] totalPrice2020 = {Double.valueOf(0)};
        Integer orderCount2020 = orderIdSet2020.size();
        final Integer[] regionId = {null};
        value.forEach(onlineSubscription -> {
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
        return new OnlineOrderEntity2019And2020(totalCount2019[0], totalCount2020[0], orderCount2019, orderCount2020,
                totalPrice2019[0], totalPrice2020[0], regionId[0]);
    }

    private List<OnlineSubscription> prepareOnlineSubs() {
        List<CatalogPeriod> catalogPeriods = onlineReportDao.getPeriodList("year in (2019,2020)");
        String periods = StringUtils.join(catalogPeriods.stream().map(CatalogPeriod::getPeriodId).collect(Collectors.toList()), ",");
        List<CatalogPublicationEntity> publicationEntities = reportDao.getCatalogPublicationInfo(periods);
        Map<Integer, Map<String, List<CatalogPublicationEntity>>> publicationMap = publicationEntities.stream()
                .collect(groupingBy(CatalogPublicationEntity::getPeriodId, groupingBy(CatalogPublicationEntity::getPublicationCode)));
        String startDate2019 = "2019-01-01 00:00:01";
        String endDate2019 = "2019-04-20 23:59:59";
        String startDate2020 = "2020-01-01 00:00:01";
        String endDate2020 = "2020-04-20 23:59:59";
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
        return subscriptions;
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

    public void createReportWithBuyRegion(String value) {
        List<OnlineSubscription> subscriptions = prepareOnlineSubs();
        Set<Integer> orderIdSet = new LinkedHashSet<>(subscriptions.stream().map(OnlineSubscription::getOnlineOrderId).collect(Collectors.toList()));
        List<OnlineOrderInfo> orderInfoList = onlineReportDao.getOnlineOrderInfo(orderIdSet);
        if (value.equals("")) {
            List<String> fileData = new ArrayList<>();
            orderInfoList.forEach(info -> {
                fileData.add(info.getClientHid());
            });
            reportService.writeTextToFile(fileData, "Hids.txt");
            System.out.println("hids ready");
        } else {
            List<UfpsEntity> ufpsEntities = resourceService.getUfpsEntityList();
            Map<Integer, UfpsEntity> ufpsMap = ufpsEntities.stream().collect(Collectors.toMap(UfpsEntity::getIntId, Function.identity()));
            Map<Integer, String> macroRegionEntityMap = resourceService.getMacroRegionEntityMap();
            Map<String, String> hidInfoMap = resourceService.getHidInfoMap();
            List<String> zipCodes = new ArrayList<>();
            for (Map.Entry<String, String> entry : hidInfoMap.entrySet()) {
                if (!entry.getValue().equals("Не указан")) {
                    zipCodes.add("\'" + entry.getValue() + "\'");
                }
            }
            Set<String> zips = new LinkedHashSet<>(zipCodes);
            Map<String, Integer> treatmentMap = onlineReportDao.getAllTreatmentByZipCodes(StringUtils.join(zips, ","));
            Map<Integer, OnlineOrderInfo> orderInfoMap = new HashMap<>();
            orderInfoList.forEach(info -> {
                info.setRegionId(getRegionIdByTreatment(info.getClientHid(), treatmentMap, hidInfoMap));
                orderInfoMap.put(info.getId(), info);
            });
            subscriptions.forEach(onlineSubscription -> {
                onlineSubscription.setRegionBuyCode(orderInfoMap.get(onlineSubscription.getOnlineOrderId()).getRegionId());
            });
            Map<Integer, List<OnlineSubscription>> onlineSubscriptionMap = subscriptions.stream()
                    .collect(groupingBy(OnlineSubscription::getRegionBuyCode));
            List<String> dataToFile = new ArrayList<>();
            for (Map.Entry<Integer, List<OnlineSubscription>> entry : onlineSubscriptionMap.entrySet()) {
                OnlineOrderEntity2019And2020 onlineEntity = getEntityForOnlineReport(entry.getValue());
                dataToFile.add(generateLineForRegionBuy(entry.getKey() == 0 ? "Н/д" : ufpsMap.get(entry.getKey()).getDescription(),
                        entry.getKey() == 0 ? "Н/д" : macroRegionEntityMap.get(entry.getKey()),
                        onlineEntity.getCount2019(), onlineEntity.getOrderCount2019(), onlineEntity.getTotalPrice2019(),
                        onlineEntity.getCount2020(), onlineEntity.getOrderCount2020(), onlineEntity.getTotalPrice2020()));
            }
            reportService.writeTextToFile(dataToFile, "ReportData.txt");
            System.out.println("Gotovo");
        }
    }

    private String generateLineForRegionBuy(String ufpsEntity, String s, Integer count2019, Integer orderCount2019,
                                            Double totalPrice2019, Integer count2020, Integer orderCount2020, Double totalPrice2020) {
        return ufpsEntity + "\t" + s + "\t" + count2019 + "\t" + orderCount2019 + "\t" + totalPrice2019 + "\t" + count2020 +
                "\t" +  orderCount2020 + "\t" + totalPrice2020;
    }

    private Integer getRegionIdByTreatment(String clientHid, Map<String, Integer> treatmentMap, Map<String, String> hidInfoMap) {
        String zipCode = hidInfoMap.get(clientHid);
        if (zipCode != null && !zipCode.trim().equals("Не указан")) {
            Integer regionId = treatmentMap.get(zipCode);
            if (regionId != null) {
                return regionId;
            } else System.out.println(zipCode);
        }
        return 0;
    }
}
