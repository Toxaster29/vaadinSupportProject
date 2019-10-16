package com.packagename.myapp.spring.entity.parser;

import lombok.Data;

import java.awt.*;
import java.time.LocalDate;
import java.util.List;

@Data
public class Publication {

    private Long id;
    private Long publTypeId;
    private String title;
    private String annotation;
    private Long countryId;
    private Integer[] regions;
    private Integer[] languages;
    private Integer[] thematics;
    private Byte social;
    private String issn;
    private Long inn;
    private Long vatId;
    private String mediaRegNum;
    private LocalDate mediaRegDate;
    private Image img;
    private List<PublVersion> publVersion;

}
