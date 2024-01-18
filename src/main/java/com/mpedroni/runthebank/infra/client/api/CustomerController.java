package com.mpedroni.runthebank.infra.client.api;

import com.mpedroni.runthebank.domain.client.ClientService;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
public class CustomerController {
    private final ClientService clientService;

    public CustomerController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateClientRequest request) {
        var customer = clientService.createCustomer(
            request.name(),
            request.document(),
            request.address(),
            request.password()
        );

        return ResponseEntity.created(URI.create(customer.id().toString())).build();
    }
}
