package com.mssmfactory.covidrescuersbackend.domainmodel;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.mssmfactory.covidrescuersbackend.utils.serialization.DurationSerializer;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;

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

    private Integer maxNumberOfAllowedPermissionsAtOnce;

    @JsonSerialize(using = DurationSerializer.class)
    private Duration defaultNavigationPermissionDuration;
}
