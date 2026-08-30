package com.boutique.user.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

/**
 * Resolves user attributes from Cognito's OIDC userInfo endpoint using the
 * already-validated OAuth access token. Cognito access JWTs are authorization
 * tokens and are not required to carry the email claim themselves.
 */
@Component
public class CognitoUserInfoClient {

    private final RestClient restClient;
    private final String userInfoUri;

    public CognitoUserInfoClient(@Value("${cognito.user-info-uri:}") String userInfoUri) {
        this.userInfoUri = userInfoUri == null ? "" : userInfoUri.trim();
        this.restClient = RestClient.create();
    }

    public String verifiedEmail(String accessToken) {
        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("Cognito access token is required.");
        }
        if (userInfoUri.isBlank()) {
            throw new IllegalStateException("COGNITO_USERINFO_URI is not configured.");
        }

        @SuppressWarnings("unchecked")
        Map<String, Object> body = restClient.get()
                .uri(userInfoUri)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken)
                .retrieve()
                .body(Map.class);

        Object email = body == null ? null : body.get("email");
        if (email == null || email.toString().isBlank()) {
            throw new IllegalArgumentException("Verified Cognito email is unavailable.");
        }
        return email.toString().trim();
    }
}
