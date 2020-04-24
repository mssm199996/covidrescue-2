package com.mssmfactory.covidrescuersbackend.repositories;

import com.mssmfactory.covidrescuersbackend.domainmodel.NavigationPermission;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface NavigationPermissionRepository extends MongoRepository<NavigationPermission, Long> {

    List<NavigationPermission> findAllByAccountId(Long accountId);

    Optional<NavigationPermission> findByAccountIdAndFromLessThanEqualAndToGreaterThanEqual(
            Long accountId, LocalDateTime bottomBound, LocalDateTime upperBound);

    Long countAllByTownIdAndFromLessThanEqualAndToGreaterThanEqual(Integer townId, LocalDateTime bottomBound,
                                                                   LocalDateTime upperBound);
}
