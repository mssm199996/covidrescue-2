package com.mssmfactory.covidrescuersbackend.repositories;

import com.mssmfactory.covidrescuersbackend.domainmodel.PendingAccountRegistration;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface PendingAccountRegistrationRepository extends MongoRepository<PendingAccountRegistration, Long> {

    Optional<PendingAccountRegistration> findByEmail(String email);

    Optional<PendingAccountRegistration> findByEmailAndToken(String email, String token);
}
