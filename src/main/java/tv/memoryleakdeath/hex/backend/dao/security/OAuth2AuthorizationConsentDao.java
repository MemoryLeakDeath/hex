package tv.memoryleakdeath.hex.backend.dao.security;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.CaseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.stereotype.Repository;

import tv.memoryleakdeath.hex.backend.dao.mapper.UserApplicationInfoMapper;
import tv.memoryleakdeath.hex.common.pojo.HexOAuthAuthorizationConsentPojo;
import tv.memoryleakdeath.hex.common.pojo.UserApplicationInfo;

@Repository
public class OAuth2AuthorizationConsentDao implements OAuth2AuthorizationConsentService {
    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthorizationConsentDao.class);
    private static final String[] COLUMNS = { "registered_client_id", "principal_name", "authorities" };
    private static final String[] CAMELCASE_COLUMNS = Stream.of(COLUMNS)
            .map(i -> ":" + CaseUtils.toCamelCase(i, false, '_')).toArray(String[]::new);

    @Autowired
    private NamedParameterJdbcTemplate namedParamJdbcTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private OAuth2ClientRepositoryDao registeredClientRepository;

    @Override
    public void save(OAuth2AuthorizationConsent authorizationConsent) {
        HexOAuthAuthorizationConsentPojo pojo = new HexOAuthAuthorizationConsentPojo();
        pojo.setPrincipalName(authorizationConsent.getPrincipalName());
        pojo.setRegisteredClientId(authorizationConsent.getRegisteredClientId());
        pojo.setAuthorities(authorizationConsent.getAuthorities().stream().map(c -> c.getAuthority())
                .collect(Collectors.joining(",")));
        if (exists(pojo.getRegisteredClientId(), pojo.getPrincipalName())) {
            update(pojo);
        } else {
            insert(pojo);
        }
    }

    private static final String INSERT_SQL = """
            INSERT INTO oauth2authorizationconsent (%s) VALUES(%s)
            """.formatted(StringUtils.join(COLUMNS, ","), StringUtils.join(CAMELCASE_COLUMNS, ","));

    private boolean insert(HexOAuthAuthorizationConsentPojo pojo) {
        int rows = namedParamJdbcTemplate.update(INSERT_SQL, new BeanPropertySqlParameterSource(pojo));
        return (rows > 0);
    }

    private boolean update(HexOAuthAuthorizationConsentPojo pojo) {
        String sql = """
                UPDATE oauth2authorizationconsent SET authorities = :authorities
                WHERE registered_client_id = :registeredClientId AND principal_name = :principalName
                """;
        int rows = namedParamJdbcTemplate.update(sql, new BeanPropertySqlParameterSource(pojo));
        return (rows > 0);
    }

    @Override
    public void remove(OAuth2AuthorizationConsent authorizationConsent) {
        if (authorizationConsent == null) {
            return;
        }
        remove(authorizationConsent.getRegisteredClientId(), authorizationConsent.getPrincipalName());
    }

    public void remove(String clientId, String principalName) {
        String sql = "delete from oauth2authorizationconsent where registered_client_id = ? and principal_name = ?";
        jdbcTemplate.update(sql, clientId, principalName);
    }

    private boolean exists(String registeredClientId, String principalName) {
        String sql = "select COUNT(*) from oauth2authorizationconsent where registered_client_id = ? and principal_name = ?";
        int result = jdbcTemplate.queryForObject(sql, Integer.class, registeredClientId, principalName);
        return (result > 0);
    }

    @Override
    public OAuth2AuthorizationConsent findById(String registeredClientId, String principalName) {
        if (!registeredClientRepository.exists(registeredClientId)) {
            return null;
        }
        String sql = "select authorities from oauth2authorizationconsent where registered_client_id = ? and principal_name = ?";
        List<String> authorities = jdbcTemplate.query(sql, (rs, rowNum) -> {
            return rs.getString("authorities");
        }, registeredClientId, principalName);
        if (authorities.isEmpty()) {
            return null;
        }
        OAuth2AuthorizationConsent.Builder builder = OAuth2AuthorizationConsent.withId(registeredClientId,
                principalName);
        org.springframework.util.StringUtils.commaDelimitedListToSet(authorities.get(0))
                .stream().forEach(g -> {
                    builder.authority(new SimpleGrantedAuthority(g));
                });
        return builder.build();
    }

    public List<UserApplicationInfo> findApplicationInfoForUser(String principalName) {
        String sql = """
                select ac.registered_client_id, r.client_name, max(auth.authorization_code_expires_at) as expires,
                greatest(max(auth.authorization_code_issued_at), max(auth.access_token_issued_at), max(auth.refresh_token_issued_at)) as last_used
                from oauth2authorizationconsent ac
                inner join oauth2clientregistry r on (ac.registered_client_id = r.id)
                left outer join oauth2authorization auth on (r.id = auth.registered_client_id)
                where auth.principal_name = ?
                group by ac.registered_client_id, r.client_name
                order by last_used desc
                """;
        return jdbcTemplate.query(sql, new UserApplicationInfoMapper(), principalName);
    }

}
