package com.mssmfactory.covidrescuersbackend.domainmodel;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.web.bind.annotation.GetMapping;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Document
public class Meeting {

    public static String SEQUENCE_ID = "meetings_sequence";

    @Id
    @Indexed
    private Long id;

    @NotNull
    @Indexed
    private Long triggererAccountId, targetAccountId;

    @NotNull
    private Account.AccountState triggererAccountState, targetAccountState;

    private LocalDateTime moment;

    @NotNull
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint position;
}
