package com.packagename.myapp.spring.entity.parser.newFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ConnectionThematic {

    private Integer oldId;
    private String oldName;
    private String newIds;

}
