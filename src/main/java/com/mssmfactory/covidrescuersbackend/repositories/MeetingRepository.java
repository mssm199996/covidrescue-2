package com.mssmfactory.covidrescuersbackend.repositories;

import com.mssmfactory.covidrescuersbackend.domainmodel.Meeting;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MeetingRepository extends MongoRepository<Meeting, Long> {

    List<Meeting> findByTriggererAccountIdOrTargetAccountIdOrderByLocalDateTimeDesc(Long triggererAccountId, Long targetAccountId);
}
