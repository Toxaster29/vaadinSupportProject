package com.packagename.myapp.spring.entity.parser.newFormat;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Agency {

    private Integer id;
    private Integer supplyId;
    private Integer parentId;
    private String name;
    private String inn;
    private String email;
    private String phone;

}
