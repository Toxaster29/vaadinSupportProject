package com.packagename.myapp.spring.entity.parser.newFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.awt.*;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
public class Publication {

    private Long id;
    private Integer publTypeId;
    private String title;
    private String annotation;
    private Integer ageId;
    private Integer countryId;
    private Integer[] regions;
    private Integer[] languages;
    private Integer[] thematics;
    private Byte social;
    private String issn;
    private Long inn;
    private Integer vatId;
    private String mediaRegNum;
    private LocalDate mediaRegDate;
    private Image img;
    private List<PublVersion> publVersion;

}
