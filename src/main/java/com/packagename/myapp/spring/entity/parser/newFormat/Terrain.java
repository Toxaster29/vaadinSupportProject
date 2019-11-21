package com.packagename.myapp.spring.entity.parser.newFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Terrain {

    private Integer id;
    private String name;
    private Integer regionId;
    private List<String> zipcodes;

    public String zipcodesToString() {
        String codes = "";
        if (this.zipcodes != null) {
            for (String line : this.zipcodes) {
                codes += line;
            }
        }
        return codes;
    }

}
