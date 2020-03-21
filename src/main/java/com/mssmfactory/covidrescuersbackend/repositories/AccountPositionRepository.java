package com.mssmfactory.covidrescuersbackend.repositories;

import com.mssmfactory.covidrescuersbackend.domainmodel.AccountPosition;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AccountPositionRepository extends MongoRepository<AccountPosition, Long> {

    List<AccountPosition> findAllByAccountId(Long accountId);
}
