package com.packagename.myapp.spring.dto;

import com.packagename.myapp.spring.entity.schedule.PublisherWithContract;

import java.util.List;

public interface InsertDao {
    List<PublisherWithContract> getAllPublisherByYearAndHalf(List<String> publisherWithSchedule);

    void setContractIdForPublisher(PublisherWithContract publisher);

    List<String> getPublishersWithSchedule();
}
