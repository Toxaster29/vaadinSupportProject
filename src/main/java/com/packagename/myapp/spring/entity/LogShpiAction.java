package com.packagename.myapp.spring.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
public class LogShpiAction {

    private String codeShpi;

    private LocalDateTime createDate;

    private String systemId;

    private Byte serverId;

    private String actionName;

    private Boolean status;

    private String description;

}
