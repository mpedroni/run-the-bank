package com.mpedroni.runthebank.infra.client.persistence;

import com.mpedroni.runthebank.domain.client.ClientType;

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
