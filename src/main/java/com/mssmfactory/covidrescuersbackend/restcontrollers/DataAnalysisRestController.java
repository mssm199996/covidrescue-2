package com.mssmfactory.covidrescuersbackend.restcontrollers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.mssmfactory.covidrescuersbackend.dto.CityStateCountResponse;
import com.mssmfactory.covidrescuersbackend.dto.OneLineCityStateCountResponse;
import com.mssmfactory.covidrescuersbackend.dto.OneLineTownStateCountResponse;
import com.mssmfactory.covidrescuersbackend.dto.TownStateCountResponse;
import com.mssmfactory.covidrescuersbackend.services.DataAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("analysis")
public class DataAnalysisRestController {

    @Autowired
    private DataAnalysisService dataAnalysisService;

    @GetMapping("findAllCityStateCount")
    public List<CityStateCountResponse> findAllCityStateCount() {
        return this.dataAnalysisService.findAllCityStateCount();
    }

    @GetMapping("findAllOneLineCityStateCount")
    public List<OneLineCityStateCountResponse> findAllOneLineCityStateCount() {
        return this.dataAnalysisService.findAllOneLineCityStateCount();
    }

    @GetMapping("findAllTownStateCountByCityId/{cityId}")
    public List<TownStateCountResponse> findAllTownStateCountByCityId(@PathVariable("cityId") Integer cityId) throws JsonProcessingException {
        return this.dataAnalysisService.findAllTownStateCountByCityId(cityId);
    }

    @GetMapping("findAllOneLineTownStateCountByCityId/{cityId}")
    public List<OneLineTownStateCountResponse> findAllOneLineTownStateCountByCityId(@PathVariable("cityId") Integer cityId) throws JsonProcessingException {
        return this.dataAnalysisService.findAllOneLineTownStateCountByCityId(cityId);
    }
}
