package com.mssmfactory.covidrescuersbackend;

import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.domainmodel.Meeting;
import com.mssmfactory.covidrescuersbackend.repositories.AccountRepository;
import com.mssmfactory.covidrescuersbackend.repositories.MeetingRepository;
import com.mssmfactory.covidrescuersbackend.services.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

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
        account.setFirstName("FIR: [" + name + "]");
        account.setFamillyName("FAM: [" + name + "]");
        account.setNumberOfMeetings(0);

        this.accountRepository.save(account);

        return account;
    }
}
