package com.mssmfactory.covidrescuersbackend.utils;

import com.mssmfactory.covidrescuersbackend.domainmodel.Meeting;
import com.mssmfactory.covidrescuersbackend.repositories.MeetingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class SuspectionPropagationHandler {

    @Autowired
    private MeetingRepository meetingRepository;

    public Set<Long> propagate(Long parentAccountId) {
        Set<Long> result = new HashSet<>();

        this.propagate(parentAccountId, null, result, new HashSet<>());

        return result;
    }

    private void propagate(Long parentAccountId, Meeting parentToChildRelation, Set<Long> accountsToUpdateIds, Set<Meeting> visitedMeetings) {
        List<Meeting> meetingList;

        if (parentToChildRelation == null) {
            meetingList = this.meetingRepository.findByTriggererAccountIdOrTargetAccountId(parentAccountId,
                    parentAccountId);
        } else {
            meetingList = this.meetingRepository.findAllByTriggererAccountIdOrTargetAccountIdAndMomentGreaterThan(
                    parentAccountId, parentAccountId, parentToChildRelation.getMoment());
        }

        for (Meeting meeting : meetingList) {
            if (!visitedMeetings.contains(meeting)) {
                Long partnerId = meeting.getTriggererAccountId() == parentAccountId ?
                        meeting.getTargetAccountId() :
                        meeting.getTriggererAccountId();

                // Mark this account as treated
                accountsToUpdateIds.add(partnerId);
                visitedMeetings.add(meeting);

                this.propagate(partnerId, meeting, accountsToUpdateIds, visitedMeetings);
            }
        }
    }
}
