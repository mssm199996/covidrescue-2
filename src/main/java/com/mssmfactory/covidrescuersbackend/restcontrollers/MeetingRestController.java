package com.mssmfactory.covidrescuersbackend.restcontrollers;

import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.domainmodel.Meeting;
import com.mssmfactory.covidrescuersbackend.dto.DetailedMeetingResponse;
import com.mssmfactory.covidrescuersbackend.dto.MeetingRequest;
import com.mssmfactory.covidrescuersbackend.repositories.MeetingRepository;
import com.mssmfactory.covidrescuersbackend.services.AccountService;
import com.mssmfactory.covidrescuersbackend.services.MeetingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("meeting")
public class MeetingRestController {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private MeetingRepository meetingRepository;

    @GetMapping("findDetailedMeetingsByLoggedInAccount")
    public List<DetailedMeetingResponse> findDetailedMeetingsByLoggedInAccount() {
        Account loggedInAccount = this.accountService.findLoggedInAccount();

        if (loggedInAccount != null)
            return this.meetingService.findDetailedMeetingsByTriggeredOrTarget(loggedInAccount);

        return null;
    }

    @GetMapping("findDetailedMeetings/{accountId}")
    public List<DetailedMeetingResponse> findDetailedMeetings(@PathVariable("accountId") Long accountId) {
        List<DetailedMeetingResponse> detailedMeetingResponses = this.meetingService.findDetailedMeetingsByTriggeredOrTarget(accountId);

        return detailedMeetingResponses;
    }

    // ---------------------------------------------------------------------------------------------------

    @GetMapping("countAllByAccountStateNearPosition")
    public Long countAllByAccountStateNearPosition(
            @RequestParam("longitude") Double longitude,
            @RequestParam("latitude") Double latitude,
            @RequestParam("radius") Double radius,
            @RequestParam("accountState") Account.AccountState accountState) {
        return this.meetingRepository.countAllByPositionIsNear(longitude, latitude, 0, radius, accountState);
    }

    // ---------------------------------------------------------------------------------------------------

    @PostMapping
    public ResponseEntity<Meeting> save(@Valid @RequestBody MeetingRequest meetingRequest) {
        Account account = this.accountService.findLoggedInAccount();

        if (account != null) {
            Meeting meeting = this.meetingService.save(account, meetingRequest);

            return new ResponseEntity(meeting, HttpStatus.OK);
        } else
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
    }
}
