package com.packagename.myapp.spring.entity.parser.newFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class Format {

    private String type;
    private Byte version;
    private LocalDate date;
    private String sender;
    private List<Directory> distribution;
    private List<Directory> supply;
    private List<Directory> expedition;
    private List<Directory> client;
    private List<Directory> publtype;
    private List<Directory> thematic;
    private List<Directory> vat;
    private List<Directory> age;
    private List<Directory> country;
    private List<Directory> language;
    private List<Directory> format;
    private List<Directory> time;
    private List<Directory> periodtype;
    private List<Directory> region;
    private List<Terrain> terrain;
    private List<Agency> agency;
    private List<Campaign> campaign;

}
