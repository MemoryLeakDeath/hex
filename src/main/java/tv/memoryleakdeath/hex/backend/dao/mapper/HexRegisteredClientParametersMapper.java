package tv.memoryleakdeath.hex.backend.dao.mapper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.util.StringUtils;

import tv.memoryleakdeath.hex.common.pojo.HexRegisteredClientPojo;

public class HexRegisteredClientParametersMapper {

    public HexRegisteredClientPojo getParams(RegisteredClient registeredClient) {
        HexRegisteredClientPojo pojo = new HexRegisteredClientPojo();
        pojo.setClientIdIssuedAt(
                (registeredClient.getClientIdIssuedAt() != null) ? Date.from(registeredClient.getClientIdIssuedAt())
                        : Date.from(Instant.now()));
        pojo.setClientSecretExpiresAt((registeredClient.getClientSecretExpiresAt() != null)
                ? Date.from(registeredClient.getClientSecretExpiresAt())
                : Date.from(Instant.now()));

        List<String> clientAuthenticationMethods = new ArrayList<>(
                registeredClient.getClientAuthenticationMethods().size());
        registeredClient.getClientAuthenticationMethods().forEach(
                clientAuthenticationMethod -> clientAuthenticationMethods.add(clientAuthenticationMethod.getValue()));

        List<String> authorizationGrantTypes = new ArrayList<>(registeredClient.getAuthorizationGrantTypes().size());
        registeredClient.getAuthorizationGrantTypes()
                .forEach(authorizationGrantType -> authorizationGrantTypes.add(authorizationGrantType.getValue()));
        pojo.setClientAuthenticationMethods(StringUtils.collectionToCommaDelimitedString(clientAuthenticationMethods));
        pojo.setAuthorizationGrantTypes(StringUtils.collectionToCommaDelimitedString(authorizationGrantTypes));
        pojo.setRedirectUris(StringUtils.collectionToCommaDelimitedString(registeredClient.getRedirectUris()));
        pojo.setPostLogoutRedirectUris(
                StringUtils.collectionToCommaDelimitedString(registeredClient.getPostLogoutRedirectUris()));
        pojo.setScopes(StringUtils.collectionToCommaDelimitedString(registeredClient.getScopes()));
        pojo.setId(registeredClient.getId());
        pojo.setClientId(registeredClient.getClientId());
        pojo.setClientName(registeredClient.getClientName());
        pojo.setClientSecret(registeredClient.getClientSecret());
        return pojo;
    }
}
