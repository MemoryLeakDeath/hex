package tv.memoryleakdeath.hex.backend.dao.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import tv.memoryleakdeath.hex.common.pojo.HexRegisteredClientPojo;

public class HexRegisteredClientPojoMapper implements RowMapper<HexRegisteredClientPojo> {

    @Override
    public HexRegisteredClientPojo mapRow(ResultSet rs, int rowNum) throws SQLException {
        HexRegisteredClientPojo pojo = new HexRegisteredClientPojo();
        pojo.setAuthorizationGrantTypes(rs.getString("authorization_grant_types"));
        pojo.setClientAuthenticationMethods(rs.getString("client_authentication_methods"));
        pojo.setClientId(rs.getString("client_id"));
        pojo.setClientIdIssuedAt(rs.getTimestamp("client_id_issued_at"));
        pojo.setClientName(rs.getString("client_name"));
        pojo.setClientSecret(rs.getString("client_secret"));
        pojo.setClientSecretExpiresAt(rs.getTimestamp("client_secret_expires_at"));
        pojo.setId(rs.getString("id"));
        pojo.setPostLogoutRedirectUris(rs.getString("post_logout_redirect_uris"));
        pojo.setRedirectUris(rs.getString("redirect_uris"));
        pojo.setScopes(rs.getString("scopes"));
        return pojo;
    }

}
