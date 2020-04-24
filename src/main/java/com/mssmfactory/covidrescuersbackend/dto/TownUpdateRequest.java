package com.mssmfactory.covidrescuersbackend.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Getter
@Setter
public class TownUpdateRequest {

    @NotNull
    private Integer townId, maxNumberOfAllowedPermissionsAtOnce;

    @NotNull
    private TownUpdateRequestDuration defaultNavigationPermissionDuration;

    @Getter
    @Setter
    public class TownUpdateRequestDuration {

        @NotNull
        @PositiveOrZero
        private Long hours, minutes;
    }
}
