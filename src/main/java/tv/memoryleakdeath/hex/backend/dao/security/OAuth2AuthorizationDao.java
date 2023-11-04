package tv.memoryleakdeath.hex.backend.dao.security;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Repository;

import jakarta.annotation.PostConstruct;
import tv.memoryleakdeath.hex.backend.dao.mapper.HexOAuth2AuthorizationMapper;
import tv.memoryleakdeath.hex.backend.dao.mapper.HexOAuth2AuthorizationParametersMapper;
import tv.memoryleakdeath.hex.common.pojo.HexOAuthAuthorizationPojo;

@Repository
public class OAuth2AuthorizationDao implements OAuth2AuthorizationService {
    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthorizationDao.class);
    private static final String[] COLUMNS = { "id", "registered_client_id", "principal_name",
            "authorization_grant_type", "authorized_scopes", "attributes", "state", "authorization_code_value",
            "authorization_code_issued_at", "authorization_code_expires_at", "authorization_code_metadata",
            "access_token_value", "access_token_issued_at", "access_token_expires_at", "access_token_metadata",
            "access_token_type", "access_token_scopes", "refresh_token_value", "refresh_token_issued_at",
            "refresh_token_expires_at", "refresh_token_metadata", "user_code_value", "user_code_issued_at",
            "user_code_expires_at", "user_code_metadata" };
    private static final String[] CAMELCASE_COLUMNS = Stream.of(COLUMNS)
            .map(i -> ":" + CaseUtils.toCamelCase(i, false, '_')).toArray(String[]::new);
    private static final String CONCATENATED_COLUMNS = StringUtils.join(COLUMNS, ",");

    private HexOAuth2AuthorizationMapper authMapper = null;
    private HexOAuth2AuthorizationParametersMapper paramMapper = new HexOAuth2AuthorizationParametersMapper();

    @Autowired
    private RegisteredClientRepository clientRepository;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        this.authMapper = new HexOAuth2AuthorizationMapper(clientRepository);
    }

    private static final String UPDATE_SQL = """
            UPDATE oauth2authorization SET registered_client_id = :registeredClientId, principal_name = :principalName,
            authorization_grant_type = :authorizationGrantType, authorized_scopes = :authorizedScopes, attributes = :attributes,
            state = :state, authorization_code_value = :authorizationCodeValue, authorization_code_issued_at = :authorizationCodeIssuedAt,
            authorization_code_expires_at = :authorizationCodeExpiresAt, authorization_code_metadata = :authorizationCodeMetadata,
            access_token_value = :accessTokenValue, access_token_issued_at = :accessTokenIssuedAt, access_token_expires_at = :accessTokenExpiresAt,
            access_token_metadata = :accessTokenMetadata, access_token_type = :accessTokenType, access_token_scopes = :accessTokenScopes,
            refresh_token_value = :refreshTokenValue, refresh_token_issued_at = :refreshTokenIssuedAt,
            refresh_token_expires_at = :refreshTokenExpiresAt, refresh_token_metadata = :refreshTokenMetadata,
            user_code_value = :userCodeValue, user_code_issued_at = :userCodeIssuedAt, user_code_expires_at = :userCodeExpiresAt,
            user_code_metadata = :userCodeMetadata
            WHERE id = :id
            """;

    private static final String INSERT_SQL = """
            insert into oauth2authorization (%s) VALUES (%s)
            """.formatted(CONCATENATED_COLUMNS, StringUtils.join(CAMELCASE_COLUMNS, ","));
    @Override
    public void save(OAuth2Authorization authorization) {
        HexOAuthAuthorizationPojo pojo = paramMapper.getParams(authorization);
        if (exists(authorization.getId())) {
            update(pojo);
        } else {
            insert(pojo);
        }
    }

    private boolean update(HexOAuthAuthorizationPojo mapper) {
        int rows = namedParameterJdbcTemplate.update(UPDATE_SQL, new BeanPropertySqlParameterSource(mapper));
        return (rows > 0);
    }

    private boolean insert(HexOAuthAuthorizationPojo mapper) {
        int rows = namedParameterJdbcTemplate.update(INSERT_SQL, new BeanPropertySqlParameterSource(mapper));
        return (rows > 0);
    }

    @Override
    public void remove(OAuth2Authorization authorization) {
        if (authorization == null) {
            return;
        }
        String sql = "DELETE FROM oauth2authorization WHERE id = ?";
        jdbcTemplate.update(sql, authorization.getId());
    }

    private boolean exists(String id) {
        String sql = "select COUNT(*) from oauth2authorization where id = ?";
        int result = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return (result > 0);
    }

    private static final String GET_BY_ID_SQL = """
            select %s from oauth2authorization where id = ?
            """.formatted(CONCATENATED_COLUMNS);
    @Override
    public OAuth2Authorization findById(String id) {
        List<OAuth2Authorization> results = jdbcTemplate.query(GET_BY_ID_SQL, authMapper, id);
        if (!results.isEmpty()) {
            return results.get(0);
        }
        return null;
    }

    @Override
    public OAuth2Authorization findByToken(String token, OAuth2TokenType tokenType) {
        if (tokenType == null) {
            return null;
        }
        String sql = null;
        String tokenTypeValue = tokenType.getValue();
        if (OAuth2ParameterNames.STATE.equals(tokenTypeValue)) {
            sql = getSqlStatementForFilter("state");
        } else if (OAuth2ParameterNames.CODE.equals(tokenTypeValue)) {
            sql = getSqlStatementForFilter("authorization_code_value");
        } else if (OAuth2ParameterNames.ACCESS_TOKEN.equals(tokenTypeValue)) {
            sql = getSqlStatementForFilter("access_token_value");
        } else if (OAuth2TokenType.REFRESH_TOKEN.equals(tokenType)) {
            sql = getSqlStatementForFilter("refresh_token_value");
        } else if (OAuth2ParameterNames.USER_CODE.equals(tokenTypeValue)) {
            sql = getSqlStatementForFilter("user_code_value");
        }

        List<OAuth2Authorization> results = jdbcTemplate.query(sql, authMapper, token);
        if (!results.isEmpty()) {
            return results.get(0);
        }
        return null;
    }

    private String getSqlStatementForFilter(String columnName) {
        return "select %s from oauth2authorization where %s = ?".formatted(CONCATENATED_COLUMNS, columnName);
    }

    public List<String> getNonExpiredAccessTokensForApplicationAndUser(String registeredClientId, String userName) {
        String sql = "select access_token_value from oauth2authorization where registered_client_id = ? and principal_name = ? and authorization_code_expires_at > NOW()";
        return jdbcTemplate.query(sql, new ResultSetExtractor<List<String>>() {
            @Override
            public List<String> extractData(ResultSet rs) throws SQLException, DataAccessException {
                List<String> results = new ArrayList<>();
                while (rs.next()) {
                    results.add(rs.getString("access_token_value"));
                }
                return results;
            }
        }, registeredClientId, userName);
    }

}
