package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.schedule.PublisherWithContract;
import com.packagename.myapp.spring.entity.subscription.Subscription;
import com.packagename.myapp.spring.entity.treatment.TreatmentEntity;

import java.util.List;

public interface InsertDao {

    List<PublisherWithContract> getAllPublisherByYearAndHalf(List<String> publisherWithSchedule);

    void setContractIdForPublisher(PublisherWithContract publisher);

    List<String> getPublishersWithSchedule();

    List<Subscription> getSubscriptionWithoutAnnulment(TreatmentEntity entity);
}
