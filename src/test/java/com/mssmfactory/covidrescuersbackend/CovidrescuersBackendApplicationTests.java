package com.mssmfactory.covidrescuersbackend;

import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.domainmodel.Meeting;
import com.mssmfactory.covidrescuersbackend.repositories.AccountRepository;
import com.mssmfactory.covidrescuersbackend.repositories.MeetingRepository;
import com.mssmfactory.covidrescuersbackend.services.AccountService;
import org.apache.commons.lang3.RandomUtils;
import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootTest
class CovidrescuersBackendApplicationTests {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private MeetingRepository meetingRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void fullyTestPropagation() {
        this.meetingRepository.deleteAll();

        final int numberOfDays = 1;
        final int numberOfMeetingsPerDayPerAccount = 8;

        final int numberOfAccounts = 100;
        final int numberOfRelations = numberOfAccounts * numberOfMeetingsPerDayPerAccount * numberOfDays;

        ArrayList<Account> accounts = new ArrayList<>(numberOfAccounts);
        ArrayList<Meeting> meetings = new ArrayList<>(numberOfRelations);

        Map<Long, Account> accountMap = new HashMap<>();

        for (int i = 0; i < numberOfAccounts; i++) {
            Account account = this.createTestAccount("P" + i);
            accounts.add(account);
            accountMap.put(account.getId(), account);
        }

        for (int i = 0; i < numberOfRelations; i++) {
            Account triggerer = this.pickRandomAccount(accounts, null);
            Account targeted = this.pickRandomAccount(accounts, triggerer);

            Meeting meeting = this.createTestMeeting(triggerer, targeted);
            meetings.add(meeting);
        }

        Account contaminatedAccount = this.pickRandomAccount(accounts, null);
        contaminatedAccount.setAccountState(Account.AccountState.CONTAMINATED);

        System.out.println("Contaminated account: " + contaminatedAccount.getId());
        this.accountRepository.save(contaminatedAccount);

        System.out.println("Propagation started");
        this.accountService.propagateOnContaminated(contaminatedAccount.getId());
        System.out.println("Propagation finished");

        List<Meeting> orderedMeetings = meetings.stream().sorted(Comparator.comparing(Meeting::getMoment))
                .collect(Collectors.toList());

        System.out.println("Simulation started");
        for (Meeting meeting : orderedMeetings) {
            System.out.println("meeting: " + meeting);

            Account a1 = accountMap.get(meeting.getTriggererAccountId());
            Account a2 = accountMap.get(meeting.getTargetAccountId());

            Account.AccountState s1 = a1.getAccountState();
            Account.AccountState s2 = a2.getAccountState();

            /* HH -> Nothing
               HS -> 1 = S
               HC -> 1 = S
               SH -> 2 = S
               SS -> Nothing
               SC -> Nothing
               CH -> 2 = S
               CS -> Nothing
               CC -> Nothing
             */

            System.out.println("a1: " + a1);
            System.out.println("a2: " + a2);

            if (s1 == Account.AccountState.HEALTHY && s2 == Account.AccountState.SUSPECTED)
                a1.setAccountState(Account.AccountState.SUSPECTED);
            else if (s1 == Account.AccountState.HEALTHY && s2 == Account.AccountState.CONTAMINATED)
                a1.setAccountState(Account.AccountState.SUSPECTED);
            else if (s1 == Account.AccountState.SUSPECTED && s2 == Account.AccountState.HEALTHY)
                a2.setAccountState(Account.AccountState.SUSPECTED);
            else if (s1 == Account.AccountState.CONTAMINATED && s2 == Account.AccountState.HEALTHY)
                a2.setAccountState(Account.AccountState.SUSPECTED);

            System.out.println("a1: " + a1);
            System.out.println("a2: " + a2);
            System.out.println("---------------------->");
        }

        System.out.println("Simulation finished");
        System.out.println("-----------------------------------------------------------------");
        List<Account> updatedAccounts = this.accountRepository.findAll();

        for (Account account : updatedAccounts) {
            System.out.println(":=> " + account);
        }

        for (Account account : updatedAccounts) {
            Account twinAccount = accountMap.get(account.getId());

            if (twinAccount != null) {
                System.out.println("Account: " + account);
                System.out.println("Twin Account: " + twinAccount);

                assert account.getAccountState().equals(twinAccount.getAccountState());
                System.out.println("------------------------------------------------");
            }
        }
    }

