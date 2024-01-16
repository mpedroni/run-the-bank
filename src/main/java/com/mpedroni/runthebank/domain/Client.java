package com.mpedroni.runthebank.domain;

import java.util.UUID;

public record Client(UUID id, String name, String document, String address, String password, ClientType type) {

}
