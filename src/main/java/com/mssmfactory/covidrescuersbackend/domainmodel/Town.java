package com.mssmfactory.covidrescuersbackend.domainmodel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@ToString
@Document
public class Town {

    @Id
    @Indexed
    private Integer id;

    @Indexed
    private Integer cityId;

    private String name;
}
