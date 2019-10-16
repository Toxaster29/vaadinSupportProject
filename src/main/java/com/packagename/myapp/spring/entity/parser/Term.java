package com.packagename.myapp.spring.entity.parser;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Term {

    private Byte month;
    private LocalDate date;

}
