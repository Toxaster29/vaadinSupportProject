package com.packagename.myapp.spring.service;

import com.packagename.myapp.spring.dto.ReportDao;
import com.packagename.myapp.spring.entity.report.CatalogPublicationDate;
import com.packagename.myapp.spring.entity.report.SubscriptionsReportPart;
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

}
