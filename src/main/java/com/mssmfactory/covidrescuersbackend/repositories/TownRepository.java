package com.mssmfactory.covidrescuersbackend.repositories;

import com.mssmfactory.covidrescuersbackend.domainmodel.Town;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TownRepository extends MongoRepository<Town, Integer> {

    List<Town> findAllByCityId(Integer cityId);
}
