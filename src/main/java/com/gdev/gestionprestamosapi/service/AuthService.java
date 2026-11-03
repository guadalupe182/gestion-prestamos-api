package com.gdev.gestionprestamosapi.service;

import com.gdev.gestionprestamosapi.client.AuthClient;
import com.gdev.gestionprestamosapi.dto.AuthResponse;
import com.gdev.gestionprestamosapi.dto.LoginRequest;
import com.gdev.gestionprestamosapi.dto.RegisterRequest;
import com.gdev.gestionprestamosapi.model.AppUser;
import com.gdev.gestionprestamosapi.repository.AppUserRepository;
import com.gdev.gestionprestamosapi.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class AuthService {

    private final AppUserRepository appUserRepository;
    private final AuthClient authClient;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(AppUserRepository appUserRepository,
                       AuthClient authClient,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.appUserRepository = appUserRepository;
        this.authClient = authClient;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String username = request.username().trim();
        if (appUserRepository.existsByUsernameIgnoreCase(username)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El nombre de usuario ya está registrado");
        }

        boolean licenseValid;
        try {
            licenseValid = authClient.isLicenseValid(request.licenseKey());
        } catch (RuntimeException e) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "No fue posible validar la licencia en el CRM de GDEV", e);
        }

        if (!licenseValid) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La licenseKey de GDEV no es válida");
        }

        AppUser user = new AppUser(
                username,
                passwordEncoder.encode(request.password()),
                request.licenseKey()
        );
        appUserRepository.save(user);

        return new AuthResponse(jwtService.generateToken(user.getUsername()), "Bearer",
                jwtService.getExpirationSeconds());
    }

    public AuthResponse login(LoginRequest request) {
        String username = request.username().trim();
        AppUser user = appUserRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        return new AuthResponse(jwtService.generateToken(user.getUsername()), "Bearer",
                jwtService.getExpirationSeconds());
    }
}