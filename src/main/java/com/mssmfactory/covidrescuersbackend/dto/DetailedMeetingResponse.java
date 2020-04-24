package com.mssmfactory.covidrescuersbackend.dto;

import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.domainmodel.Meeting;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class DetailedMeetingResponse {

    private Account triggererAccount, targetAccount;
    private Meeting meeting;

    public DetailedMeetingResponse(Meeting meeting, Account triggererAccount, Account targetAccount) {
        this.meeting = meeting;
        this.triggererAccount = triggererAccount;
        this.targetAccount = targetAccount;
    }
}
