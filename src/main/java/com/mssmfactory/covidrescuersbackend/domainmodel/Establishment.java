package com.mssmfactory.covidrescuersbackend.domainmodel;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;

import javax.validation.constraints.Email;
import java.time.LocalDateTime;

@Getter
@Setter
public class Establishment {

    public static String SEQUENCE_ID = "establishments_sequence";

    @Id
    @Indexed
    private Long id;

    @Indexed
    private Integer cityId, townId;

    @Indexed
    private String token;

    @Email
    @Indexed
    private String email;

    private String name, address;

    private LocalDateTime subscriptionDate;

    @Override
    public boolean equals(Object o) {
        return o instanceof Establishment && this.getId().equals(((Establishment) o).getId());
    }
}
