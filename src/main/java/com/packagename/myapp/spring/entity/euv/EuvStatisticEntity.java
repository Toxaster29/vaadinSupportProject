package com.packagename.myapp.spring.entity.euv;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EuvStatisticEntity {

    private Integer euvOperationCount;
    private Integer eoOperationCount;
    private Integer userWebFromLoginCount;

}
