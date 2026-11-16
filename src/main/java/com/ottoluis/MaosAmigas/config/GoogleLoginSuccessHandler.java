
package com.ottoluis.MaosAmigas.config;

import com.ottoluis.MaosAmigas.services.GoogleOAuth2Service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class GoogleLoginSuccessHandler
        implements AuthenticationSuccessHandler {

    private final GoogleOAuth2Service googleOAuth2Service;
    private final String frontendUrl;

    public GoogleLoginSuccessHandler(
            GoogleOAuth2Service googleOAuth2Service,
            @Value("${app.frontend.url:http://localhost:5173}")
            String frontendUrl) {

        this.googleOAuth2Service = googleOAuth2Service;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication)
            throws IOException, ServletException {

        try {
            if (!(authentication.getPrincipal() instanceof OidcUser googleUser)) {
                throw new AuthenticationException(
                        "Usuário autenticado não é OIDC.") {};
            }

            String token = googleOAuth2Service.autenticar(googleUser);

            response.sendRedirect(
                    frontendUrl + "/login#token=" + token
            );

        } catch (Exception exception) {

            exception.printStackTrace();

            response.sendRedirect(
                    frontendUrl + "/login#erro=google_login"
            );
        }
    }
}
