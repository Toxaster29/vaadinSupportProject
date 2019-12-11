package com.packagename.myapp.spring.entity.schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class PublisherSchedule {

    private String publisherId;
    private Integer contractId;
    private List<ScheduleDates> dates;

}
