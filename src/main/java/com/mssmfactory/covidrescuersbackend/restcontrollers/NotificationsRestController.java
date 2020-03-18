package com.mssmfactory.covidrescuersbackend.restcontrollers;

import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.dto.NotificationResponse;
import com.mssmfactory.covidrescuersbackend.services.AccountService;
import com.mssmfactory.covidrescuersbackend.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("notification")
public class NotificationsRestController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @GetMapping("findByLoggedInAccount")
    public List<NotificationResponse> findByLoggedInAccount() {
        Account loggedInAccount = this.accountService.findLoggedInAccount();

        if (loggedInAccount != null)
            return this.notificationService.findAllByAccount(loggedInAccount, this.httpServletRequest.getLocale());

        return null;
    }

    @PatchMapping("updateNotificationMarkByLoggedInAccount/{notificationId}")
    public ResponseEntity updateNotificationMarkByLoggedInAccount(
            @PathVariable("notificationId") Long notificationId,
            @RequestParam("mark") boolean marked) {
        Account account = this.accountService.findLoggedInAccount();

        if (account != null) {
            this.notificationService.updateNotificationMark(account, notificationId, marked);

            return new ResponseEntity(HttpStatus.OK);
        }

        return new ResponseEntity(HttpStatus.FORBIDDEN);
    }
}
