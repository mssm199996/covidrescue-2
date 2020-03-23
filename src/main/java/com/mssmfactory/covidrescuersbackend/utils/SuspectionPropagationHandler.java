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

        /*this.propagate(parentAccountId, null, result, new HashSet<>());*/

        this.propagate(parentAccountId, result);

        return result;
    }

    private void propagate(Long parentAccountId, Set<Long> accountsToUpdateIds) {
        List<Meeting> meetingList;

        meetingList = this.meetingRepository.findByTriggererAccountIdOrTargetAccountId(parentAccountId,
                parentAccountId);

        for (Meeting meeting : meetingList) {
            Long partnerId = meeting.getTriggererAccountId().equals(parentAccountId) ?
                    meeting.getTargetAccountId() :
                    meeting.getTriggererAccountId();

            // Mark this account as treated
            accountsToUpdateIds.add(partnerId);
        }
    }

    /*private void propagate(Long parentAccountId, Meeting parentToChildRelation, Set<Long> accountsToUpdateIds,
                           Set<Long> visitedMeetingIds) {
        List<Meeting> meetingList;

        if (parentToChildRelation == null) {
            meetingList = this.meetingRepository.findByTriggererAccountIdOrTargetAccountId(parentAccountId,
                    parentAccountId);
        } else {
            meetingList = this.meetingRepository.findAllByTriggererAccountIdOrTargetAccountIdAndMomentGreaterThan(
                    parentAccountId, parentAccountId, parentToChildRelation.getMoment());
        }

        for (Meeting meeting : meetingList) {
            if (!visitedMeetingIds.contains(meeting.getId())) {
                Long partnerId = meeting.getTriggererAccountId().equals(parentAccountId) ?
                        meeting.getTargetAccountId() :
                        meeting.getTriggererAccountId();

                // Mark this account as treated
                accountsToUpdateIds.add(partnerId);
                visitedMeetingIds.add(meeting.getId());

                this.propagate(partnerId, meeting, accountsToUpdateIds, visitedMeetingIds);
            }
        }
    }*/
}
