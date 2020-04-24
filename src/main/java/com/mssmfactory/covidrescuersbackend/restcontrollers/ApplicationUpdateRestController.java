package com.mssmfactory.covidrescuersbackend.restcontrollers;

import com.mssmfactory.covidrescuersbackend.domainmodel.ApplicationUpdate;
import com.mssmfactory.covidrescuersbackend.dto.ApplicationUpdateRequest;
import com.mssmfactory.covidrescuersbackend.repositories.ApplicationUpdateRepository;
import com.mssmfactory.covidrescuersbackend.services.ApplicationUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("applicationUpdate")
public class ApplicationUpdateRestController {

    @Autowired
    private ApplicationUpdateRepository applicationUpdateRepository;

    @Autowired
    private ApplicationUpdateService applicationUpdateService;

    @GetMapping("findAll")
    public List<ApplicationUpdate> findAll() {
        return this.applicationUpdateRepository.findAll();
    }

    @GetMapping("findByLastVersion")
    public ApplicationUpdate findByLastVersion() {
        Optional<ApplicationUpdate> applicationUpdateOptional = this.applicationUpdateRepository.findFirstByLastVersion(true);

        if (applicationUpdateOptional.isPresent())
            return applicationUpdateOptional.get();

        else return null;
    }

    @PostMapping
    public ApplicationUpdate save(@Valid @RequestBody ApplicationUpdateRequest applicationUpdateRequest) {
        return this.applicationUpdateService.save(applicationUpdateRequest);
    }

    @DeleteMapping("deleteById")
    public void deleteById(@RequestParam("applicationUpdateId") Integer applicationUpdateId) {
        this.applicationUpdateRepository.deleteById(applicationUpdateId);
    }
}
