package com.mssmfactory.covidrescuersbackend.restcontrollers;

import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.domainmodel.Meeting;
import com.mssmfactory.covidrescuersbackend.dto.DetailedMeetingResponse;
import com.mssmfactory.covidrescuersbackend.dto.MeetingRequest;
import com.mssmfactory.covidrescuersbackend.services.MeetingService;
import lombok.Getter;
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

    @GetMapping("findDetailedMeetings/{accountId}")
    public List<DetailedMeetingResponse> findDetailedMeetings(@PathVariable("accountId") Long accountId) {
        List<DetailedMeetingResponse> detailedMeetingResponses = this.meetingService.findDetailedMeetings(accountId);

        return detailedMeetingResponses;
    }

    @PostMapping
    public ResponseEntity<Meeting> save(@Valid @RequestBody MeetingRequest meetingRequest) {
        Meeting meeting = this.meetingService.save(meetingRequest);

        return new ResponseEntity(meeting, HttpStatus.OK);
    }
}
