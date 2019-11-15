package com.packagename.myapp.spring.entity.parser.newFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Accept {

    private Integer id;
    private Integer periodTypeId;
    private String name;
    private LocalDate dateBeg;
    private LocalDate dateEnd;
    private Boolean adjust;
    private LocalDate adjustDate;
    private Byte priority;

}
