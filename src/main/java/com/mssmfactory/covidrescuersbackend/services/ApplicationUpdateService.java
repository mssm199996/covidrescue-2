package com.mssmfactory.covidrescuersbackend.services;

import com.mssmfactory.covidrescuersbackend.domainmodel.ApplicationUpdate;
import com.mssmfactory.covidrescuersbackend.dto.ApplicationUpdateRequest;
import com.mssmfactory.covidrescuersbackend.repositories.ApplicationUpdateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ApplicationUpdateService {

    @Autowired
    private ApplicationUpdateRepository applicationUpdateRepository;

    @Autowired
    private SequenceService sequenceService;

    public ApplicationUpdate save(ApplicationUpdateRequest applicationUpdateRequest) {
        ApplicationUpdate applicationUpdate = new ApplicationUpdate();
        applicationUpdate.setDescription(applicationUpdateRequest.getDescription());
        applicationUpdate.setId(this.sequenceService.getNextValue(ApplicationUpdate.SEQUENCE_ID));
        applicationUpdate.setLastVersion(true);
        applicationUpdate.setReleaseDateTime(LocalDateTime.now());
        applicationUpdate.setUrl(applicationUpdateRequest.getUrl());
        applicationUpdate.setVersion(applicationUpdateRequest.getVersion());

        this.deprecateAllApplicationUpdates();
        this.applicationUpdateRepository.save(applicationUpdate);
        this.sequenceService.setNextValue(ApplicationUpdate.SEQUENCE_ID);

        return applicationUpdate;
    }

    private void deprecateAllApplicationUpdates() {
        List<ApplicationUpdate> applicationUpdates = this.applicationUpdateRepository.findAll();
        applicationUpdates.forEach(applicationUpdate -> applicationUpdate.setLastVersion(false));

        this.applicationUpdateRepository.saveAll(applicationUpdates);
    }
}
