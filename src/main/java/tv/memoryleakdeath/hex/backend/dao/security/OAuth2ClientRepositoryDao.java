package tv.memoryleakdeath.hex.backend.dao.security;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import tv.memoryleakdeath.hex.backend.dao.mapper.HexRegisteredClientMapper;
import tv.memoryleakdeath.hex.backend.dao.mapper.HexRegisteredClientParametersMapper;
import tv.memoryleakdeath.hex.backend.dao.mapper.HexRegisteredClientPojoMapper;
import tv.memoryleakdeath.hex.common.pojo.HexRegisteredClientPojo;

@Repository
public class OAuth2ClientRepositoryDao {
    private static final Logger logger = LoggerFactory.getLogger(OAuth2ClientRepositoryDao.class);
    private static final String[] COLUMNS = { "id", "client_id", "client_id_issued_at", "client_secret",
            "client_secret_expires_at", "client_name", "client_authentication_methods", "authorization_grant_types",
            "redirect_uris", "post_logout_redirect_uris", "scopes" };
    private static final String[] CAMELCASE_COLUMNS = Stream.of(COLUMNS)
            .map(i -> ":" + CaseUtils.toCamelCase(i, false, '_')).toArray(String[]::new);

    private static final String UPDATE_SQL = """
            UPDATE oauth2clientregistry SET client_secret = :clientSecret, client_secret_expires_at = :clientSecretExpiresAt, client_name = :clientName,
            client_authentication_methods = :clientAuthenticationMethods, authorization_grant_types = :authorizationGrantTypes, redirect_uris = :redirectUris,
            post_logout_redirect_uris = :postLogoutRedirectUris, scopes = :scopes
            WHERE id = :id
            """;

    private static final String UPDATE_BASIC_SQL = """
            UPDATE oauth2clientregistry SET client_name = :clientName, client_authentication_methods = :clientAuthenticationMethods,
            authorization_grant_types = :authorizationGrantTypes, redirect_uris = :redirectUris,
            post_logout_redirect_uris = :postLogoutRedirectUris, scopes = :scopes
            WHERE id = :id
            """;

    private static final String INSERT_SQL = """
            INSERT INTO oauth2clientregistry (%s) VALUES (%s)
            """.formatted(StringUtils.join(COLUMNS, ","), StringUtils.join(CAMELCASE_COLUMNS, ","));

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private HexRegisteredClientParametersMapper paramMapper = new HexRegisteredClientParametersMapper();

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public void save(RegisteredClient registeredClient) {
        update(paramMapper.getParams(registeredClient));
    }

    @Transactional
    public void save(HexRegisteredClientPojo clientPojo) {
        updateBasicProperties(clientPojo);
    }

    private boolean update(HexRegisteredClientPojo clientPojo) {
        int rows = namedParameterJdbcTemplate.update(UPDATE_SQL, new BeanPropertySqlParameterSource(clientPojo));
        return (rows > 0);
    }

    private boolean updateBasicProperties(HexRegisteredClientPojo clientPojo) {
        int rows = namedParameterJdbcTemplate.update(UPDATE_BASIC_SQL, new BeanPropertySqlParameterSource(clientPojo));
        return (rows > 0);
    }

    @Transactional
    public boolean insert(RegisteredClient client, String userId) {
        HexRegisteredClientPojo clientPojo = paramMapper.getParams(client);
        return insert(clientPojo, userId);
    }

    @Transactional
    public boolean insert(final HexRegisteredClientPojo pojo, String userId) {
        pojo.setId(UUID.randomUUID().toString());
        pojo.setClientId(UUID.randomUUID().toString());
        pojo.setClientIdIssuedAt(new Date());
        pojo.setClientSecret(encodeClientSecret(pojo.getClientSecret()));
        int rows = namedParameterJdbcTemplate.update(INSERT_SQL, new BeanPropertySqlParameterSource(pojo));
        String linkingTableInsert = "insert into clientownerregistry (userid, registeredclientid) VALUES (?::UUID,?)";
        int linkingTableRows = jdbcTemplate.update(linkingTableInsert, userId, pojo.getClientId());
        return (rows > 0 && linkingTableRows > 0);
    }

