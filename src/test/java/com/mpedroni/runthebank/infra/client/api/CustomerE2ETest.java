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
public class CustomerE2ETest {
    @Autowired
    MockMvc mvc;

    @Autowired
    ClientRepository clientRepository;

    @BeforeEach
    void cleanDatabase() {
        clientRepository.deleteAll();
    }

    @Test
    void createsACustomerWithTheGivenData() throws Exception {
        // given
        var aName = "John Doe";
        var aDocument = "12365478902";
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
        mvc.perform(post("/customers")
            .contentType("application/json")
            .content(content))
            .andExpect(status().isCreated());

        // then
        var customers = clientRepository.findAll();
        assertThat(customers).hasSize(1);

        var customer = customers.getFirst();
        assertThat(customer.getId()).isNotNull();
        assertThat(customer.getName()).isEqualTo(aName);
        assertThat(customer.getDocument()).isEqualTo(aDocument);
        assertThat(customer.getAddress()).isEqualTo(anAddress);
        assertThat(customer.getType()).isEqualTo(ClientTypeJpa.CUSTOMER);
    }
}
