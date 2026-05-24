package com.dogac.user_service.infrastructure.keycloack;

import java.util.List;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import jakarta.ws.rs.core.Response;

@Component
public class KeycloakUserProviderImpl implements KeycloakUserProvider {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    public KeycloakUserProviderImpl(Keycloak keycloak) {
        this.keycloak = keycloak;
    }

    @Override
    public String createUser(String email, String password, String firstName, String lastName) {

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(password);
        credential.setTemporary(false);

        UserRepresentation user = new UserRepresentation();
        user.setUsername(email);
        user.setEmail(email);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEnabled(true);
        user.setEmailVerified(true);
        user.setCredentials(List.of(credential));

        Response response = keycloak.realm(realm)
                .users()
                .create(user);

        if (response.getStatus() != 201) {
            String error = response.readEntity(String.class);
            response.close();
            throw new RuntimeException("Keycloak user create failed. Status: "
                    + response.getStatus() + " Body: " + error);
        }

        String location = response.getHeaderString("Location");
        response.close();

        return location.substring(location.lastIndexOf("/") + 1);
    }
}