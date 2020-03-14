package com.mssmfactory.covidrescuersbackend.services;

import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.domainmodel.Meeting;
import com.mssmfactory.covidrescuersbackend.dto.DetailedMeetingResponse;
import com.mssmfactory.covidrescuersbackend.dto.MeetingRequest;
import com.mssmfactory.covidrescuersbackend.exceptions.NoSuchAccountException;
import com.mssmfactory.covidrescuersbackend.repositories.AccountRepository;
import com.mssmfactory.covidrescuersbackend.repositories.MeetingRepository;
import com.mssmfactory.covidrescuersbackend.repositories.SequenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
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

    public Meeting save(MeetingRequest meetingRequest) {
        Optional<Account> triggererAccount = this.accountRepository.findByPhoneNumber(meetingRequest.getTriggererAccountPhoneNumber());

        if (triggererAccount.isPresent()) {
            Optional<Account> targetedAccount = this.accountRepository.findByPhoneNumber(meetingRequest.getTargetAccountPhoneNumber());

            if (targetedAccount.isPresent()) {
                Meeting meeting = new Meeting();
                meeting.setId(this.sequenceService.getNextValue(Meeting.SEQUENCE_ID));
                meeting.setLatitude(meetingRequest.getLatitude());
                meeting.setLocalDateTime(LocalDateTime.now());
                meeting.setLongitude(meetingRequest.getLongitude());
                meeting.setTargetAccountId(targetedAccount.get().getId());
                meeting.setTriggererAccountId(triggererAccount.get().getId());
                meeting.setTriggererAccountState(triggererAccount.get().getAccountState());
                meeting.setTargetAccountState(targetedAccount.get().getAccountState());

                this.meetingRepository.save(meeting);
                this.sequenceService.setNextValue(Meeting.SEQUENCE_ID);

                return meeting;
            } else throw new NoSuchAccountException(meetingRequest.getTargetAccountPhoneNumber());
        } else throw new NoSuchAccountException(meetingRequest.getTriggererAccountPhoneNumber());
    }

    public List<DetailedMeetingResponse> findDetailedMeetings(Long accountId) {
        Optional<Account> accountOptional = this.accountRepository.findById(accountId);

        if (accountOptional.isPresent()) {
            Account account = accountOptional.get();

            List<Meeting> meetings = this.meetingRepository.
                    findByTriggererAccountIdOrTargetAccountIdOrderByLocalDateTimeDesc(account.getId(),
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
        } else throw new NoSuchAccountException(accountId);
    }
}
