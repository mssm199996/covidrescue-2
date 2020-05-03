package com.mssmfactory.covidrescuersbackend.utils.propagation;

import java.util.Set;

public interface IPropagationHandler {

    Set<Long> propagate(Long parentAccountId);
}
