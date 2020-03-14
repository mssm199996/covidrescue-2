package com.mssmfactory.covidrescuersbackend.services;

import com.mssmfactory.covidrescuersbackend.domainmodel.Sequence;
import com.mssmfactory.covidrescuersbackend.repositories.SequenceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class SequenceService {

    @Autowired
    private SequenceRepository sequenceRepository;

    public Long getNextValue(String sequenceId) {
        Optional<Sequence> sequenceOptional = this.sequenceRepository.findById(sequenceId);

        if (sequenceOptional.isPresent())
            return sequenceOptional.get().getNextValue();
        else {
            Sequence sequence = new Sequence();
            sequence.setId(sequenceId);
            sequence.setNextValue(1L);

            this.sequenceRepository.save(sequence);

            return 1L;
        }
    }

    public void setNextValue(String sequenceId) {
        Optional<Sequence> sequenceOptional = this.sequenceRepository.findById(sequenceId);

        if (sequenceOptional.isPresent()) {
            Sequence sequence = sequenceOptional.get();
            sequence.setNextValue(sequence.getNextValue() + 1);

            this.sequenceRepository.save(sequence);
        }
    }
}
