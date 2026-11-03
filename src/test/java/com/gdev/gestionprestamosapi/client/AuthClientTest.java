package com.gdev.gestionprestamosapi.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withBadRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class AuthClientTest {

    private static final String URL = "https://api.gdevsoftware.com/api/v1/licenses/validate/{key}";
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Test
    void isLicenseValid_returnsTrueWhenCrmConfirmsValidity() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        server.expect(requestTo("https://api.gdevsoftware.com/api/v1/licenses/validate/license-123"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(OBJECT_MAPPER.writeValueAsString(java.util.Map.of("valid", true)),
                        MediaType.APPLICATION_JSON));

        AuthClient authClient = new AuthClient(restTemplate, URL);

        assertThat(authClient.isLicenseValid("license-123")).isTrue();
        server.verify();
    }

    @Test
    void isLicenseValid_returnsFalseForNestedInvalidResponse() throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        server.expect(requestTo("https://api.gdevsoftware.com/api/v1/licenses/validate/license-123"))
                .andRespond(withSuccess(OBJECT_MAPPER.writeValueAsString(
                        java.util.Map.of("data", java.util.Map.of("active", false))),
                        MediaType.APPLICATION_JSON));

        AuthClient authClient = new AuthClient(restTemplate, URL);

        assertThat(authClient.isLicenseValid("license-123")).isFalse();
        server.verify();
    }

    @Test
    void isLicenseValid_returnsFalseOnHttpClientError() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        server.expect(requestTo("https://api.gdevsoftware.com/api/v1/licenses/validate/license-123"))
                .andRespond(withBadRequest());

        AuthClient authClient = new AuthClient(restTemplate, URL);

        assertThat(authClient.isLicenseValid("license-123")).isFalse();
        server.verify();
    }

    @Test
    void isLicenseValid_throwsOnServerError() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer server = MockRestServiceServer.bindTo(restTemplate).build();
        server.expect(requestTo("https://api.gdevsoftware.com/api/v1/licenses/validate/license-123"))
                .andRespond(withServerError());

        AuthClient authClient = new AuthClient(restTemplate, URL);

        assertThatThrownBy(() -> authClient.isLicenseValid("license-123"))
                .isInstanceOf(LicenseValidationException.class);
        server.verify();
    }
}