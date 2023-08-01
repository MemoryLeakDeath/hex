package tv.memoryleakdeath.hex.frontend.controller;

import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsent;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationConsentService;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.hex.backend.dao.security.AuthenticationDao;
import tv.memoryleakdeath.hex.backend.dao.user.UserDetailsDao;
import tv.memoryleakdeath.hex.common.pojo.Auth;
import tv.memoryleakdeath.hex.common.pojo.UserDetails;

@Controller
@RequestMapping("/oauth2")
public class OAuth2AuthorizationConsentController extends BaseFrontendController {
    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthorizationConsentController.class);

    @Autowired
    private RegisteredClientRepository clientRepo;

    @Autowired
    private OAuth2AuthorizationConsentService consentService;

    @Autowired
    private AuthenticationDao authDao;

    @Autowired
    private UserDetailsDao userDetailsDao;

    @GetMapping("/consent")
    public String consent(HttpServletRequest request, Principal principal, Model model,
            @RequestParam(name = OAuth2ParameterNames.CLIENT_ID, required = true) String clientId,
            @RequestParam(name = OAuth2ParameterNames.SCOPE, required = true) String scope,
            @RequestParam(name = OAuth2ParameterNames.STATE, required = true) String state) {
        Set<String> newlyRequestedScopes = new HashSet<>();
        Set<String> previouslyApprovedScopes = new HashSet<>();
        setLayout(model, "layout/no-navigation");
        setPageTitle(request, model, "title.consent");
        try {
            RegisteredClient client = clientRepo.findByClientId(clientId);
            OAuth2AuthorizationConsent consent = consentService.findById(clientId, principal.getName());

            if (client == null) {
                addErrorMessage(request, "text.error.systemerror");
                return "redirect:/logoutonly";
            }

            if (consent != null) {
                previouslyApprovedScopes = consent.getScopes();
            }

            for (String newScope : StringUtils.delimitedListToStringArray(scope, " ")) {
                if (OidcScopes.OPENID.equals(newScope)) {
                    continue; // skip "openid" scope
                }
                if (!previouslyApprovedScopes.contains(newScope)) {
                    newlyRequestedScopes.add(newScope);
                }
            }

            Auth user = authDao.getUserByUsername(principal.getName());
            if (Boolean.FALSE.equals(user.getActive())) {
                return "redirect:/logoutonly";
            }
            UserDetails userDetails = userDetailsDao.findById(user.getId());
            model.addAttribute("clientId", clientId);
            model.addAttribute("clientName", client.getClientName());
            model.addAttribute("state", state);
            model.addAttribute("requestedScopes", newlyRequestedScopes);
            model.addAttribute("previousScopes", mapScopesToDescription(previouslyApprovedScopes));
            model.addAttribute("newScopes", mapScopesToDescription(newlyRequestedScopes));
            model.addAttribute("userDisplayName", userDetails.getDisplayName());
            model.addAttribute("userAvatar", userDetails.getGravatarId());
            model.addAttribute("authURI", "/oauth2/authorize");
        } catch (Exception e) {
            logger.error("Unable to display OAuth2 consent form!", e);
            addErrorMessage(request, "text.error.systemerror");
        }
        return "oauth/consent";
    }

    private List<String> mapScopesToDescription(Set<String> scopes) {
        return scopes.stream().map(scope -> "text.oauth.scope." + scope).toList();
    }
}
