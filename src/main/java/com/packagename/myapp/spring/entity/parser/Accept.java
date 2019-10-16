package com.packagename.myapp.spring.entity.parser;

import lombok.Data;

import java.time.LocalDate;

@Data
public class Accept {

    private Long id;
    private Long periodTypeId;
    private String name;
    private LocalDate dateBeg;
    private LocalDate dateEnd;
    private Byte adjust;
    private LocalDate adjustDate;
    private Byte priority;

}
