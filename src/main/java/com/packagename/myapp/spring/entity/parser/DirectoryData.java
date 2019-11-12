package com.packagename.myapp.spring.entity.parser;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DirectoryData {

    private Integer id;
    private String name;
    private Integer directoryId;

}
