package com.gdev.gestionprestamosapi.service;

import com.gdev.gestionprestamosapi.client.AuthClient;
import com.gdev.gestionprestamosapi.dto.AuthResponse;
import com.gdev.gestionprestamosapi.dto.RegisterRequest;
import com.gdev.gestionprestamosapi.repository.AppUserRepository;
import com.gdev.gestionprestamosapi.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AppUserRepository appUserRepository;

    @Mock
    private AuthClient authClient;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @Test
    void registerRejectsInvalidLicense() {
        RegisterRequest request = new RegisterRequest("maria", "password123", "bad-license");
        when(appUserRepository.existsByUsernameIgnoreCase("maria")).thenReturn(false);
        when(authClient.isLicenseValid("bad-license")).thenReturn(false);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("400");
    }

    @Test
    void registerReturnsTokenForValidLicense() {
        RegisterRequest request = new RegisterRequest("maria", "password123", "good-license");
        when(appUserRepository.existsByUsernameIgnoreCase("maria")).thenReturn(false);
        when(authClient.isLicenseValid("good-license")).thenReturn(true);
        when(passwordEncoder.encode("password123")).thenReturn("hashed");
        when(jwtService.generateToken("maria")).thenReturn("token");
        when(jwtService.getExpirationSeconds()).thenReturn(3600L);

        AuthResponse response = authService.register(request);

        assertThat(response.token()).isEqualTo("token");
        assertThat(response.tokenType()).isEqualTo("Bearer");
    }
}