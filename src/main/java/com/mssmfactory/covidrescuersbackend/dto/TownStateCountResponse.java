package com.mssmfactory.covidrescuersbackend.dto;

import com.mssmfactory.covidrescuersbackend.domainmodel.Account;
import com.mssmfactory.covidrescuersbackend.domainmodel.City;
import com.mssmfactory.covidrescuersbackend.domainmodel.Town;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TownStateCountResponse {

    private City city;
    private Town town;
    private Account.AccountState accountState;
    private Long count;
}
