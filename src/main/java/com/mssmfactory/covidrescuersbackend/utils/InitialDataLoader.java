package com.mssmfactory.covidrescuersbackend.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mssmfactory.covidrescuersbackend.domainmodel.City;
import com.mssmfactory.covidrescuersbackend.domainmodel.Town;
import com.mssmfactory.covidrescuersbackend.repositories.CityRepository;
import com.mssmfactory.covidrescuersbackend.repositories.TownRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class InitialDataLoader {

    @Value("${mssm.refreshInitialData}")
    private boolean refreshInitialData;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private TownRepository townRepository;

    @PostConstruct
    public void initInitialData() throws IOException {
        if (this.refreshInitialData) {
            InputStream citiesInputStream = new FileInputStream(ResourceUtils.getFile(
                    "classpath:static/cities.json"));

            InputStream townsInputStream = new FileInputStream(ResourceUtils.getFile(
                    "classpath:static/towns.json"));

            try {
                List<City> cities = this.objectMapper.readValue(citiesInputStream, new TypeReference<>() {
                });

                List<Town> towns = this.objectMapper.readValue(townsInputStream, new TypeReference<>() {
                });

                this.cityRepository.saveAll(cities);
                this.townRepository.saveAll(towns);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
