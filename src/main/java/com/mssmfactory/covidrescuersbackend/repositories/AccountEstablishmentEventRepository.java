package com.mssmfactory.covidrescuersbackend.repositories;

import com.mssmfactory.covidrescuersbackend.domainmodel.AccountEstablishmentEvent;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AccountEstablishmentEventRepository extends MongoRepository<AccountEstablishmentEvent, Long> {

    List<AccountEstablishmentEvent> findAllByAccountId(Long accountId);

    List<AccountEstablishmentEvent> findAllByEstablishmentIdAndMomentBetween(Long establishmentId, LocalDateTime start, LocalDateTime end);
}