    @Test
    void testPropagation() {
        Account p0 = this.createTestAccount("P0");
        Account p1 = this.createTestAccount("P1");
        Account p2 = this.createTestAccount("P2");
        Account p3 = this.createTestAccount("P3");
        Account p4 = this.createTestAccount("P4");
        Account p5 = this.createTestAccount("P5");
        Account p11 = this.createTestAccount("P11");
        Account p12 = this.createTestAccount("P12");
        Account p13 = this.createTestAccount("P13");
        Account p31 = this.createTestAccount("P31");
        Account p32 = this.createTestAccount("P32");
        Account p51 = this.createTestAccount("P51");
        Account p52 = this.createTestAccount("P52");

        LocalDateTime t0 = LocalDateTime.now().minusDays(30);

        Meeting t1 = this.createTestMeeting(p0, p1, t0.plusDays(1));
        Meeting t2 = this.createTestMeeting(p0, p2, t0.plusDays(21));
        Meeting t3 = this.createTestMeeting(p0, p3, t0.plusMinutes(30));
        Meeting t4 = this.createTestMeeting(p0, p4, t0.plusDays(15));
        Meeting t5 = this.createTestMeeting(p0, p5, t0.plusDays(2));

        Meeting t11 = this.createTestMeeting(p1, p11, t1.getMoment().plusDays(2));
        Meeting t12 = this.createTestMeeting(p1, p12, t1.getMoment().plusDays(13));
        Meeting t13 = this.createTestMeeting(p1, p13, t1.getMoment().plusDays(9));

        Meeting t110 = this.createTestMeeting(p11, p0, t11.getMoment().plusMinutes(50));

        Meeting t122 = this.createTestMeeting(p12, p2, t12.getMoment().plusDays(5));

        Meeting t132 = this.createTestMeeting(p13, p2, t13.getMoment().plusMinutes(20));

        Meeting t23 = this.createTestMeeting(p2, p3, t2.getMoment().plusMinutes(1));

        Meeting t331 = this.createTestMeeting(p3, p31, t3.getMoment().plusDays(2));
        Meeting t332 = this.createTestMeeting(p3, p32, t3.getMoment().plusDays(20));
        Meeting t34 = this.createTestMeeting(p3, p4, t3.getMoment().plusDays(18));

        Meeting t324 = this.createTestMeeting(p32, p4, t332.getMoment().plusMinutes(1));
        Meeting t352 = this.createTestMeeting(p3, p52, t3.getMoment().plusMinutes(5));

        Meeting t51 = this.createTestMeeting(p5, p51, t5.getMoment().minusDays(2));
        Meeting t52 = this.createTestMeeting(p5, p52, t5.getMoment().minusDays(2));
    }

    private Long nextMeetingId = 1L;

    private Meeting createTestMeeting(Account triggerer, Account target) {
        Double randomDaysOp = 2 * Math.random() - 1;
        Double randomHoursOp = 2 * Math.random() - 1;
        Double randomMinutesOp = 2 * Math.random() - 1;

        Integer randomDays = RandomUtils.nextInt(0, 90);
        Integer randomHours = RandomUtils.nextInt(0, 90);
        Integer randomMinutes = RandomUtils.nextInt(0, 90);

        LocalDateTime localDateTime = LocalDateTime.now();

        if (randomDaysOp >= 0.0)
            localDateTime = localDateTime.plusDays(randomDays);
        else localDateTime = localDateTime.minusDays(randomDays);

        if (randomHoursOp >= 0.0)
            localDateTime = localDateTime.plusHours(randomHours);
        else localDateTime = localDateTime.minusHours(randomHours);

        if (randomMinutesOp >= 0.0)
            localDateTime = localDateTime.plusMinutes(randomMinutes);
        else localDateTime = localDateTime.minusMinutes(randomMinutes);

        return this.createTestMeeting(triggerer, target, localDateTime);
    }

    private Meeting createTestMeeting(Account triggerer, Account target,
                                      LocalDateTime localDateTime) {
        Random random = new Random();

        Meeting meeting = new Meeting();
        meeting.setTargetAccountState(target.getAccountState());
        meeting.setTriggererAccountState(triggerer.getAccountState());
        meeting.setId(this.nextMeetingId++);
        meeting.setPosition(new GeoJsonPoint(50 * random.nextDouble(), 50 * random.nextDouble()));
        meeting.setMoment(localDateTime);
        meeting.setTriggererAccountId(triggerer.getId());
        meeting.setTargetAccountId(target.getId());

        this.meetingRepository.save(meeting);

        return meeting;
    }

    private Long nextAccount = 100L;

    private Account createTestAccount(String name) {
        Account account = new Account();
        account.setId(this.nextAccount++);
        account.setUsername(name);
        account.setPhoneNumber(UUID.randomUUID().toString());
        account.setPassword(UUID.randomUUID().toString());
        account.setAccountRole(Account.AccountRole.USER);
        account.setAccountState(Account.AccountState.HEALTHY);
        account.setCityId(1);
        account.setTownId(1);
        account.setFirstName(name);
        account.setFamillyName(name);
        account.setNumberOfMeetings(0);

        this.accountRepository.save(account);

        return account;
    }

    private Account pickRandomAccount(ArrayList<Account> accounts, Account accountToAvoid) {
        while (true) {
            int i = (int) (Math.random() * (accounts.size() - 1));

            Account account = accounts.get(i);

            if (accountToAvoid == null || !account.equals(accountToAvoid))
                return account;
        }
    }
}
