package com.mssmfactory.covidrescuersbackend.domainmodel;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document
public class Sequence {

    @Id
    private String id;

    private Long nextValue;
}
