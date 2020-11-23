package com.packagename.myapp.spring.entity.order;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class OnlineOrder {

    private Integer id;
    private String hid;
    private LocalDateTime createDate;

}
