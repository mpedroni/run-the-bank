package com.mpedroni.runthebank.infra;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "clients")
public class ClientJpaEntity {

    @Id
    private UUID id;

    private String name;
    private String document;
    private String address;
    private String password;
    private ClientTypeJpa type;

    public ClientJpaEntity() {
    }

    public ClientJpaEntity(UUID id, String name, String document, String address, String password, ClientTypeJpa type) {
        this.id = id;
        this.name = name;
        this.document = document;
        this.address = address;
        this.password = password;
        this.type = type;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public ClientTypeJpa getType() {
        return type;
    }

    public void setType(ClientTypeJpa type) {
        this.type = type;
    }
}
