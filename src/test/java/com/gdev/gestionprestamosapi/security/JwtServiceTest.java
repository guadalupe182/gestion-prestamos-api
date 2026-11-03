package com.gdev.gestionprestamosapi.security;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private final JwtService jwtService = new JwtService("0123456789abcdef0123456789abcdef", 60000);

    @Test
    void generateAndParseToken() {
        String token = jwtService.generateToken("maria");

        assertThat(jwtService.isTokenValid(token)).isTrue();
        assertThat(jwtService.extractSubject(token)).isEqualTo("maria");
        assertThat(jwtService.getExpirationSeconds()).isEqualTo(60);
    }

    @Test
    void rejectsMalformedToken() {
        assertThat(jwtService.isTokenValid("not-a-token")).isFalse();
    }
}