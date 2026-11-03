package com.gdev.gestionprestamosapi.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gdev.gestionprestamosapi.client.AuthClient;
import com.gdev.gestionprestamosapi.dto.RegisterRequest;
import com.gdev.gestionprestamosapi.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "jwt.secret=0123456789abcdef0123456789abcdef",
        "jwt.expiration-ms=60000"
})
@AutoConfigureMockMvc
class SecurityIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private AppUserRepository appUserRepository;

    @MockBean
    private AuthClient authClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void cleanDatabase() {
        appUserRepository.deleteAll();
    }

    @Test
    void protectedLoanEndpointRequiresJwt() throws Exception {
        mockMvc.perform(get("/api/v1/prestamos/activos"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registeredUserCanAccessProtectedLoanEndpoint() throws Exception {
        when(authClient.isLicenseValid("valid-license")).thenReturn(true);

        RegisterRequest request = new RegisterRequest("maria", "password123", "valid-license");
        String response = mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(response).get("token").asText();

        mockMvc.perform(get("/api/v1/prestamos/activos")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void registrationRejectsInvalidLicense() throws Exception {
        when(authClient.isLicenseValid("invalid-license")).thenReturn(false);

        RegisterRequest request = new RegisterRequest("pedro", "password123", "invalid-license");
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}