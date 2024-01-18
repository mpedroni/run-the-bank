package com.mpedroni.runthebank.infra;

import com.mpedroni.runthebank.domain.ClientService;
import java.net.URI;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/companies")
public class CompanyController {
    private final ClientService clientService;

    public CompanyController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateClientRequest request) {
        var company = clientService.createCompany(
            request.name(),
            request.document(),
            request.address(),
            request.password()
        );

        return ResponseEntity.created(URI.create(company.id().toString())).build();
    }
}
