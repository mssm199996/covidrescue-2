package com.mssmfactory.covidrescuersbackend.repositories;

import com.mssmfactory.covidrescuersbackend.domainmodel.Town;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TownRepository extends MongoRepository<Town, Integer> {
}
