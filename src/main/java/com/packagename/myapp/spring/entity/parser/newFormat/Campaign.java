package com.packagename.myapp.spring.entity.parser.newFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Campaign {

    private Integer year;
    private Byte yearsub;
    private List<Accept> accept;
    private List<Publication> publication;
    private List<Catalog> catalog;

}
