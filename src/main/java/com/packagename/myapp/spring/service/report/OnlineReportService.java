package com.packagename.myapp.spring.service.report;

import com.packagename.myapp.spring.dto.report.OnlineReportDao;
import com.packagename.myapp.spring.dto.report.ReportDao;
import com.packagename.myapp.spring.entity.insert.EmailPhone;
import com.packagename.myapp.spring.entity.report.CatalogPeriod;
import com.packagename.myapp.spring.entity.report.CatalogPublicationEntity;
import com.packagename.myapp.spring.entity.report.online.*;
import com.packagename.myapp.spring.entity.ufps.UfpsEntity;
import com.packagename.myapp.spring.service.ResourceService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
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
        Integer year1 = 2019;
        Integer year2 = 2020;
        Integer halfYear = 2;
        String endDate1 = "2020-03-01 23:59:59";
        String endDate2 = "2021-03-01 23:59:59";

        Map<Integer, String> macroRegionMap = resourceService.getMacroRegionEntityMap();
        List<UfpsEntity> ufpsEntities = resourceService.getUfpsEntityList();
        Map<Integer, UfpsEntity> ufpsMap = ufpsEntities.stream().collect(Collectors.toMap(UfpsEntity::getIntId, Function.identity()));
        List<OnlineSubscription> subscriptions = prepareOnlineSubs(true, year1, year2, halfYear, endDate1, endDate2);
        Map<String, List<OnlineSubscription>> subscriptionMap = subscriptions.stream().collect(groupingBy(OnlineSubscription::getPostalCode));
        List<String> dataToFile = new ArrayList<>();
        for (Map.Entry<String, List<OnlineSubscription>> entry : subscriptionMap.entrySet()) {
            OnlineOrderEntity2019And2020 onlineEntity = getEntityForOnlineReport(entry.getValue(), year1, year2);
            dataToFile.add(generateLine(entry.getKey(), onlineEntity.getRegionId(), onlineEntity.getCount2019(),
                    onlineEntity.getOrderCount2019(), onlineEntity.getTotalPrice2019(),
                    onlineEntity.getCount2020(), onlineEntity.getOrderCount2020(), onlineEntity.getTotalPrice2020(), macroRegionMap, ufpsMap));
        }
        reportService.writeTextToFile(dataToFile, "ReportData.txt");
        System.out.println("Yeah");
    }

    private OnlineOrderEntity2019And2020 getEntityForOnlineReport(List<OnlineSubscription> value, Integer year1, Integer year2) {
        Set<Integer> orderIdSet2019 = new LinkedHashSet<>(value.stream().filter(onlineSubscription -> onlineSubscription.getYear() == year1)
                .map(OnlineSubscription::getOnlineOrderId).collect(Collectors.toList()));
        Set<Integer> orderIdSet2020 = new LinkedHashSet<>(value.stream().filter(onlineSubscription -> onlineSubscription.getYear() == year2)
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
            if (onlineSubscription.getYear() == year1) {
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

    private List<OnlineSubscription> prepareOnlineSubs(boolean equals, Integer year1, Integer year2, Integer halfYear,
                                                       String endDate1, String endDate2) {
        List<CatalogPeriod> catalogPeriods = onlineReportDao
                .getPeriodList(year2 != null ? "year in (" + year1 + "," + year2 + ")" :"year in ("+ year1 +")");
        String periods = StringUtils.join(catalogPeriods.stream().map(CatalogPeriod::getPeriodId).collect(Collectors.toList()), ",");
        List<CatalogPublicationEntity> publicationEntities = reportDao.getCatalogPublicationInfo(periods);
        Map<Integer, Map<String, List<CatalogPublicationEntity>>> publicationMap = publicationEntities.stream()
                .collect(groupingBy(CatalogPublicationEntity::getPeriodId, groupingBy(CatalogPublicationEntity::getPublicationCode)));

        List<CatalogPeriod> period1 = reportDao.getPeriodList(year1, halfYear);
        String per1 = StringUtils.join(period1.stream().map(CatalogPeriod::getPeriodId).collect(Collectors.toList()), ",");

        List<CatalogPeriod> period2 = reportDao.getPeriodList(year2, halfYear);
        String per2 = StringUtils.join(period2.stream().map(CatalogPeriod::getPeriodId).collect(Collectors.toList()), ",");

        List<OnlineSubscription> subscriptions = new ArrayList<>();

        if (year2 == null) {
            subscriptions.addAll(onlineReportDao.getAllSubscriptionByPeriods(per1, year1, equals));
        } else {
            if (endDate1 != null && endDate2 != null) {
                subscriptions.addAll(onlineReportDao.getAllSubscriptionByPeriodsWithEndDate(per1, year1, equals, endDate1));
                subscriptions.addAll(onlineReportDao.getAllSubscriptionByPeriodsWithEndDate(per2, year2, equals, endDate2));
            } else {
                subscriptions.addAll(onlineReportDao.getAllSubscriptionByPeriods(per1, year1, equals));
                subscriptions.addAll(onlineReportDao.getAllSubscriptionByPeriods(per2, year2, equals));
            }
        }

        subscriptions.forEach(onlineSubscription -> {
            Map<String, List<CatalogPublicationEntity>> catalogPublications = publicationMap.get(onlineSubscription.getCatalogId());
            if (catalogPublications != null) {
                List<CatalogPublicationEntity> publicationList = catalogPublications.get(onlineSubscription.getPublicationCode());
                if (publicationList != null) {
                    Integer[] output = publicationList.get(0).getOutputMonthCount();
                    String publicationName = publicationList.get(0).getTitle();
                    Integer count = 0;
                    Integer MspCount = 0;
                    for (int i = 0; i < onlineSubscription.getAllocation().length; i++) {
                        MspCount += onlineSubscription.getAllocation()[i];
                        if (output[i] > 0) {
                            count += onlineSubscription.getMspAlloc()[i] * output[i];
                        }
                    }
                    onlineSubscription.setPublicationName(publicationName);
                    onlineSubscription.setSubPrice(onlineSubscription.getMinSubPrice() * MspCount);
                    onlineSubscription.setCount(count);
                }
            }
        });
        return subscriptions;
    }

    private int[] getAllocationsFromString(String string) {
        String out = string.substring(1, string.length() - 1);
        String[] alloc = out.split(",");
        return  Arrays.asList(alloc).stream().mapToInt(Integer::parseInt).toArray();
    }

    private String generateLine(String entryKey, Integer key, Integer totalCount, Integer orderCount, Double totalPrice,
                                Integer integer, Integer orderCount2020, Double aDouble, Map<Integer, String> macroRegionMap, Map<Integer, UfpsEntity> ufpsMap) {
        return  ufpsMap.get(key).getDescription() + "\t" + entryKey + "\t" + macroRegionMap.get(key) + "\t" + totalCount + "\t" + orderCount + "\t" + totalPrice
                + "\t" + integer + "\t" + orderCount2020 + "\t" + aDouble;
    }

    public void createReportWithBuyRegion(String value) {
        Integer year1 = 2020;
        Integer year2 = 2021;
        Integer halfYear = 1;
        String endDate1 = "2020-03-01 23:59:59";
        String endDate2 = "2021-03-01 23:59:59";

        System.out.println("Start ReportWithBuyRegion");

        List<OnlineSubscription> subscriptions = prepareOnlineSubs(true, year1, year2, halfYear, endDate1, endDate2);
        Set<Integer> orderIdSet = new LinkedHashSet<>(subscriptions.stream().map(OnlineSubscription::getOnlineOrderId)
                .collect(Collectors.toList()));
        List<OnlineOrderInfo> orderInfoList = onlineReportDao.getOnlineOrderInfo(orderIdSet);
        if (value.equals("")) {
            Set<String> fileData = new HashSet<>();
            orderInfoList.forEach(info -> {
                fileData.add(info.getClientHid());
            });
            if (!fileData.isEmpty()) {
                reportService.writeTextToFile(fileData, "Hids.txt");
                System.out.println("hids ready");
            } else System.out.println("hids empty");
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
                onlineSubscription.setRegionBuyCode(orderInfoMap.get(onlineSubscription.getOnlineOrderId()) != null ?
                        orderInfoMap.get(onlineSubscription.getOnlineOrderId()).getRegionId() : 0);
            });
            Map<Integer, List<OnlineSubscription>> onlineSubscriptionMap = subscriptions.stream()
                    .collect(groupingBy(OnlineSubscription::getRegionBuyCode));
            List<String> dataToFile = new ArrayList<>();
            for (Map.Entry<Integer, List<OnlineSubscription>> entry : onlineSubscriptionMap.entrySet()) {
                OnlineOrderEntity2019And2020 onlineEntity = getEntityForOnlineReport(entry.getValue(), year1, year2);
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

    public void createReportOnlineTopPublications(String value) {
        Integer year1 = 2020;
        Integer year2 = null; //для одного года
        Integer halfYear = 2;

        List<OnlineSubscription> subscriptions = prepareOnlineSubs(value.equals(""), year1, year2, halfYear, null, null);
        Map<String, List<OnlineSubscription>> onlineSubscriptionMap = subscriptions.stream()
                .collect(groupingBy(OnlineSubscription::getPublicationIndex));
        List<TopPopularIndex> topPopularIndices = new ArrayList<>();
        for (Map.Entry<String, List<OnlineSubscription>> entry : onlineSubscriptionMap.entrySet()) {
            topPopularIndices.add(getDataBySubscriptionList(entry.getValue()));
        }
        Collections.sort(topPopularIndices, Collections.reverseOrder());
        List<String> dataToFile = new ArrayList<>();
        for (int i = 0; i < 1500; i++) {
            dataToFile.add(topPopularIndices.get(i).toString());
        }
        reportService.writeTextToFile(dataToFile, "ReportData");
        System.out.println("Top");
    }

    private TopPopularIndex getDataBySubscriptionList(List<OnlineSubscription> value) {
        if (!value.isEmpty()) {
            OnlineSubscription sub = value.get(0);
            Integer orderCount = value.size();
            final Integer[] totalCount = {0};
            final Double[] totalPrice = {Double.valueOf(0)};
            for (OnlineSubscription subscription : value) {
                if (subscription.getCount() != null) {
                    totalCount[0] += subscription.getCount();
                    totalPrice[0] += subscription.getSubPrice();
                }
            }
            return new TopPopularIndex(sub.getPublicationIndex(), sub.getPublicationName(), totalCount[0], orderCount, totalPrice[0]);
        } else return null;
    }

    public void createOnlineReportForMonth(String value) {
        String startDate = "2020-01-01 00:00:01";
        String endDate = "2020-01-31 23:59:59";
        List<OnlineSubscription> subscriptions = onlineReportDao.getOnlineSubsByTime(startDate, endDate);
        Set<Integer> orderIdSet = new LinkedHashSet<>(subscriptions.stream().map(OnlineSubscription::getOnlineOrderId).collect(Collectors.toList()));
        List<OnlineOrderInfo> onlineOrderInfoList = onlineReportDao.getOnlineOrderInfo(orderIdSet);
        if (value.equals("")) {
            List<String> fileData = new ArrayList<>();
            Set<String> hids = new LinkedHashSet<String>(onlineOrderInfoList.stream().map(OnlineOrderInfo::getClientHid).collect(Collectors.toList()));
            fileData.addAll(hids);
            reportService.writeTextToFile(fileData, "hids.txt");
            System.out.println("Hids Ready");
        } else {
            List<String> fileData = new ArrayList<>();
            Map<String, PochtaIdInfo> emailPhoneMap = resourceService.getHidEmailPhoneMap();
            Map<Integer, OnlineOrderInfo> onlineOrderInfoMap = onlineOrderInfoList.stream().collect(Collectors.toMap(OnlineOrderInfo::getId, onlineOrderInfo -> onlineOrderInfo));
            List<OrderElement> orderElements = onlineReportDao.getOnlineOrderElements(orderIdSet);
            Map<Integer, List<OrderElement>> orderElementsMap =  orderElements.stream().collect(groupingBy(OrderElement::getOnlineOrderId));
            List<CatalogPeriod> catalogPeriods = onlineReportDao.getPeriodList("year = 2020");
            String periods = StringUtils.join(catalogPeriods.stream().map(CatalogPeriod::getPeriodId).collect(Collectors.toList()), ",");
            List<CatalogOnlineEntity> publicationEntities = onlineReportDao.getOnlineCatalog(periods);
            Map<Integer, Map<String, List<CatalogOnlineEntity>>> publicationMap = publicationEntities.stream()
                    .collect(groupingBy(CatalogOnlineEntity::getPeriodId, groupingBy(CatalogOnlineEntity::getPublicationCode)));
            subscriptions.forEach(onlineSubscription -> {
                Map<String, List<CatalogOnlineEntity>> catalogMap = publicationMap.get(onlineSubscription.getCatalogId());
                List<CatalogOnlineEntity> publicationEntityList = catalogMap.get(onlineSubscription.getPublicationCode());
                onlineSubscription.setPublicationName(publicationEntityList.get(0).getTitle());
                OnlineOrderInfo orderInfo = onlineOrderInfoMap.get(onlineSubscription.getOnlineOrderId());
                PochtaIdInfo emailPhone = emailPhoneMap.get(orderInfo.getClientHid());
                List<OrderElement> orderElementList = orderElementsMap.get(orderInfo.getId());
                final OrderElement[] element = {null};
                orderElementList.forEach(orderElement -> {
                    if (orderElement.getUfpsCode().equals(onlineSubscription.getRegionCode().toString())
                            && orderElement.getPublicationCode().equals(onlineSubscription.getPublicationCode())
                    ) element[0] = orderElement;
                });
                String isDD = element[0].getMarker() != null && element[0].getMarker().equals("KINDNESS_TREE") ? "Да" : "Нет";
                String deliveryType = "";
                switch (element[0].getDeliveryType()) {
                    case 0:
                        deliveryType = "до адресата";
                        break;
                    case 1:
                        deliveryType = "до a/я";
                        break;
                    case 2:
                        deliveryType = "до востребования";
                        break;
                }
                onlineSubscription.setSubPrice(countSubPrice(onlineSubscription.getMinSubPrice(), onlineSubscription.getAllocation()));
                String line = generateLineForOnlineMonthReport(onlineSubscription, emailPhone, orderInfo.getClientHid(), deliveryType, isDD);
                if (line != null) fileData.add(line);
            });
            reportService.writeTextToFile(fileData, "reportData.txt");
            System.out.println("Report data ready");
        }

    }

    private Double countSubPrice(Double minSubPrice, int[] mspAlloc) {
        Double sum = Double.valueOf(0);
        for (Integer alloc : mspAlloc) {
            sum += alloc * minSubPrice;
        }
        return sum;
    }

    private String generateLineForOnlineMonthReport(OnlineSubscription onlineSubscription, PochtaIdInfo emailPhone, String clientHid, String deliveryType, String isDD) {
        try {
            return emailPhone.getFio() + "\t" + emailPhone.getEmail() + "\t" + emailPhone.getPhone() + "\t" + clientHid
                    + "\t" + onlineSubscription.getFIO() + "\t" + onlineSubscription.getAddress() + "\t" + deliveryType
                    + "\t" + onlineSubscription.getCreateDate() + "\t" + onlineSubscription.getPublicationName()
                    + "\t" + onlineSubscription.getPublicationIndex() + "\t" + onlineSubscription.getMinSubsPeriod()
                    + "\t" + Arrays.toString(onlineSubscription.getAllocation()) + "\t" + onlineSubscription.getMinSubPrice()
                    + "\t" + onlineSubscription.getSubPrice() + "\t" + isDD;
        } catch (Exception e) {
            System.out.println("Some problem");
        }
        return null;
    }

    public void createOnlineReportByRegionReceiving(String value) {
        Integer year1 = 2020;
        Integer year2 = 2021;
        Integer halfYear = 1;
        String endDate1 = "2020-03-01 23:59:59";
        String endDate2 = "2021-03-01 23:59:59";
        /*String endDate1 = null;
        String endDate2 = null;*/

        Map<Integer, String> macroRegionMap = resourceService.getMacroRegionEntityMap();
        List<UfpsEntity> ufpsEntities = resourceService.getUfpsEntityList();
        Map<Integer, UfpsEntity> ufpsMap = ufpsEntities.stream().collect(Collectors.toMap(UfpsEntity::getIntId, Function.identity()));
        List<OnlineSubscription> subscriptions = prepareOnlineSubs(true, year1, year2, halfYear, endDate1, endDate2);
        Map<Integer, List<OnlineSubscription>> subscriptionMap = subscriptions.stream().collect(groupingBy(OnlineSubscription::getRegionCode));
        List<String> dataToFile = new ArrayList<>();
        for (Map.Entry<Integer, List<OnlineSubscription>> entry : subscriptionMap.entrySet()) {
            OnlineOrderEntity2019And2020 onlineEntity = getEntityForOnlineReport(entry.getValue(), year1, year2);
            dataToFile.add(generateLineForRegionBuy(ufpsMap.get(entry.getKey()).getDescription(), macroRegionMap.get(entry.getKey()),
                    onlineEntity.getCount2019(), onlineEntity.getOrderCount2019(), onlineEntity.getTotalPrice2019(),
                    onlineEntity.getCount2020(), onlineEntity.getOrderCount2020(), onlineEntity.getTotalPrice2020()));
        }
        reportService.writeTextToFile(dataToFile, "ReportData.txt");
        System.out.println("Yeah");
    }
}
