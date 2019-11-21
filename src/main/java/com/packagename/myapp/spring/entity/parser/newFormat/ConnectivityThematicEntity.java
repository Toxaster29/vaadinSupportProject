package com.packagename.myapp.spring.entity.parser.newFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ConnectivityThematicEntity {

    private Integer oldId;
    private String oldName;
    private List<Directory> directory;

    public String getDirectoryName() {
        String names = "";
        for (Directory dir : this.getDirectory()) {
            names += dir.getName()+ "; ";
        }
        return names;
    }

    public String getDirectoryId() {
        String id = "";
        for (Directory dir : this.getDirectory()) {
           id += dir.getId()+ ";";
        }
        return id;
    }

}
