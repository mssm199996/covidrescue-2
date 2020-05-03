package com.mssmfactory.covidrescuersbackend;

import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.domainmodel.AccountEstablishmentEvent;
import com.mssmfactory.covidrescuersbackend.domainmodel.Establishment;
import com.mssmfactory.covidrescuersbackend.repositories.AccountEstablishmentEventRepository;
import com.mssmfactory.covidrescuersbackend.repositories.AccountRepository;
import com.mssmfactory.covidrescuersbackend.repositories.EstablishmentRepository;
import com.mssmfactory.covidrescuersbackend.utils.propagation.EstablishmentPropagationHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@SpringBootTest
public class EstablishmentPropagationHandlerTest {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private EstablishmentRepository establishmentRepository;

    @Autowired
    private AccountEstablishmentEventRepository accountEstablishmentEventRepository;

    @Autowired
    private EstablishmentPropagationHandler establishmentPropagationHandler;

    private Integer eventStartId = 100;

    public void testAllOverlappings() {
        this.accountEstablishmentEventRepository.deleteAll();

        Account p1 = this.createAccount(101);
        Account p2 = this.createAccount(102);
        Account p3 = this.createAccount(103);
        Account p4 = this.createAccount(104);
        Account p5 = this.createAccount(105);

        Establishment e1 = this.createEstablishment(50);
        Establishment e2 = this.createEstablishment(51);

        LocalDateTime t1 = LocalDateTime.now();
        LocalDateTime t7 = t1.plusDays(1);

        LocalDateTime t2 = t1.plusHours(8),
                t3 = t1.minusHours(2),
                t4 = t1.minusHours(1),
                t5 = t7.minusMinutes(20),
                t6 = t7.plusMinutes(5),
                t8 = t7.plusMinutes(30),
                t9 = t7.plusMinutes(10),
                t10 = t8.minusMinutes(5),
                t11 = t2.plusMinutes(5),
                t12 = t11.plusMinutes(45);

        AccountEstablishmentEvent.AccountEstablishmentEventType input = AccountEstablishmentEvent.AccountEstablishmentEventType.INPUT;
        AccountEstablishmentEvent.AccountEstablishmentEventType output = AccountEstablishmentEvent.AccountEstablishmentEventType.OUTPUT;

        this.createAccountEstablishmentEvent(t1, p1, e1, input);
        this.createAccountEstablishmentEvent(t2, p1, e1, output);
        this.createAccountEstablishmentEvent(t3, p2, e1, input);
        this.createAccountEstablishmentEvent(t4, p2, e1, output);
        this.createAccountEstablishmentEvent(t5, p3, e2, input);
        this.createAccountEstablishmentEvent(t6, p3, e2, output);
        this.createAccountEstablishmentEvent(t7, p1, e2, input);
        this.createAccountEstablishmentEvent(t8, p1, e2, output);
        this.createAccountEstablishmentEvent(t9, p4, e2, input);
        this.createAccountEstablishmentEvent(t10, p4, e2, output);
        this.createAccountEstablishmentEvent(t11, p5, e1, input);
        this.createAccountEstablishmentEvent(t12, p5, e1, output);

        Set<Long> suspecteds = this.establishmentPropagationHandler.propagate(p1.getId());

        Assertions.assertIterableEquals(suspecteds, List.of(p3.getId(), p4.getId()));
    }

