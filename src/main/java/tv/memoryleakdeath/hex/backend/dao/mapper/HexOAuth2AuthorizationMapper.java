package tv.memoryleakdeath.hex.backend.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2UserCode;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

public class HexOAuth2AuthorizationMapper implements RowMapper<OAuth2Authorization> {
    private static final Logger logger = LoggerFactory.getLogger(HexOAuth2AuthorizationMapper.class);
    private ObjectMapper objectMapper = new ObjectMapper();
    private RegisteredClientRepository registeredClientRepository;

    public HexOAuth2AuthorizationMapper(RegisteredClientRepository registeredClientRepository) {
        this.registeredClientRepository = registeredClientRepository;

        ClassLoader classLoader = HexOAuth2AuthorizationMapper.class.getClassLoader();
        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
        this.objectMapper.registerModules(securityModules);
        this.objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public OAuth2Authorization mapRow(ResultSet rs, int rowNum) throws SQLException {
        String registeredClientId = rs.getString("registered_client_id");
        RegisteredClient registeredClient = registeredClientRepository.findById(registeredClientId);
        if (registeredClient == null) {
            throw new DataRetrievalFailureException("The RegisteredClient with id '" + registeredClientId
                    + "' was not found in the RegisteredClientRepository.");
        }

        OAuth2Authorization.Builder builder = OAuth2Authorization.withRegisteredClient(registeredClient);
        String id = rs.getString("id");
        String principalName = rs.getString("principal_name");
        String authorizationGrantType = rs.getString("authorization_grant_type");
        Set<String> authorizedScopes = Collections.emptySet();
        String authorizedScopesString = rs.getString("authorized_scopes");
        if (authorizedScopesString != null) {
            authorizedScopes = StringUtils.commaDelimitedListToSet(authorizedScopesString);
        }
        Map<String, Object> attributes = parseMap(rs.getString("attributes"));

        builder.id(id).principalName(principalName)
                .authorizationGrantType(new AuthorizationGrantType(authorizationGrantType))
                .authorizedScopes(authorizedScopes).attributes((attrs) -> attrs.putAll(attributes));

        String state = rs.getString("state");
        if (StringUtils.hasText(state)) {
            builder.attribute(OAuth2ParameterNames.STATE, state);
        }

        Instant tokenIssuedAt;
        Instant tokenExpiresAt;
        String authorizationCodeValue = rs.getString("authorization_code_value");

        if (StringUtils.hasText(authorizationCodeValue)) {
            tokenIssuedAt = rs.getTimestamp("authorization_code_issued_at").toInstant();
            tokenExpiresAt = rs.getTimestamp("authorization_code_expires_at").toInstant();
            Map<String, Object> authorizationCodeMetadata = parseMap(rs.getString("authorization_code_metadata"));

            OAuth2AuthorizationCode authorizationCode = new OAuth2AuthorizationCode(authorizationCodeValue,
                    tokenIssuedAt, tokenExpiresAt);
            builder.token(authorizationCode, (metadata) -> metadata.putAll(authorizationCodeMetadata));
        }

        String accessTokenValue = rs.getString("access_token_value");
        if (StringUtils.hasText(accessTokenValue)) {
            tokenIssuedAt = rs.getTimestamp("access_token_issued_at").toInstant();
            tokenExpiresAt = rs.getTimestamp("access_token_expires_at").toInstant();
            Map<String, Object> accessTokenMetadata = parseMap(rs.getString("access_token_metadata"));
            OAuth2AccessToken.TokenType tokenType = null;
            if (OAuth2AccessToken.TokenType.BEARER.getValue().equalsIgnoreCase(rs.getString("access_token_type"))) {
                tokenType = OAuth2AccessToken.TokenType.BEARER;
            }

            Set<String> scopes = Collections.emptySet();
            String accessTokenScopes = rs.getString("access_token_scopes");
            if (accessTokenScopes != null) {
                scopes = StringUtils.commaDelimitedListToSet(accessTokenScopes);
            }
            OAuth2AccessToken accessToken = new OAuth2AccessToken(tokenType, accessTokenValue, tokenIssuedAt,
                    tokenExpiresAt, scopes);
            builder.token(accessToken, (metadata) -> metadata.putAll(accessTokenMetadata));
        }

        String refreshTokenValue = rs.getString("refresh_token_value");
        if (StringUtils.hasText(refreshTokenValue)) {
            tokenIssuedAt = rs.getTimestamp("refresh_token_issued_at").toInstant();
            tokenExpiresAt = null;
            Timestamp refreshTokenExpiresAt = rs.getTimestamp("refresh_token_expires_at");
            if (refreshTokenExpiresAt != null) {
                tokenExpiresAt = refreshTokenExpiresAt.toInstant();
            }
            Map<String, Object> refreshTokenMetadata = parseMap(rs.getString("refresh_token_metadata"));

            OAuth2RefreshToken refreshToken = new OAuth2RefreshToken(refreshTokenValue, tokenIssuedAt, tokenExpiresAt);
            builder.token(refreshToken, (metadata) -> metadata.putAll(refreshTokenMetadata));
        }

        String userCodeValue = rs.getString("user_code_value");
        if (StringUtils.hasText(userCodeValue)) {
            tokenIssuedAt = rs.getTimestamp("user_code_issued_at").toInstant();
            tokenExpiresAt = rs.getTimestamp("user_code_expires_at").toInstant();
            Map<String, Object> userCodeMetadata = parseMap(userCodeValue);

            OAuth2UserCode userCode = new OAuth2UserCode(userCodeValue, tokenIssuedAt, tokenExpiresAt);
            builder.token(userCode, (metadata) -> metadata.putAll(userCodeMetadata));
        }

        return builder.build();
    }

    private Map<String, Object> parseMap(String data) {
        try {
            return this.objectMapper.readValue(data, new TypeReference<Map<String, Object>>() {
            });
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }
}
