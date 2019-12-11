package com.packagename.myapp.spring.entity.schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class ScheduleDates {

    private String name;
    private String month;
    private LocalDate docGeneration;
    private LocalDate tfpsDate;
    private LocalDate onlineDate;
    private LocalDate publisherDate;

}
