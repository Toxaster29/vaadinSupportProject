package com.packagename.myapp.spring.entity.subscription;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@AllArgsConstructor
@Getter
public class Subscription {

    private String publicationCode;
    private Integer msp;
    private Integer alloc1;
    private Integer alloc2;
    private Integer alloc3;
    private Integer alloc4;
    private Integer alloc5;
    private Integer alloc6;
    private Integer alloc7;
    private Integer alloc8;
    private Integer alloc9;
    private Integer alloc10;
    private Integer alloc11;
    private Integer alloc12;
    private Integer allocSum;
    private Integer deliveryType;
    private String postCode;
    private String region;
    private String area;
    private String city;
    private String city1;
    private String street;
    private String house;
    private String housing;
    private String building;
    private String flat;
    private String surname;
    private String name;
    private String patronymic;
    private String orgName;
    private LocalDate startDate;
}
