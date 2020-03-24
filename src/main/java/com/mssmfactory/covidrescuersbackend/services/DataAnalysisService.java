package com.mssmfactory.covidrescuersbackend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.domainmodel.City;
import com.mssmfactory.covidrescuersbackend.domainmodel.Town;
import com.mssmfactory.covidrescuersbackend.dto.CityStateCountResponse;
import com.mssmfactory.covidrescuersbackend.dto.OneLineCityStateCountResponse;
import com.mssmfactory.covidrescuersbackend.dto.OneLineTownStateCountResponse;
import com.mssmfactory.covidrescuersbackend.dto.TownStateCountResponse;
import com.mssmfactory.covidrescuersbackend.exceptions.NoSuchCityException;
import com.mssmfactory.covidrescuersbackend.repositories.AccountRepository;
import com.mssmfactory.covidrescuersbackend.repositories.CityRepository;
import com.mssmfactory.covidrescuersbackend.repositories.TownRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class DataAnalysisService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private TownRepository townRepository;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MessageSource messageSource;

    public List<OneLineCityStateCountResponse> findAllOneLineCityStateCount(){
        List<City> cityList = this.cityRepository.findAll();
        List<OneLineCityStateCountResponse> result = new ArrayList<>(cityList.size());

        Account.AccountState[] accountStates = Account.AccountState.values();

        for (City city : cityList) {
            OneLineCityStateCountResponse oneLineCityStateCountResponse = new OneLineCityStateCountResponse();
            oneLineCityStateCountResponse.setCity(city);

            for (Account.AccountState accountState : accountStates) {
                Long numberOfAccounts = this.accountRepository.countAllByCityIdAndAccountState(city.getId(), accountState);

                oneLineCityStateCountResponse.getStateCountMap().put(accountState, numberOfAccounts);
            }

            result.add(oneLineCityStateCountResponse);
        }

        return result;
    }

    public List<CityStateCountResponse> findAllCityStateCount() {
        List<City> cityList = this.cityRepository.findAll();
        List<CityStateCountResponse> result = new ArrayList<>(cityList.size() * Account.AccountState.values().length);

        Account.AccountState[] accountStates = Account.AccountState.values();

        for (City city : cityList) {
            for (Account.AccountState accountState : accountStates) {
                Long numberOfAccounts = this.accountRepository.countAllByCityIdAndAccountState(city.getId(), accountState);

                CityStateCountResponse cityStateCountResponse = new CityStateCountResponse();
                cityStateCountResponse.setCity(city);
                cityStateCountResponse.setAccountState(accountState);
                cityStateCountResponse.setCount(numberOfAccounts);

                result.add(cityStateCountResponse);
            }
        }

        return result;
    }

    public List<OneLineTownStateCountResponse> findAllOneLineTownStateCountByCityId(Integer cityId) throws JsonProcessingException {
        Optional<City> cityOptional = this.cityRepository.findById(cityId);

        if (cityOptional.isPresent()) {
            City city = cityOptional.get();

            List<Town> townList = this.townRepository.findAllByCityId(cityId);
            List<OneLineTownStateCountResponse> result = new ArrayList<>(townList.size());

            Account.AccountState[] accountStates = Account.AccountState.values();

            for (Town town : townList) {
                OneLineTownStateCountResponse oneLineTownStateCountResponse = new OneLineTownStateCountResponse();
                oneLineTownStateCountResponse.setCity(city);
                oneLineTownStateCountResponse.setTown(town);

                for (Account.AccountState accountState : accountStates) {
                    Long numberOfAccounts = this.accountRepository.countAllByTownIdAndAccountState(town.getId(), accountState);

                    oneLineTownStateCountResponse.getStateCountMap().put(accountState, numberOfAccounts);
                }

                result.add(oneLineTownStateCountResponse);
            }

            return result;
        } else throw new NoSuchCityException(this.messageSource, this.httpServletRequest, this.objectMapper, cityId);
    }

    public List<TownStateCountResponse> findAllTownStateCountByCityId(Integer cityId) throws JsonProcessingException {
        Optional<City> cityOptional = this.cityRepository.findById(cityId);

        if (cityOptional.isPresent()) {
            City city = cityOptional.get();

            List<Town> townList = this.townRepository.findAllByCityId(cityId);
            List<TownStateCountResponse> result = new ArrayList<>(townList.size() * Account.AccountState.values().length);

            Account.AccountState[] accountStates = Account.AccountState.values();

            for (Town town : townList) {
                for (Account.AccountState accountState : accountStates) {
                    Long numberOfAccounts = this.accountRepository.countAllByTownIdAndAccountState(town.getId(), accountState);

                    TownStateCountResponse townStateCountResponse = new TownStateCountResponse();
                    townStateCountResponse.setCity(city);
                    townStateCountResponse.setTown(town);
                    townStateCountResponse.setAccountState(accountState);
                    townStateCountResponse.setCount(numberOfAccounts);

                    result.add(townStateCountResponse);
                }
            }

            return result;
        } else throw new NoSuchCityException(this.messageSource, this.httpServletRequest, this.objectMapper, cityId);
    }
}