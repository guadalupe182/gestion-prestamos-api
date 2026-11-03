package com.gdev.gestionprestamosapi.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Cliente de integración para validar licencias contra el CRM de GDEV.
 */
@Component
public class AuthClient {

    private static final Logger log = LoggerFactory.getLogger(AuthClient.class);

    private final RestTemplate restTemplate;
    private final String licenseValidationUrl;

    public AuthClient(RestTemplate restTemplate,
                      @Value("${gdev.crm.license-validation-url}") String licenseValidationUrl) {
        this.restTemplate = restTemplate;
        this.licenseValidationUrl = licenseValidationUrl;
    }

    /**
     * Valida una licencia en el CRM de GDEV.
     *
     * @return true únicamente cuando el CRM confirma explícitamente que la licencia es válida.
     */
    public boolean isLicenseValid(String licenseKey) {
        return validateLicense(licenseKey);
    }

    /**
     * Alias compatible con implementaciones que nombran la validación de otra forma.
     */
    public boolean validateLicense(String licenseKey) {
        if (licenseKey == null || licenseKey.isBlank()) {
            return false;
        }

        try {
            ResponseEntity<JsonNode> response = restTemplate.getForEntity(
                    licenseValidationUrl,
                    JsonNode.class,
                    licenseKey
            );

            JsonNode body = response.getBody();
            if (!response.getStatusCode().is2xxSuccessful() || body == null) {
                return false;
            }

            boolean valid = extractValidation(body);
            log.debug("Validación de licencia {} devolvió {}", mask(licenseKey), valid);
            return valid;
        } catch (HttpClientErrorException e) {
            log.warn("El CRM rechazó la licencia con estado HTTP {}", e.getStatusCode().value());
            return false;
        } catch (RestClientException e) {
            log.error("No fue posible validar la licencia en el CRM de GDEV", e);
            throw new LicenseValidationException("No fue posible validar la licencia en el CRM de GDEV", e);
        }
    }

    private boolean extractValidation(JsonNode node) {
        if (node == null || node.isNull()) {
            return false;
        }

        if (node.isBoolean()) {
            return node.asBoolean();
        }

        if (node.isTextual()) {
            String value = node.asText().trim().toLowerCase();
            return "true".equals(value) || "valid".equals(value) || "active".equals(value);
        }

        if (node.isObject()) {
            JsonNode data = node.has("data") ? node.get("data") : node;
            for (String field : new String[]{"valid", "isValid", "licenseValid", "isLicenseValid", "active", "isActive"}) {
                if (data.has(field)) {
                    return extractValidation(data.get(field));
                }
            }

            for (String field : new String[]{"status", "licenseStatus", "state"}) {
                if (data.has(field)) {
                    String value = data.get(field).asText("").trim().toLowerCase();
                    return "valid".equals(value) || "active".equals(value) || "approved".equals(value);
                }
            }
        }

        return false;
    }

    private String mask(String value) {
        if (value.length() <= 4) {
            return "****";
        }
        return "****" + value.substring(value.length() - 4);
    }
}