package com.mpedroni.runthebank.infra.services;

import java.util.UUID;

public interface NotificationService {
    void notify(UUID clientId, String message);
}
