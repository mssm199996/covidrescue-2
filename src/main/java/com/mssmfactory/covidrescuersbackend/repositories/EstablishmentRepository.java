package com.mssmfactory.covidrescuersbackend.repositories;

import com.mssmfactory.covidrescuersbackend.domainmodel.Establishment;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface EstablishmentRepository extends MongoRepository<Establishment, Long> {

    Optional<Establishment> findByEmail(String email);

    Optional<Establishment> findByToken(String token);

    void deleteByEmail(String email);
}
