package com.mpedroni.runthebank.infra;

import com.mpedroni.runthebank.domain.ClientService;
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
    public ResponseEntity<?> create(@RequestBody CreateCustomerRequest request) {
        clientService.createCustomer(
            request.name(),
            request.document(),
            request.address(),
            request.password()
        );

        return ResponseEntity.created(null).build();
    }
}
