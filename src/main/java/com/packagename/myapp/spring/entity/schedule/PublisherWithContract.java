package com.packagename.myapp.spring.entity.schedule;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PublisherWithContract {

    private String hid;
    private Integer contractId;
    private Boolean isLocal;

}
