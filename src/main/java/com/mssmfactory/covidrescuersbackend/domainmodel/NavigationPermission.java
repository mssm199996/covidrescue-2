package com.mssmfactory.covidrescuersbackend.domainmodel;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Duration;
import java.time.LocalDateTime;

@Getter
@Setter
public class NavigationPermission {

    public static String SEQUENCE_ID = "navigation_permission_sequence";

    @Id
    private Long id;

    @NotNull
    @Indexed
    private Long accountId;

    @NotNull
    @Indexed
    private Integer townId;

    @NotNull
    private LocalDateTime from, to;

    @NotNull
    private Duration duration;

    @NotNull
    @NotBlank
    private String cause, destination;
}