    private String encodeClientSecret(String secret) {
        return passwordEncoder.encode(secret);
    }

    private static final String GET_BY_ID_SQL = """
            select %s from oauth2clientregistry where id = ?
            """.formatted(StringUtils.join(COLUMNS, ","));

    public RegisteredClient findById(String id) {
        if (id == null) {
            return null;
        }

        List<RegisteredClient> results = jdbcTemplate.query(GET_BY_ID_SQL, new HexRegisteredClientMapper(), id);
        if (!results.isEmpty()) {
            return results.get(0);
        }
        return null;
    }

    public HexRegisteredClientPojo findPojoById(String id) {
        List<HexRegisteredClientPojo> results = jdbcTemplate.query(GET_BY_ID_SQL, new HexRegisteredClientPojoMapper(),
                id);
        if (!results.isEmpty()) {
            return results.get(0);
        }
        return null;
    }

    public boolean exists(String id) {
        String sql = "select count(*) from oauth2clientregistry where id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return (count > 0);
    }

    public boolean clientExists(String clientId) {
        String sql = "select count(*) from oauth2clientregistry where client_id = ?";
        int count = jdbcTemplate.queryForObject(sql, Integer.class, clientId);
        return (count > 0);
    }

    private static final String GET_BY_CLIENTID_SQL = """
            select %s from oauth2clientregistry where client_id = ?
            """.formatted(StringUtils.join(COLUMNS, ","));

    public RegisteredClient findByClientId(String clientId) {
        if (clientId == null) {
            return null;
        }

        List<RegisteredClient> results = jdbcTemplate.query(GET_BY_CLIENTID_SQL, new HexRegisteredClientMapper(),
                clientId);
        if (!results.isEmpty()) {
            return results.get(0);
        }
        return null;
    }

    private static final String GET_CLIENTS_FOR_DEVELOPER_SQL = """
            select %s from oauth2clientregistry where client_id in (select registeredclientid from clientownerregistry where userid = ?::UUID)
            """
            .formatted(StringUtils.join(COLUMNS, ","));

    public List<HexRegisteredClientPojo> getRegisteredClientsForDeveloper(String userId) {
        return jdbcTemplate.query(GET_CLIENTS_FOR_DEVELOPER_SQL, new HexRegisteredClientPojoMapper(), userId);
    }

    public Map<String, Integer> getCountOfUserAuthorizationsForDeveloperApps(String userId) {
        String sql = """
                select r.id, COUNT(ac.registered_client_id) as cnt from oauth2clientregistry r
                left outer join oauth2authorizationconsent ac
                on (r.id = ac.registered_client_id)
                where r.client_id in (select registeredclientid from clientownerregistry
                                      where userid = ?::UUID)
                group by r.id
                """;
        return jdbcTemplate.query(sql, new ResultSetExtractor<Map<String, Integer>>() {
            @Override
            public Map<String, Integer> extractData(ResultSet rs) throws SQLException, DataAccessException {
                Map<String, Integer> resultsMap = new HashMap<>();
                while (rs.next()) {
                    String id = rs.getString("id");
                    Integer count = rs.getInt("cnt");
                    resultsMap.put(id, count);
                }
                return resultsMap;
            }

        }, userId);
    }

    @Transactional
    public boolean updateClientSecret(String id, String newSecret) {
        String sql = "update oauth2clientregistry set client_secret = ? where id = ?";
        String clientSecret = encodeClientSecret(newSecret);
        int rows = jdbcTemplate.update(sql, clientSecret, id);
        return (rows > 0);
    }

    @Transactional
    public boolean deleteClient(String id) {
        String sql = "delete from oauth2clientregistry where id = ?";
        int rows = jdbcTemplate.update(sql, id);
        return (rows > 0);
    }

}
