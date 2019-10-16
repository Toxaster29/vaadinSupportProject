package com.packagename.myapp.spring.entity.parser;

import lombok.Data;

@Data
public class Agency {

    private Long id;
    private Long supplyId;
    private Long parentId;
    private String name;
    private String inn;
    private String email;
    private String phone;

}
