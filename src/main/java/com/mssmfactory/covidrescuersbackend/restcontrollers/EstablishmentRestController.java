package com.mssmfactory.covidrescuersbackend.restcontrollers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mssmfactory.covidrescuersbackend.dto.EstablishmentRegistrationRequest;
import com.mssmfactory.covidrescuersbackend.dto.EstablishmentResponse;
import com.mssmfactory.covidrescuersbackend.repositories.EstablishmentRepository;
import com.mssmfactory.covidrescuersbackend.services.EstablishmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("establishment")
public class EstablishmentRestController {

    @Autowired
    private EstablishmentService establishmentService;

    @Autowired
    private EstablishmentRepository establishmentRepository;

    // -------------------------------------------------------------------------------------------------------

    @GetMapping("findAll")
    public List<EstablishmentResponse> findAll() {
        return this.establishmentService.findAll();
    }

    // -------------------------------------------------------------------------------------------------------

    @PostMapping
    public EstablishmentResponse save(@RequestBody @Valid EstablishmentRegistrationRequest establishmentRegistrationRequest) throws JsonProcessingException {
        return this.establishmentService.save(establishmentRegistrationRequest);
    }

    // -------------------------------------------------------------------------------------------------------

    @DeleteMapping("deleteAll")
    public void deleteAll() {
        this.establishmentRepository.deleteAll();
    }

    @DeleteMapping("deleteByEmail")
    public void deleteByEmail(@RequestParam("email") String email) {
        this.establishmentRepository.deleteByEmail(email);
    }
}
