package com.mpedroni.runthebank.infra;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

    @Test
    void shouldCreateACustomerWithTheGivenData() throws Exception {
        mvc.perform(post("/customers")
            .contentType("application/json")
            .content("""
                {
                  "name": "John Doe",
                  "document": 12365478902,
                  "address": "Cidade de Pallet",
                  "password": "Password@1234"
                }
                """))
            .andExpect(status().isCreated());

        var customer = clientRepository.findAll().stream().findFirst().orElse(null);

        assertThat(customer).isNotNull();
        assertThat(customer.getId()).isNotNull();
        assertThat(customer.getName()).isEqualTo("John Doe");
        assertThat(customer.getDocument()).isEqualTo("12365478902");
        assertThat(customer.getAddress()).isEqualTo("Cidade de Pallet");
        assertThat(customer.getType()).isEqualTo(ClientTypeJpa.CUSTOMER);
    }
}
