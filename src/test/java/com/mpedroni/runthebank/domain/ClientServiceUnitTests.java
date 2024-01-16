package com.mpedroni.runthebank.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;

public class ClientServiceUnitTests {
    private final ClientGateway clientGateway = mock(ClientGateway.class);
    private final ClientService sut = new ClientService(clientGateway);

    String aName = "John Doe";
    String aDocument = "12345678900";
    String anAddress = "Jane Doe Street, 123";
    String aPassword = "Password@1234";

    @Test
    public void createsACustomerWithTheGivenData() {
        var customer = sut.createCustomer("John Doe", "12345678900", "123 Main St", "password");

        verify(clientGateway).createCustomer(customer);
        assertThat(customer.name()).isEqualTo("John Doe");
        assertThat(customer.document()).isEqualTo("12345678900");
        assertThat(customer.address()).isEqualTo("123 Main St");
    }

    @Test
    public void throwsWhenTheGivenDocumentAlreadyExists() {
        when(clientGateway.exists(aDocument)).thenReturn(true);

        var thrown = catchThrowable(() -> sut.createCustomer(aName, aDocument, anAddress, aPassword));

        assertThat(thrown)
            .isInstanceOf(ApplicationException.class)
            .hasMessage("A customer with the given document already exists");
    }

    @Test
    public void throwsAnExceptionWhenNameIsInvalid() {
        var anInvalidName = "";

        var thrown = catchThrowable(() -> sut.createCustomer(anInvalidName, aDocument, anAddress, aPassword));

        assertThat(thrown)
            .isInstanceOf(ValidationError.class)
            .hasMessage("Name must not be empty");
    }

    @Test
    public void throwsAnExceptionWhenDocumentIsInvalid() {
        var anInvalidDocument = "1234567890";

        var thrown = catchThrowable(() -> sut.createCustomer(aName, anInvalidDocument, anAddress, aPassword));

        assertThat(thrown)
            .isInstanceOf(ValidationError.class)
            .hasMessage("Document must have 11 digits");
    }

    @Test
    public void throwsAnExceptionWhenAddressIsEmpty() {
        var anInvalidAddress = "";

        var thrown = catchThrowable(() -> sut.createCustomer(aName, aDocument, anInvalidAddress, aPassword));

        assertThat(thrown)
            .isInstanceOf(ValidationError.class)
            .hasMessage("Address must not be empty");
    }

    @Test
    public void throwsAnExceptionWhenPasswordIsEmpty() {
        var anInvalidPassword = "";

        var thrown = catchThrowable(() -> sut.createCustomer(aName, aDocument, anAddress, anInvalidPassword));

        assertThat(thrown)
            .isInstanceOf(ValidationError.class)
            .hasMessage("Password must not be empty");
    }
}
