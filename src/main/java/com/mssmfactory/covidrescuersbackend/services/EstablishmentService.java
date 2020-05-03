package com.mssmfactory.covidrescuersbackend.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mssmfactory.covidrescuersbackend.domainmodel.City;
import com.mssmfactory.covidrescuersbackend.domainmodel.Establishment;
import com.mssmfactory.covidrescuersbackend.domainmodel.PendingAccountRegistration;
import com.mssmfactory.covidrescuersbackend.domainmodel.Town;
import com.mssmfactory.covidrescuersbackend.dto.EstablishmentRegistrationRequest;
import com.mssmfactory.covidrescuersbackend.dto.EstablishmentResponse;
import com.mssmfactory.covidrescuersbackend.exceptions.EstablishmentAlreadyExistsException;
import com.mssmfactory.covidrescuersbackend.exceptions.NoSuchCityException;
import com.mssmfactory.covidrescuersbackend.exceptions.NoSuchTownException;
import com.mssmfactory.covidrescuersbackend.exceptions.TownAndCityMismatchException;
import com.mssmfactory.covidrescuersbackend.repositories.CityRepository;
import com.mssmfactory.covidrescuersbackend.repositories.EstablishmentRepository;
import com.mssmfactory.covidrescuersbackend.repositories.TownRepository;
import com.mssmfactory.covidrescuersbackend.utils.communication.EmailHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class EstablishmentService {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private TownRepository townRepository;

    @Autowired
    private EstablishmentRepository establishmentRepository;

    @Autowired
    private SequenceService sequenceService;

    @Autowired
    private EmailHandler emailHandler;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private Map<Integer, City> cityMap;

    @Autowired
    private Map<Integer, Town> townMap;

    @Autowired
    private Map<Long, Establishment> establishmentMap;

    public List<EstablishmentResponse> findAll() {
        return this.fromEstablishmentsToEstablishmentsResponses(this.cityMap, this.townMap, this.establishmentRepository.findAll());
    }

    public EstablishmentResponse save(EstablishmentRegistrationRequest establishmentRegistrationRequest) throws JsonProcessingException {
        Optional<Establishment> establishmentOptional = this.establishmentRepository.findByEmail(establishmentRegistrationRequest.getEmail());

        if (!establishmentOptional.isPresent()) {
            Optional<City> cityOptional = this.cityRepository.findById(establishmentRegistrationRequest.getCityId());

            if (cityOptional.isPresent()) {
                City city = cityOptional.get();
                Optional<Town> townOptional = this.townRepository.findById(establishmentRegistrationRequest.getTownId());

                if (townOptional.isPresent()) {
                    Town town = townOptional.get();

                    if (town.getCityId().equals(city.getId())) {
                        Locale locale = this.httpServletRequest.getLocale();

                        Establishment establishment = new Establishment();
                        establishment.setAddress(establishmentRegistrationRequest.getAddress());
                        establishment.setCityId(establishmentRegistrationRequest.getCityId());
                        establishment.setName(establishmentRegistrationRequest.getName());
                        establishment.setId(this.sequenceService.getNextValue(Establishment.SEQUENCE_ID));
                        establishment.setSubscriptionDate(LocalDateTime.now());
                        establishment.setToken(UUID.randomUUID().toString());
                        establishment.setTownId(establishmentRegistrationRequest.getTownId());
                        establishment.setEmail(establishmentRegistrationRequest.getEmail());

                        this.establishmentRepository.save(establishment);
                        this.establishmentMap.put(establishment.getId(), establishment);
                        this.sequenceService.setNextValue(Establishment.SEQUENCE_ID);

                        (new Thread(() -> {
                            this.emailHandler.sendEstablishmentRegistrationDetails(establishment, city, town, locale);
                        })).start();

                        return this.fromEstablishmentToEstablishmentResponse(establishment, city, town);
                    } else
                        throw new TownAndCityMismatchException(this.messageSource, this.httpServletRequest, this.objectMapper, establishmentRegistrationRequest);
                } else
                    throw new NoSuchTownException(this.messageSource, this.httpServletRequest, this.objectMapper, establishmentRegistrationRequest);
            } else
                throw new NoSuchCityException(this.messageSource, this.httpServletRequest, this.objectMapper, establishmentRegistrationRequest);
        } else
            throw new EstablishmentAlreadyExistsException(this.messageSource, this.httpServletRequest, this.objectMapper, establishmentRegistrationRequest);
    }

    // --------------------------------------------------------------------------------------------------------

    public List<EstablishmentResponse> fromEstablishmentsToEstablishmentsResponses(Map<Integer, City> cityMap,
                                                                                   Map<Integer, Town> townMap,
                                                                                   Collection<Establishment> establishments) {

        List<EstablishmentResponse> establishmentResponseList = new ArrayList<>(establishments.size());

        for (Establishment establishment : establishments) {
            City city = cityMap.get(establishment.getCityId());
            Town town = townMap.get(establishment.getTownId());

            establishmentResponseList.add(this.fromEstablishmentToEstablishmentResponse(establishment, city, town));
        }

        return establishmentResponseList;
    }

    public EstablishmentResponse fromEstablishmentToEstablishmentResponse(Establishment establishment, City city, Town town) {
        EstablishmentResponse establishmentResponse = new EstablishmentResponse();
        establishmentResponse.setAddress(establishment.getAddress());
        establishmentResponse.setCity(city);
        establishmentResponse.setEmail(establishment.getEmail());
        establishmentResponse.setId(establishment.getId());
        establishmentResponse.setName(establishment.getName());
        establishmentResponse.setSubscriptionDate(establishment.getSubscriptionDate());
        establishmentResponse.setToken(establishment.getToken());
        establishmentResponse.setTown(town);

        return establishmentResponse;
    }
}
