package com.mssmfactory.covidrescuersbackend.repositories;

import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.domainmodel.Meeting;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface MeetingRepository extends MongoRepository<Meeting, Long> {

    @Query(value = "{" +
                "'position':{" +
                    "$nearSphere:{" +
                        "$geometry:{" +
                            "type:'Point'," +
                            "coordinates:[?0,?1]" +
                        "}," +
                            "$minDistance:?2," +
                            "$maxDistance:?3" +
                        "}" +
                    "}," +
                "$or:[" +
                    "{'triggererAccountState':?4}," +
                    "{'targetAccountState':?4}" +
                "]" +
            "}", count = true)
    Long countAllByPositionIsNear(double longitude, double latitude, double min, double max, Account.AccountState accountState);

    List<Meeting> findByTriggererAccountIdOrTargetAccountId(Long triggererAccountId, Long targetAccountId);
    List<Meeting> findByTriggererAccountIdOrTargetAccountIdOrderByMomentDesc(Long triggererAccountId,
                                                                             Long targetAccountId);

    @Query("{" +
                "$or:[ " +
                    "{'triggererAccountId':?0}," +
                    "{'targetAccountId':?1}" +
                "]," +
                "'moment':{$gt:?2}" +
            "}")
    List<Meeting> findAllByTriggererAccountIdOrTargetAccountIdAndMomentGreaterThan(Long triggeredAccountId, Long targetAccountId,
                                                                                   LocalDateTime currentLocalDateTime);
}
