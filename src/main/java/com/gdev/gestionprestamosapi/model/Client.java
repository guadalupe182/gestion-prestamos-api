package com.gdev.gestionprestamosapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

/**
 * Entidad base del MVP para los clientes de la API de préstamos.
 */
@Entity
@Table(name = "clients")
@Getter
@Setter
@NoArgsConstructor
public class Client {

    public static final String TABLE_NAME = "clients";

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(nullable = false, unique = true, length = 180)
    private String email;

    public Client(String name, String email) {
        this.name = name;
        this.email = email;
    }
}