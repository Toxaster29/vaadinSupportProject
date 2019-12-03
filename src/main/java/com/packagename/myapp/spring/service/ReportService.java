package com.packagename.myapp.spring.service;

import com.packagename.myapp.spring.dto.ReportDao;
import com.packagename.myapp.spring.entity.report.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;
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
}
