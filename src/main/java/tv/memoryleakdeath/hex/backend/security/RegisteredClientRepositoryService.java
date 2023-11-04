package tv.memoryleakdeath.hex.backend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;

import tv.memoryleakdeath.hex.backend.dao.security.OAuth2ClientRepositoryDao;

@Service
public class RegisteredClientRepositoryService implements RegisteredClientRepository {
    @Autowired
    private OAuth2ClientRepositoryDao dao;

    @Override
    public void save(RegisteredClient registeredClient) {
        dao.save(registeredClient);
    }

    @Override
    public RegisteredClient findById(String id) {
        return dao.findById(id);
    }

    @Override
    public RegisteredClient findByClientId(String clientId) {
        return dao.findByClientId(clientId);
    }

}
