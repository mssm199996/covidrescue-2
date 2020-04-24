package com.mssmfactory.covidrescuersbackend.repositories;

import com.mssmfactory.covidrescuersbackend.domainmodel.ApplicationUpdate;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface ApplicationUpdateRepository extends MongoRepository<ApplicationUpdate, Integer> {

    Optional<ApplicationUpdate> findFirstByLastVersion(Boolean lastVersion);
}
