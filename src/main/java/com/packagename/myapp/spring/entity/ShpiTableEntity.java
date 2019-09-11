package com.packagename.myapp.spring.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ShpiTableEntity {

    private String codeShpi;

    private Integer actionCount;

    private Boolean haveProblem;

}
