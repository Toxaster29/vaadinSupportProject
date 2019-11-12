package com.packagename.myapp.spring.entity.parser.oldFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class STopicIn {

    private Integer catalogId;
    private Integer rubricId;
    private String publicationId;

    public STopicIn(List<String> params) {
        this(Integer.parseInt(params.get(0)), Integer.parseInt(params.get(1)), params.get(2));
    }
}
