package tv.memoryleakdeath.hex.backend.dao.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.security.jackson2.SecurityJackson2Modules;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.oauth2.core.OAuth2Token;
import org.springframework.security.oauth2.core.OAuth2UserCode;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationCode;
import org.springframework.security.oauth2.server.authorization.jackson2.OAuth2AuthorizationServerJackson2Module;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import tv.memoryleakdeath.hex.common.pojo.HexOAuthAuthorizationPojo;

public class HexOAuth2AuthorizationParametersMapper {
    private ObjectMapper objectMapper = new ObjectMapper();

    public HexOAuth2AuthorizationParametersMapper() {
        ClassLoader classLoader = HexOAuth2AuthorizationParametersMapper.class.getClassLoader();
        List<Module> securityModules = SecurityJackson2Modules.getModules(classLoader);
        this.objectMapper.registerModules(securityModules);
        this.objectMapper.registerModule(new OAuth2AuthorizationServerJackson2Module());
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public HexOAuthAuthorizationPojo getParams(OAuth2Authorization authorization) {
        HexOAuthAuthorizationPojo pojo = new HexOAuthAuthorizationPojo();
        pojo.setId(authorization.getId());
        pojo.setRegisteredClientId(authorization.getRegisteredClientId());
        pojo.setPrincipalName(authorization.getPrincipalName());
        pojo.setAuthorizationGrantType(authorization.getAuthorizationGrantType().getValue());

        String authorizedScopes = null;
        if (!CollectionUtils.isEmpty(authorization.getAuthorizedScopes())) {
            authorizedScopes = StringUtils.collectionToDelimitedString(authorization.getAuthorizedScopes(), ",");
        }
        pojo.setAuthorizedScopes(authorizedScopes);

        String attributes = writeMap(authorization.getAttributes());
        pojo.setAttributes(attributes);

        String state = null;
        String authorizationState = authorization.getAttribute(OAuth2ParameterNames.STATE);
        if (StringUtils.hasText(authorizationState)) {
            state = authorizationState;
        }
        pojo.setState(state);

        OAuth2Authorization.Token<OAuth2AuthorizationCode> authorizationCode = authorization
                .getToken(OAuth2AuthorizationCode.class);
        TokenPojo authorizationCodeSqlParameters = parseToken(authorizationCode);
        pojo.setAuthorizationCodeExpiresAt(authorizationCodeSqlParameters.getTokenExpiresAt());
        pojo.setAuthorizationCodeIssuedAt(authorizationCodeSqlParameters.getTokenIssuedAt());
        pojo.setAuthorizationCodeMetadata(authorizationCodeSqlParameters.getMetadata());
        pojo.setAuthorizationCodeValue(authorizationCodeSqlParameters.getTokenValue());

        OAuth2Authorization.Token<OAuth2AccessToken> accessToken = authorization.getToken(OAuth2AccessToken.class);
        TokenPojo accessTokenSqlParameters = parseToken(accessToken);
        pojo.setAccessTokenExpiresAt(accessTokenSqlParameters.getTokenExpiresAt());
        pojo.setAccessTokenIssuedAt(accessTokenSqlParameters.getTokenIssuedAt());
        pojo.setAccessTokenMetadata(accessTokenSqlParameters.getMetadata());
        pojo.setAccessTokenValue(accessTokenSqlParameters.getTokenValue());

        String accessTokenType = null;
        String accessTokenScopes = null;
        if (accessToken != null) {
            accessTokenType = accessToken.getToken().getTokenType().getValue();
            if (!CollectionUtils.isEmpty(accessToken.getToken().getScopes())) {
                accessTokenScopes = StringUtils.collectionToDelimitedString(accessToken.getToken().getScopes(), ",");
            }
        }
        pojo.setAccessTokenType(accessTokenType);
        pojo.setAccessTokenScopes(accessTokenScopes);

        OAuth2Authorization.Token<OAuth2RefreshToken> refreshToken = authorization.getRefreshToken();
        TokenPojo refreshTokenSqlParameters = parseToken(refreshToken);
        pojo.setRefreshTokenExpiresAt(refreshTokenSqlParameters.getTokenExpiresAt());
        pojo.setRefreshTokenIssuedAt(refreshTokenSqlParameters.getTokenIssuedAt());
        pojo.setRefreshTokenMetadata(refreshTokenSqlParameters.getMetadata());
        pojo.setRefreshTokenValue(refreshTokenSqlParameters.getTokenValue());

        OAuth2Authorization.Token<OAuth2UserCode> userCode = authorization.getToken(OAuth2UserCode.class);
        TokenPojo userCodeSqlParameters = parseToken(userCode);
        pojo.setUserCodeExpiresAt(userCodeSqlParameters.getTokenExpiresAt());
        pojo.setUserCodeIssuedAt(userCodeSqlParameters.getTokenIssuedAt());
        pojo.setUserCodeMetadata(userCodeSqlParameters.getMetadata());
        pojo.setUserCodeValue(userCodeSqlParameters.getTokenValue());
        return pojo;
    }

    private String writeMap(Map<String, Object> data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (Exception ex) {
            throw new IllegalArgumentException(ex.getMessage(), ex);
        }
    }

    private <T extends OAuth2Token> TokenPojo parseToken(OAuth2Authorization.Token<T> token) {
        String tokenValue = null;
        Date tokenIssuedAt = null;
        Date tokenExpiresAt = null;
        String metadata = null;
        if (token != null) {
            tokenValue = token.getToken().getTokenValue();
            if (token.getToken().getIssuedAt() != null) {
                tokenIssuedAt = Date.from(token.getToken().getIssuedAt());
            }
            if (token.getToken().getExpiresAt() != null) {
                tokenExpiresAt = Date.from(token.getToken().getExpiresAt());
            }
            metadata = writeMap(token.getMetadata());
        }

        TokenPojo pojo = new TokenPojo();
        pojo.setTokenValue(tokenValue);
        pojo.setTokenIssuedAt(tokenIssuedAt);
        pojo.setTokenExpiresAt(tokenExpiresAt);
        pojo.setMetadata(metadata);
        return pojo;
    }

}

class TokenPojo {
    private String tokenValue = null;
    private Date tokenIssuedAt = null;
    private Date tokenExpiresAt = null;
    private String metadata = null;

    public String getTokenValue() {
        return tokenValue;
    }

    public void setTokenValue(String tokenValue) {
        this.tokenValue = tokenValue;
    }

    public Date getTokenIssuedAt() {
        return tokenIssuedAt;
    }

    public void setTokenIssuedAt(Date tokenIssuedAt) {
        this.tokenIssuedAt = tokenIssuedAt;
    }

    public Date getTokenExpiresAt() {
        return tokenExpiresAt;
    }

    public void setTokenExpiresAt(Date tokenExpiresAt) {
        this.tokenExpiresAt = tokenExpiresAt;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

}
