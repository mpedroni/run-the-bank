package com.mpedroni.runthebank.infra.client.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.mpedroni.runthebank.infra.client.persistence.ClientRepository;
import com.mpedroni.runthebank.infra.client.persistence.ClientTypeJpa;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class CompanyE2ETest {
    @Autowired
    MockMvc mvc;

    @Autowired
    ClientRepository clientRepository;

    @BeforeEach
    void cleanDatabase() {
        clientRepository.deleteAll();
    }

    @Test
    void createsACompanyWithTheGivenData() throws Exception {
        // given
        var aName = "John Doe Inc.";
        var aDocument = "12345678901234";
        var anAddress = "Cidade de Pallet";
        var aPassword = "Password@1234";

        var content = """
            {
              "name": "%s",
              "document": "%s",
              "address": "%s",
              "password": "%s"
            }
            """.formatted(aName, aDocument, anAddress, aPassword);

        // when
        mvc.perform(post("/companies")
                .contentType("application/json")
                .content(content))
            .andExpect(status().isCreated());

        // then
        var companies = clientRepository.findAll();
        assertThat(companies).hasSize(1);

        var company = companies.getFirst();
        assertThat(company.getId()).isNotNull();
        assertThat(company.getName()).isEqualTo(aName);
        assertThat(company.getDocument()).isEqualTo(aDocument);
        assertThat(company.getAddress()).isEqualTo(anAddress);
        assertThat(company.getType()).isEqualTo(ClientTypeJpa.COMPANY);
    }
}
