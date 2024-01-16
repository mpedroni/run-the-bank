package com.mpedroni.runthebank.infra;

import com.mpedroni.runthebank.domain.ClientType;

public enum ClientTypeJpa {
    CUSTOMER,
    COMPANY;

    public static ClientTypeJpa fromDomain(ClientType type) {
        return switch (type) {
            case CUSTOMER -> CUSTOMER;
            case COMPANY -> COMPANY;
        };
    }
}
