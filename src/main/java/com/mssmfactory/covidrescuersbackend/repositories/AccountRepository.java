package com.mssmfactory.covidrescuersbackend.repositories;

import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface AccountRepository extends MongoRepository<Account, Long> {

    Optional<Account> findByEmail(String email);

    Optional<Account> findByUsername(String username);

    Long countAllByAccountRole(Account.AccountRole accountRole);

    Long countAllByCityIdAndAccountState(Integer cityId, Account.AccountState accountState);

    Long countAllByTownIdAndAccountState(Integer townId, Account.AccountState accountState);

    List<Account> findAllByAccountStateAndIdIn(Account.AccountState accountState, Set<Long> ids);

    List<Account> findAllByEmailStartingWith(String phoneNumber);


}
