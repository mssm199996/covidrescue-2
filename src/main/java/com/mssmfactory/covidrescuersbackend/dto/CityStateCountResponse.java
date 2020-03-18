package com.mssmfactory.covidrescuersbackend.dto;

import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.domainmodel.City;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CityStateCountResponse {

    private City city;
    private Account.AccountState accountState;
    private Long count;
}
