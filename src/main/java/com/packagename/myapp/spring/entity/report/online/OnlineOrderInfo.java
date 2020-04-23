package com.packagename.myapp.spring.entity.report.online;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class OnlineOrderInfo {

    private Integer id;
    private String clientHid;
    private Integer regionId;

}
