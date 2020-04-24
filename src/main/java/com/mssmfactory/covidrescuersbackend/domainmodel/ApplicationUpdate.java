package com.mssmfactory.covidrescuersbackend.domainmodel;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
public class ApplicationUpdate {

    public static String SEQUENCE_ID = "application_update_sequence";

    @Id
    private Long id;

    @NotNull
    private LocalDateTime releaseDateTime;

    @NotNull
    private Boolean lastVersion;

    @NotNull
    @NotBlank
    private String version, url, description;
}
