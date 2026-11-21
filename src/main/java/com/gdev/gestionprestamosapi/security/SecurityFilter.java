package com.gdev.gestionprestamosapi.security;

import com.gdev.gestionprestamosapi.client.AuthClient;
import com.gdev.gestionprestamosapi.model.AppUser;
import com.gdev.gestionprestamosapi.repository.AppUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SecurityFilter extends OncePerRequestFilter {

    private static final long LICENSE_CACHE_TTL_MS = 300_000L;

    private final JwtService jwtService;
    private final AppUserRepository appUserRepository;
    private final AuthClient authClient;
    private final Map<String, LicenseCacheEntry> licenseCache = new ConcurrentHashMap<>();

    public SecurityFilter(JwtService jwtService, AppUserRepository appUserRepository, AuthClient authClient) {
        this.jwtService = jwtService;
        this.appUserRepository = appUserRepository;
        this.authClient = authClient;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring("Bearer ".length()).trim();
        if (token.isBlank() || !jwtService.isTokenValid(token)) {
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
            return;
        }

        String username = jwtService.extractSubject(token);
        if (username == null || appUserRepository.findByUsernameIgnoreCase(username).isEmpty()) {
            SecurityContextHolder.clearContext();
            filterChain.doFilter(request, response);
            return;
        }

        AppUser user = appUserRepository.findByUsernameIgnoreCase(username).orElse(null);
        if (user == null || !isLicenseActive(user)) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "La licencia de GDEV no está activa");
            return;
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                username,
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        filterChain.doFilter(request, response);
    }

    private boolean isLicenseActive(AppUser user) {
        long now = System.currentTimeMillis();
        LicenseCacheEntry cached = licenseCache.get(user.getUsername());
        if (cached != null && cached.expiresAt() > now) {
            return cached.valid();
        }

        boolean valid;
        try {
            valid = authClient.isLicenseValid(user.getLicenseKey());
        } catch (RuntimeException e) {
            valid = false;
        }
        licenseCache.put(user.getUsername(), new LicenseCacheEntry(valid, now + LICENSE_CACHE_TTL_MS));
        return valid;
    }

    private record LicenseCacheEntry(boolean valid, long expiresAt) {
    }
}