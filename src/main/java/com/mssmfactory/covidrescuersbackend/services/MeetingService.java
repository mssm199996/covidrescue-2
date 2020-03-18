package com.mssmfactory.covidrescuersbackend.services;

import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.domainmodel.Meeting;
import com.mssmfactory.covidrescuersbackend.dto.DetailedMeetingResponse;
import com.mssmfactory.covidrescuersbackend.dto.MeetingRequest;
import com.mssmfactory.covidrescuersbackend.exceptions.NoSuchAccountException;
import com.mssmfactory.covidrescuersbackend.repositories.AccountRepository;
import com.mssmfactory.covidrescuersbackend.repositories.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MeetingService {

    @Autowired
    private MeetingRepository meetingRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private SequenceService sequenceService;

    public Meeting save(Account triggerer, MeetingRequest meetingRequest) {
        Optional<Account> targetedAccount = this.accountRepository.findByPhoneNumber(meetingRequest.getTargetAccountPhoneNumber());

        if (targetedAccount.isPresent()) {
            Account targeted = targetedAccount.get();

            Meeting meeting = new Meeting();
            meeting.setId(this.sequenceService.getNextValue(Meeting.SEQUENCE_ID));
            meeting.setMoment(LocalDateTime.now());
            meeting.setTargetAccountId(targeted.getId());
            meeting.setTriggererAccountId(triggerer.getId());
            meeting.setTriggererAccountState(triggerer.getAccountState());
            meeting.setTargetAccountState(targeted.getAccountState());
            meeting.setPosition(new GeoJsonPoint(
                    meetingRequest.getLongitude(),
                    meetingRequest.getLatitude())
            );

            this.meetingRepository.save(meeting);
            this.sequenceService.setNextValue(Meeting.SEQUENCE_ID);

            this.incrementNumberOfMeetings(triggerer);
            this.incrementNumberOfMeetings(targeted);

            return meeting;
        } else throw new NoSuchAccountException(meetingRequest.getTargetAccountPhoneNumber());
    }

    // -----------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------

    public List<DetailedMeetingResponse> findDetailedMeetingsByTriggeredOrTarget(Long accountId) {
        Optional<Account> accountOptional = this.accountRepository.findById(accountId);

        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();

            return this.findDetailedMeetingsByTriggeredOrTarget(account);
        } else throw new NoSuchAccountException(accountId);
    }

    public List<DetailedMeetingResponse> findDetailedMeetingsByTriggeredOrTarget(Account account) {
        List<Meeting> meetings = this.meetingRepository.
                findByTriggererAccountIdOrTargetAccountIdOrderByMomentDesc(account.getId(),
                        account.getId());

        List<DetailedMeetingResponse> detailedMeetingResponses = new ArrayList<>(meetings.size());

        for (Meeting meeting : meetings) {
            Optional<Account> triggererAccount = this.accountRepository.findById(meeting.getTriggererAccountId());
            Optional<Account> targetAccount = this.accountRepository.findById(meeting.getTargetAccountId());

            DetailedMeetingResponse detailedMeetingResponse = new DetailedMeetingResponse();
            detailedMeetingResponse.setMeeting(meeting);

            if (targetAccount.isPresent())
                detailedMeetingResponse.setTargetAccount(targetAccount.get());

            if (triggererAccount.isPresent())
                detailedMeetingResponse.setTriggererAccount(triggererAccount.get());

            detailedMeetingResponses.add(detailedMeetingResponse);
        }

        return detailedMeetingResponses;
    }

    // -----------------------------------------------------------------------------------------------
    // -----------------------------------------------------------------------------------------------

    private void incrementNumberOfMeetings(Account account) {
        account.setNumberOfMeetings(account.getNumberOfMeetings() + 1);

        this.accountRepository.save(account);
    }
}
