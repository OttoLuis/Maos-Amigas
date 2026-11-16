
package com.ottoluis.MaosAmigas.controller;

import java.util.Map;

import com.ottoluis.MaosAmigas.services.JwtTokenService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class LoginController {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public LoginController(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService) {

        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        if (request.email() == null || request.senha() == null
                || request.email().isBlank() || request.senha().isBlank()) {

            return ResponseEntity.badRequest().body(
                    Map.of("mensagem", "Informe o e-mail e a senha."));
        }

        try {
            String email = request.email().trim();

            UserDetails usuario =
                    userDetailsService.loadUserByUsername(email);

            if (!passwordEncoder.matches(
                    request.senha(), usuario.getPassword())) {

                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(Map.of(
                                "mensagem",
                                "E-mail ou senha inválidos."));
            }

            String token = jwtTokenService.gerarToken(usuario);

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "accessToken", token,
                    "tokenType", "Bearer",
                    "expiresIn", jwtTokenService.getJwtDurationSeconds()
            ));

        } catch (UsernameNotFoundException exception) {

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of(
                            "mensagem",
                            "E-mail ou senha inválidos."));
        }
    }

    public record LoginRequest(String email, String senha) {
    }
}
