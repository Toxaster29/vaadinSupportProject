package com.packagename.myapp.spring.entity.parser.newFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Accept {

    private Integer id;
    private Integer periodtypeid;
    private String name;
    private LocalDate datebeg;
    private LocalDate dateend;
    private Boolean adjust;
    private LocalDate adjustdate;
    private Byte priority;

}
