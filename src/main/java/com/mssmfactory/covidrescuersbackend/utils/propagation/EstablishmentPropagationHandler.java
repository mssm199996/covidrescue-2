package com.mssmfactory.covidrescuersbackend.utils.propagation;

import com.mssmfactory.covidrescuersbackend.domainmodel.AccountEstablishmentEvent;
import com.mssmfactory.covidrescuersbackend.repositories.AccountEstablishmentEventRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class EstablishmentPropagationHandler implements IPropagationHandler {

    @Autowired
    private AccountEstablishmentEventRepository accountEstablishmentEventRepository;

    @Override
    public Set<Long> propagate(Long parentAccountId) {
        /* First extract for each Establishment when the parent entered and when he went out
         * For each entry:
         *  - Search all events
         *  - Join event by pair (start, end)
         * - For each pair p:
         *     - Search all events that happened in the same establishment as p and which are
         *         overlapped in p (moment inside p.interval)
         *     - For each event, add the current person to the result (this person need to be suspected)
         */

        Set<Long> result = new HashSet<>();

        List<AccountEstablishmentEvent> accountEstablishmentEvents = this.accountEstablishmentEventRepository.findAllByAccountId(parentAccountId);
        List<AccountEstablishmentEventPair> accountEstablishmentEventPairs = new ArrayList<>(accountEstablishmentEvents.size() / 2 + 1);

        Iterator<AccountEstablishmentEvent> accountEstablishmentEventIterator = accountEstablishmentEvents.iterator();

        while (accountEstablishmentEventIterator.hasNext()) {
            AccountEstablishmentEvent input = accountEstablishmentEventIterator.next();

            if (accountEstablishmentEventIterator.hasNext()) {
                AccountEstablishmentEvent output = accountEstablishmentEventIterator.next();

                AccountEstablishmentEventPair accountEstablishmentEventPair = new AccountEstablishmentEventPair();
                accountEstablishmentEventPair.setInput(input);
                accountEstablishmentEventPair.setOutput(output);

                accountEstablishmentEventPairs.add(accountEstablishmentEventPair);
            }
        }

        // ------------------------------------------------------------------------------------------

        for (AccountEstablishmentEventPair accountEstablishmentEventPair : accountEstablishmentEventPairs) {
            Long establishmentId = accountEstablishmentEventPair.getInput().getEstablishmentId();
            LocalDateTime inputLocalDateTime = accountEstablishmentEventPair.getInput().getMoment();
            LocalDateTime outputLocalDateTime = accountEstablishmentEventPair.getOutput().getMoment();

            List<AccountEstablishmentEvent> neighborAccountEstablishmentEvents = this.accountEstablishmentEventRepository.findAllByEstablishmentIdAndMomentBetween(
                    establishmentId, inputLocalDateTime, outputLocalDateTime);

            neighborAccountEstablishmentEvents.forEach(e -> result.add(e.getAccountId()));
        }

        return result;
    }

    @Getter
    @Setter
    @ToString
    public static class AccountEstablishmentEventPair {

        private AccountEstablishmentEvent input;
        private AccountEstablishmentEvent output;
    }
}
