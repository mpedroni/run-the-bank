package com.mpedroni.runthebank.infra.services;

public interface EventService {
    void send(Object message, String target);
}
