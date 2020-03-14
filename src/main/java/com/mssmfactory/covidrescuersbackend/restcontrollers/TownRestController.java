package com.mssmfactory.covidrescuersbackend.restcontrollers;

import com.mssmfactory.covidrescuersbackend.domainmodel.Town;
import com.mssmfactory.covidrescuersbackend.repositories.TownRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("town")
public class TownRestController {

    @Autowired
    private TownRepository townRepository;

    @GetMapping("findAll")
    public List<Town> findAll() {
        return this.townRepository.findAll();
    }
}
