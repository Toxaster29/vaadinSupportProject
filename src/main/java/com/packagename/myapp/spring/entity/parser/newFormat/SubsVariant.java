package com.packagename.myapp.spring.entity.parser.newFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SubsVariant {

    private Integer acceptid;
    private Integer[] regions;
    private Integer msp;
    private Integer price;
    private Integer vatId;
    private Byte state;

    public String getRegionsToString() {
        String regions = "";
        if (this.regions != null) {
            for (Integer region : this.regions) {
                regions += String.valueOf(region) + "; ";
            }
        }
        return regions;
    }

}
