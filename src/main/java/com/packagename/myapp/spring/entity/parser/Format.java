package com.packagename.myapp.spring.entity.parser;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class Format {

    private String type;
    private Byte version;
    private LocalDate date;
    private String sender;
    private Directory distribution;
    private Directory supply;
    private Directory expedition;
    private Directory client;
    private Directory publType;
    private Directory thematic;
    private Directory vat;
    private Directory age;
    private Directory country;
    private Directory language;
    private Directory format;
    private Directory time;
    private Directory periodType;
    private Directory region;
    private List<Terrain> terrain;
    private List<Agency> agency;
    private List<Campaign> campaign;

}
