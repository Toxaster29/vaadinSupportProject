package com.packagename.myapp.spring.service;

import com.packagename.myapp.spring.dto.ReportDao;
import com.packagename.myapp.spring.entity.insert.EmailPhone;
import com.packagename.myapp.spring.entity.report.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReportService {

    @Autowired
    private ReportDao reportDao;

    public void createAllocationReportForSubscription() {
        //TODO доработать чтоб было бытсрее
       List<SubscriptionsReportPart> subscriptionsList = reportDao.getSubscriptionListForReport();
        final Integer[] wait = {0};
        System.out.println(subscriptionsList.size() + " Размер листа");
           subscriptionsList.stream().forEach(element -> {
               List<CatalogPublicationDate> catalogDateList = reportDao.getCatalogDates(element);
               Integer count = 0;
               for (int i = 0; i < element.getAllocation().length; i++) {
                   if (element.getAllocation()[i] > 0) {
                       count+=(element.getAllocation()[i] * countForMonthWithMSP(catalogDateList, i, element.getMinSubsPeriod()));
                   }
               }
               wait[0]++;
               element.setCount(count);
               try {
                   reportDao.addDateListToDatabase(element);
               } catch (SQLException e) {
                   e.printStackTrace();
               }
               if(wait[0]%300==0) System.out.println(wait[0]);
           });
    }

    private Integer countForMonthWithMSP(List<CatalogPublicationDate> list, Integer month, Integer msp) {
        if (msp > 1) {
            if (month + msp - 1 < 6) {
                Integer count = list.stream().filter(publicationDate -> publicationDate.getMonth() == month).collect(Collectors.toList()).size();
                return count != 0 ? count * msp : 0;
            } else {
                return list.stream().filter(publicationDate -> publicationDate.getMonth() == month).collect(Collectors.toList()).size();
            }
        } else {
           return list.stream().filter(publicationDate -> publicationDate.getMonth() == month).collect(Collectors.toList()).size();
        }
    }

    public void createPublisherOutputReport(String publisherId) {
        List<CatalogPublicationList> publicationList = reportDao.getCatalogPublications(publisherId);
        publicationList.forEach(catalogPublicationList -> {
            catalogPublicationList.setPublicationInfo(reportDao.getPublicationOutputInfo(publisherId,
                    catalogPublicationList.getPeriodId(), catalogPublicationList.getPublicationCode()));
        });
        List<CatalogPeriodEntity> periodEntityList = reportDao.getCatalogPeriodList();
        publicationList.forEach(catalogPublication -> {
            Integer count = 0;
            if (!catalogPublication.getPublicationInfo().isEmpty()) {
                List<SubscriptionByPublisher> subscriptionList = reportDao.getSubscriptionList(publisherId,
                        catalogPublication.getPublicationCode(), catalogPublication.getPeriodId());
                for (SubscriptionByPublisher subscription : subscriptionList) {
                    Integer[] output = catalogPublication.getPublicationInfo().stream().filter(publication -> publication.getRegionCode()
                            .equals(subscription.getRegionCode())).findFirst().get().getMonthOutput();
                    for (int i = 0; i < 12; i++) {
                        if (subscription.getAllocation()[i] > 0) {
                            count += subscription.getAllocation()[i] * output[i];
                        }
                    }
                }
            }
            catalogPublication.setCount(count);
        });
        publicationList.forEach(catalogPublication -> {
            periodEntityList.forEach(period -> {
                for (Integer periodId : period.getPeriodIds()) {
                    if (periodId.equals(catalogPublication.getPeriodId())) {
                        period.addToPublicationList(catalogPublication);
                    }
                }
            });
        });
        periodEntityList.forEach(period -> {
            period.getPublicationList().forEach(publication -> {
                System.out.println(period.getName() + "\t" + publication.getPublicationCode() + "\t"
                        + publication.getTitle() + "\t" + publication.getCount());
            });
        });
        System.out.println("Complete");
    }

    public void createOutputReportForAllPublishers() {
        List<CatalogPeriod> catalogPeriods = reportDao.getPeriodList(2019, null);
        String periods = StringUtils.join(catalogPeriods.stream().map(CatalogPeriod::getPeriodId).collect(Collectors.toList()), ",");
        List<CatalogPublicationEntity> publicationEntities = reportDao.getCatalogPublicationInfo(periods);
        publicationEntities.forEach(entity -> {
            final String[] index = {""};
            Integer half = getHalfByPeriodId(catalogPeriods, entity.getPeriodId());
            List<CatalogPrice> catalogPrices = reportDao.getCatalogPricesByElementId(entity.getId());
            catalogPrices.forEach(price -> {
                if (!index[0].equals(price.getIndex())) {
                    if (index[0].equals("")) index[0] = price.getIndex();
                    List<String> outputs = reportDao.getSubscriptionOutputListForPublication(entity.getLegalHid(),
                            entity.getPublicationCode(), entity.getPeriodId(), price.getIndex());
                    Integer count = 0;
                    for (String output : outputs) {
                        String out = output.substring(1, output.length() - 1);
                        String[] alloc = out.split(",");
                        for (int i = 0; i < alloc.length; i++) {
                            if (entity.getOutputMonthCount()[i] > 0) {
                                count += Integer.parseInt(alloc[i]) * entity.getOutputMonthCount()[i];
                            }
                        }
                    }
                    entity.setCirculation(count);
                    entity.setOutputCount(Arrays.stream(entity.getOutputMonthCount()).mapToInt(Integer::intValue).sum());
                    reportDao.insertCatalogData(entity, price, half);
                }
            });
        });
        System.out.println("Complete");
    }

    private Integer getHalfByPeriodId(List<CatalogPeriod> catalogPeriods, Integer periodId) {
       return catalogPeriods.stream().filter(period -> period.getPeriodId().equals(periodId)).findFirst().get().getHalf();
    }

    public void addDataToReport() {
        List<String> publishers = reportDao.getReportPublishers();
        publishers.forEach(publisher -> {
            reportDao.addReportParams(publisher);
        });
        List<Integer> elementIds = reportDao.getReportElements();
        elementIds.forEach(id -> {
            reportDao.addReportParamsRegion(id);
        });
        System.out.println("Ready");
    }

    public void createChildrenDataReport() {
        List<ChildrenSubscriptionEntity> subscriptionEntityList = reportDao.getSubscriptionDataForChildrenReport();
        subscriptionEntityList.forEach(entity -> {
            reportDao.setSubscriptionMonthCount(entity);
        });
        writeToFile(subscriptionEntityList);
        System.out.println("Ok");
    }

    private void writeToFile(List<ChildrenSubscriptionEntity> entities) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter("C:\\Users\\assze\\Desktop\\reportChildren.txt"));
            for (ChildrenSubscriptionEntity entity : entities) {
                pw.write(entity.getPublicationCode() + "\t" + entity.getIndex() + "\t"
                        + entity.getTotalPrice() + "\t" + entity.getTotalCount() + "\n");
            }
            pw.close();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                if (pw != null) {
                    pw.close();
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public void createOnlineReportWithRegion() {
        final Integer[] count = {0};
        List<OnlineReportEntity> onlineReportEntities = reportDao.getOnlineReportEntities();
        //Set<Integer> set = new LinkedHashSet<>(onlineReportEntities.stream().map(OnlineReportEntity::getOnlineOrderId).collect(Collectors.toList()));
        //Map<Integer, String> oderAndHid = reportDao.getOnlineOrderHids(set);
        Set<String> publicationSet = new LinkedHashSet<>(onlineReportEntities.stream().map(OnlineReportEntity::getPublicationCode).collect(Collectors.toList()));
        Map<String, List<CatalogElement>> publicationMap = reportDao.getPublicationMap(publicationSet);
        onlineReportEntities.forEach(entity -> {
           String name = publicationMap.get(entity.getPublicationCode()).stream().filter(e -> e.getCatalogPeriod()
                   .equals(entity.getCatalogPeriod())).findFirst().get().getName();
           entity.setName(name);
           //String hid = oderAndHid.get(entity.getOnlineOrderId());
           //entity.setBuyerHid(hid);
        });
        writeOnlineReportToFile(onlineReportEntities);
    }

    private void writeOnlineReportToFile(List<OnlineReportEntity> onlineReportEntities) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter("C:\\Users\\assze\\Desktop\\reportUfps.txt"));
            for (OnlineReportEntity entity : onlineReportEntities) {
                pw.write(entity.getIndex() + "\t" + entity.getName() + "\t" + entity.getSum() + "\t" +
                        entity.getMspCount() + "\t" + entity.getDeliveryRegion() + "\n");
            }
            pw.close();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                if (pw != null) {
                    pw.close();
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public void createReportByPublicationForPeriod() {
        List<CatalogPeriod> catalogPeriods = reportDao.getPeriodList(2020, 1);
        String periods = StringUtils.join(catalogPeriods.stream().map(CatalogPeriod::getPeriodId).collect(Collectors.toList()), ",");
        List<CatalogPublicationEntity> publicationEntities = reportDao.getCatalogPublicationInfo(periods);
        List<Subscription> subscriptions = reportDao.getAllSubscriptionsForPeriod(periods);
        List<CatalogPrice> catalogPrices = reportDao.getAllCatalogPricesForPublications(publicationEntities);
        List<DeliveryInfo> deliveryInfos = reportDao.getDeliveryInfoForPeriod(periods);
        List<String> dataToFile = new ArrayList<>();
        publicationEntities.forEach(entity -> {
            final String[] index = {""};
            List<CatalogPrice> catalogPricesForPublication = catalogPrices.stream().filter(e -> e.getElementId().equals(entity.getId())).collect(Collectors.toList());
            catalogPricesForPublication.forEach(price -> {
                if (!index[0].equals(price.getIndex())) {
                    if (index[0].equals("")) index[0] = price.getIndex();
                    List<Subscription> subByParams = subscriptions.stream().filter(s -> s.getPublicationCode().equals(entity.getPublicationCode()) &&
                            s.getCatalogId().equals(entity.getPeriodId()) && price.getIndex().equals(s.getPublicationIndex())).collect(Collectors.toList());
                    Integer count = 0;
                    for (Subscription sub : subByParams) {
                        for (int i = 0; i < sub.getAllocation().length; i++) {
                            if (entity.getOutputMonthCount()[i] > 0) {
                                count += sub.getAllocation()[i] * entity.getOutputMonthCount()[i];
                            }
                        }
                    }
                    entity.setCirculation(count);
                    entity.setOutputCount(Arrays.stream(entity.getOutputMonthCount()).mapToInt(Integer::intValue).sum());
                    String deliveryInfo = deliveryInfos.stream().filter(e -> e.getPeriodId().equals(entity.getPeriodId())
                    && e.getHid().equals(entity.getLegalHid())).findFirst().get().getType();
                    dataToFile.add(createFileLine(entity, price, deliveryInfo));
                }
            });
        });
        writeTextToFile(dataToFile);
        System.out.println("This is over!");
    }

    private void writeTextToFile(List<String> dataToFile) {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new FileWriter("C:\\Users\\assze\\Desktop\\reportAll2020.txt"));
            for (String line : dataToFile) {
                pw.write(line + "\n");
            }
            pw.close();
        } catch (Exception e) {
            System.out.println(e);
        } finally {
            try {
                if (pw != null) {
                    pw.close();
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    private String createFileLine(CatalogPublicationEntity entity, CatalogPrice price, String deliveryInfo) {
        if (price.getMspPriceNoVat() > 0) {
            return entity.getLegalHid() + "\t" +  price.getIndex() + "\t" +  entity.getId() + "\t" +  entity.getTitle() + "\t" +
                    price.getMspPriceNoVat() + "\t" +  reportDao.getPriceWithVat(price.getMspPriceNoVat(), price.getVat()) + "\t" +  price.getIssuePriceNoVat() + "\t" +
                    reportDao.getPriceWithVat(price.getIssuePriceNoVat(), price.getVat()) + "\t" +  entity.getCirculation() + "\t" +  entity.getOutputCount()
                    + "\t" + reportDao.getDelivery(deliveryInfo) + "\t" + reportDao.getPayer(entity.getPlaceType());
        } else {
            return entity.getLegalHid() + "\t" +  price.getIndex() + "\t" +  entity.getId() + "\t" +  entity.getTitle() + "\t" +
                    reportDao.getPriceWithoutVat(price.getMspPrice(), price.getVat()) + "\t" +  price.getMspPrice() + "\t" +
                    reportDao.getPriceWithoutVat(price.getIssuePrice(), price.getVat()) + "\t" +  price.getIssuePrice() + "\t" +  entity.getCirculation()
                    + "\t" +  entity.getOutputCount()  + "\t" + reportDao.getDelivery(deliveryInfo) + "\t" + reportDao.getPayer(entity.getPlaceType());
        }
    }
}
