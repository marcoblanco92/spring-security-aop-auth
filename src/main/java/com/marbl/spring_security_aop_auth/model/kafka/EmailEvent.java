package com.marbl.spring_security_aop_auth.model.kafka;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class EmailEvent {
    private String to;
    private String subject;
    private String body;
}