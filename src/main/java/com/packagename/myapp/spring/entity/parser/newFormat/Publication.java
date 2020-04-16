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
    private Integer publtypeid;
    private String title;
    private String annotation;
    private Integer ageId;
    private Integer countryid;
    private Integer[] regions;
    private Integer[] languages;
    private Integer[] thematics;
    private Byte social;
    private String issn;
    private Long inn;
    private Integer vatid;
    private String mediaregnum;
    private LocalDate mediaregdate;
    private Image img;
    private List<PublVersion> publversion;

    public String getRegionsToString() {
        if (this.regions != null) {
            String line = "";
            for (Integer region : this.regions) {
                line += region + "; ";
            }
            return line;
        } else return null;
    }

    public String getLanguagesToString() {
        if (this.languages != null) {
            String line = "";
            for (Integer language : this.languages) {
                line += language + "; ";
            }
            return line;
        } else return null;
    }

    public String getThematicsToString() {
        if (this.thematics != null) {
            String line = "";
            for (Integer theme : this.thematics) {
                line += theme + "; ";
            }
            return line;
        } else return null;
    }

}
