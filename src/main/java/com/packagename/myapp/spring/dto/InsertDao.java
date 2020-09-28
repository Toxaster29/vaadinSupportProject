package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.schedule.PublisherData;
import com.packagename.myapp.spring.entity.schedule.PublisherWithContract;
import com.packagename.myapp.spring.entity.subscription.Subscription;
import com.packagename.myapp.spring.entity.treatment.TreatmentEntity;

import java.util.List;

public interface InsertDao {

    List<PublisherWithContract> getAllPublisherByYearAndHalf(List<String> publisherWithSchedule);

    void setContractIdForPublisher(PublisherWithContract publisher);

    List<String> getPublishersWithSchedule();

    List<Subscription> getSubscriptionWithoutAnnulment(TreatmentEntity entity);

    List<String> getPublishersWithScheduleByYearAndHalf(int year, int half);

    List<String> getAllWithoutScheduleByPeriod(String periods, List<String> publisherWithSchedule);

    List<String> getPublishersWithEmptyScheduleByYearAndHalf(int year, int half);

    List<String> getPublisherWithContract(List<String> publisherWithEmptySchedule, int year, int half);

    PublisherData getPublisherDataByHid(String hid);

    List<String> getAllIndexForPublisherByHid(String hid, String periods);

    List<String> getAllLocalPublisher(List<String> publisherWithSchedule);
}
