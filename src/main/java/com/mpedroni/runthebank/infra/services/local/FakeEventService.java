package com.mpedroni.runthebank.infra.services.local;

import com.mpedroni.runthebank.infra.services.EventService;

public class FakeEventService implements EventService {

    @Override
    public void send(Object message, String target) {
        System.out.println("Sending message to " + target + ": " + message.toString());
    }
}
