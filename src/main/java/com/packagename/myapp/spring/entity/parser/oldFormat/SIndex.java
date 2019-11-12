package com.packagename.myapp.spring.entity.parser.oldFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class SIndex {

    private String id;
    private String publicationId;
    private String outputCondition;
    private String subsCategory;
    private String shortOutputCondition;
    private String type;
    private String system;
    private String agencyId;
    private String complexName;
    private String complexAnnotation;
    private Integer numberByCatalog;
    private Integer sheetNumberByCatalog;
    private String description;

    public Integer getAgencyId() {
        String agencyId = this.agencyId.split(",")[0];
        return Integer.parseInt(agencyId);
    }

    public SIndex(List<String> params) {
        this(params.get(0), params.get(1), params.get(2), params.get(3), params.get(4), params.get(5), params.get(6),
                params.get(7), params.get(8), params.get(9), Integer.parseInt(params.get(10)), Integer.parseInt(params.get(11)),
                params.get(12));
    }
}