    public void testUnlinkedAndDifferentEstablishment() {
        this.accountEstablishmentEventRepository.deleteAll();

        Account p1 = this.createAccount(101);
        Account p2 = this.createAccount(102);
        Account p3 = this.createAccount(103);
        Account p4 = this.createAccount(104);

        Establishment e1 = this.createEstablishment(50);
        Establishment e2 = this.createEstablishment(51);
        Establishment e3 = this.createEstablishment(52);

        LocalDateTime t1 = LocalDateTime.of(LocalDate.now(), LocalTime.of(16, 0));
        LocalDateTime t2 = LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 15));
        LocalDateTime t3 = LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 30));
        LocalDateTime t4 = LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 45));
        LocalDateTime t5 = LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 0));
        LocalDateTime t6 = LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 0));
        LocalDateTime t7 = LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 30));
        LocalDateTime t8 = LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 45));
        LocalDateTime t9 = LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 35));
        LocalDateTime t10 = LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 55));

        AccountEstablishmentEvent.AccountEstablishmentEventType input = AccountEstablishmentEvent.AccountEstablishmentEventType.INPUT;
        AccountEstablishmentEvent.AccountEstablishmentEventType output = AccountEstablishmentEvent.AccountEstablishmentEventType.OUTPUT;

        this.createAccountEstablishmentEvent(t1, p1, e2, input);
        this.createAccountEstablishmentEvent(t2, p1, e2, output);
        this.createAccountEstablishmentEvent(t3, p2, e2, input);
        this.createAccountEstablishmentEvent(t4, p2, e2, output);
        this.createAccountEstablishmentEvent(t5, p1, e1, input);
        this.createAccountEstablishmentEvent(t6, p1, e1, output);
        this.createAccountEstablishmentEvent(t7, p3, e1, input);
        this.createAccountEstablishmentEvent(t8, p3, e1, output);

        this.createAccountEstablishmentEvent(t9, p4, e3, input);
        this.createAccountEstablishmentEvent(t10, p4, e3, output);

        Set<Long> suspecteds = this.establishmentPropagationHandler.propagate(p1.getId());

        Assertions.assertIterableEquals(suspecteds, List.of(p3.getId()));
    }

    public void testWithMissingOutputs() {
        this.accountEstablishmentEventRepository.deleteAll();

        Account p1 = this.createAccount(101);
        Account p2 = this.createAccount(102);
        Account p3 = this.createAccount(103);

        Establishment e1 = this.createEstablishment(50);

        LocalDateTime t1 = LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 0));
        LocalDateTime t2 = LocalDateTime.of(LocalDate.now(), LocalTime.of(19, 0));
        LocalDateTime t3 = LocalDateTime.of(LocalDate.now(), LocalTime.of(18, 30));
        LocalDateTime t4 = LocalDateTime.of(LocalDate.now(), LocalTime.of(17, 0));

        AccountEstablishmentEvent.AccountEstablishmentEventType input = AccountEstablishmentEvent.AccountEstablishmentEventType.INPUT;
        AccountEstablishmentEvent.AccountEstablishmentEventType output = AccountEstablishmentEvent.AccountEstablishmentEventType.OUTPUT;

        this.createAccountEstablishmentEvent(t1, p1, e1, input);
        this.createAccountEstablishmentEvent(t2, p1, e1, output);
        this.createAccountEstablishmentEvent(t3, p2, e1, input);
        this.createAccountEstablishmentEvent(t4, p3, e1, input);

        Set<Long> suspecteds = this.establishmentPropagationHandler.propagate(p1.getId());

        Assertions.assertIterableEquals(suspecteds, List.of(p2.getId()));
    }

    private AccountEstablishmentEvent createAccountEstablishmentEvent(LocalDateTime moment, Account account, Establishment establishment,
                                                                      AccountEstablishmentEvent.AccountEstablishmentEventType type) {
        AccountEstablishmentEvent accountEstablishmentEvent = new AccountEstablishmentEvent();
        accountEstablishmentEvent.setId(Long.valueOf(this.eventStartId++));
        accountEstablishmentEvent.setMoment(moment);
        accountEstablishmentEvent.setEstablishmentId(establishment.getId());
        accountEstablishmentEvent.setEstablishmentEventType(type);
        accountEstablishmentEvent.setAccountId(account.getId());

        this.accountEstablishmentEventRepository.save(accountEstablishmentEvent);

        return accountEstablishmentEvent;
    }

    private Establishment createEstablishment(int i) {
        Establishment establishment = new Establishment();
        establishment.setEmail(i + "@gmail.com");
        establishment.setToken(UUID.randomUUID().toString());
        establishment.setSubscriptionDate(LocalDateTime.now());
        establishment.setId(Long.valueOf(i));
        establishment.setName("Establishment #" + i);
        establishment.setAddress("");

        this.establishmentRepository.save(establishment);

        return establishment;
    }

    private Account createAccount(int i) {
        Account account = new Account();
        account.setEmail("account_" + i + "@gmail.com");
        account.setUsername("User [" + i + "]");
        account.setId(Long.valueOf(i));
        account.setFirstName("FiN(" + i + ")");
        account.setFamillyName("FaN(" + i + ")");
        account.setAccountState(Account.AccountState.HEALTHY);
        account.setNumberOfMeetings(0);
        account.setToken(UUID.randomUUID().toString());
        account.setAccountRole(Account.AccountRole.USER);

        this.accountRepository.save(account);

        return account;
    }
}
