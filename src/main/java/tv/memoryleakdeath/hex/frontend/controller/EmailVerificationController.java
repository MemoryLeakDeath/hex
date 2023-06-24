package tv.memoryleakdeath.hex.frontend.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.servlet.http.HttpServletRequest;
import tv.memoryleakdeath.hex.backend.dao.security.AuthenticationDao;
import tv.memoryleakdeath.hex.backend.email.EmailVerificationService;

@Controller
@RequestMapping("/email/verify")
public class EmailVerificationController extends BaseFrontendController {
    private static final Logger logger = LoggerFactory.getLogger(EmailVerificationController.class);

    @Autowired
    private EmailVerificationService verificationService;

    @Autowired
    private AuthenticationDao authDao;

    @GetMapping("/{token}")
    @PreAuthorize("hasRole('ROLE_USER')")
    public String verify(HttpServletRequest request, Model model, @PathVariable(name = "token", required = true) String token) {
        try {
            String userId = authDao.getUserIdForUsername(getUsername(request));
            boolean isVerified = verificationService.isTokenValid(userId, token);
            if (isVerified) {
                addSuccessMessage(model, "email.verify.text.success");
            } else {
                addErrorMessage(model, "email.verify.text.error");
            }
        } catch (Exception e) {
            logger.error("Unable to verify email token", e);
            addErrorMessage(model, "text.error.systemerror");
        }
        return "redirect: /";
    }
}
