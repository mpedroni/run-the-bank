package com.mpedroni.runthebank.domain.client;

import java.util.UUID;

public record Client(UUID id, String name, String document, String address, String password, ClientType type) {

}
