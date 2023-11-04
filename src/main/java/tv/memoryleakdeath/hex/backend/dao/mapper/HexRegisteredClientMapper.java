package tv.memoryleakdeath.hex.backend.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Duration;
import java.util.Set;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.util.StringUtils;

public class HexRegisteredClientMapper implements RowMapper<RegisteredClient> {

    @Override
    public RegisteredClient mapRow(ResultSet rs, int rowNum) throws SQLException {
        Timestamp clientIdIssuedAt = rs.getTimestamp("client_id_issued_at");
        Timestamp clientSecretExpiresAt = rs.getTimestamp("client_secret_expires_at");
        Set<String> clientAuthenticationMethods = StringUtils
                .commaDelimitedListToSet(rs.getString("client_authentication_methods"));
        Set<String> authorizationGrantTypes = StringUtils
                .commaDelimitedListToSet(rs.getString("authorization_grant_types"));
        Set<String> redirectUris = StringUtils.commaDelimitedListToSet(rs.getString("redirect_uris"));
        Set<String> postLogoutRedirectUris = StringUtils
                .commaDelimitedListToSet(rs.getString("post_logout_redirect_uris"));
        Set<String> clientScopes = StringUtils.commaDelimitedListToSet(rs.getString("scopes"));

        // @formatter:off
        RegisteredClient.Builder builder = RegisteredClient.withId(rs.getString("id"))
                .clientId(rs.getString("client_id"))
                .clientIdIssuedAt(clientIdIssuedAt != null ? clientIdIssuedAt.toInstant() : null)
                .clientSecret(rs.getString("client_secret"))
                .clientSecretExpiresAt(clientSecretExpiresAt != null ? clientSecretExpiresAt.toInstant() : null)
                .clientName(rs.getString("client_name"))
                .clientAuthenticationMethods((authenticationMethods) ->
                        clientAuthenticationMethods.forEach(authenticationMethod ->
                                authenticationMethods.add(resolveClientAuthenticationMethod(authenticationMethod))))
                .authorizationGrantTypes((grantTypes) ->
                        authorizationGrantTypes.forEach(grantType ->
                                grantTypes.add(resolveAuthorizationGrantType(grantType))))
                .redirectUris((uris) -> uris.addAll(redirectUris))
                .postLogoutRedirectUris((uris) -> uris.addAll(postLogoutRedirectUris))
                .scopes((scopes) -> scopes.addAll(clientScopes));
        // @formatter:on

        builder.clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build());

        TokenSettings.Builder tokenSettingsBuilder = TokenSettings.builder()
                .accessTokenFormat(OAuth2TokenFormat.SELF_CONTAINED).accessTokenTimeToLive(Duration.ofDays(30))
                .authorizationCodeTimeToLive(Duration.ofMinutes(10)).refreshTokenTimeToLive(Duration.ofDays(30));
        builder.tokenSettings(tokenSettingsBuilder.build());

        return builder.build();
    }

    private static AuthorizationGrantType resolveAuthorizationGrantType(String authorizationGrantType) {
        if (AuthorizationGrantType.AUTHORIZATION_CODE.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.AUTHORIZATION_CODE;
        } else if (AuthorizationGrantType.REFRESH_TOKEN.getValue().equals(authorizationGrantType)) {
            return AuthorizationGrantType.REFRESH_TOKEN;
        }
        return null;
    }

    private static ClientAuthenticationMethod resolveClientAuthenticationMethod(String clientAuthenticationMethod) {
        if (ClientAuthenticationMethod.CLIENT_SECRET_BASIC.getValue().equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.CLIENT_SECRET_BASIC;
        } else if (ClientAuthenticationMethod.CLIENT_SECRET_POST.getValue().equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.CLIENT_SECRET_POST;
        } else if (ClientAuthenticationMethod.CLIENT_SECRET_JWT.getValue().equals(clientAuthenticationMethod)) {
            return ClientAuthenticationMethod.CLIENT_SECRET_JWT;
        }
        return null;
    }
}
