
package com.ottoluis.MaosAmigas.services;

import java.security.SecureRandom;
import java.util.Base64;

import com.ottoluis.MaosAmigas.models.Role;
import com.ottoluis.MaosAmigas.models.Usuario;
import com.ottoluis.MaosAmigas.repository.RoleRepository;
import com.ottoluis.MaosAmigas.repository.UsuarioRepository;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GoogleOAuth2Service {

    private final UsuarioRepository usuarioRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenService jwtTokenService;

    public GoogleOAuth2Service(
            UsuarioRepository usuarioRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenService jwtTokenService) {

        this.usuarioRepository = usuarioRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenService = jwtTokenService;
    }

    @Transactional
    public String autenticar(OidcUser googleUser) {

        Boolean emailVerificado =
                googleUser.getClaimAsBoolean("email_verified");

        if (!Boolean.TRUE.equals(emailVerificado)) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("email_not_verified"));
        }

        String email = googleUser.getEmail();

        if (email == null || email.isBlank()) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error("email_missing"));
        }

        email = email.trim().toLowerCase();

        Usuario usuario = usuarioRepository.findByEmail(email);

        if (usuario == null) {
            usuario = criarUsuarioGoogle(googleUser, email);
        }

        return jwtTokenService.gerarToken(usuario);
    }

    private Usuario criarUsuarioGoogle(
            OidcUser googleUser,
            String email) {

        Role roleUsuario = roleRepository
                .findByAuthority("ROLE_USUARIO")
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Role ROLE_USUARIO não encontrada."));

        String nome = googleUser.getFullName();

        if (nome == null || nome.isBlank()) {
            nome = email.substring(0, email.indexOf('@'));
        }

        Usuario usuario = new Usuario();

        usuario.setNome(nome);
        usuario.setEmail(email);
        usuario.setSenha(passwordEncoder.encode(gerarSenhaAleatoria()));
        usuario.addRole(roleUsuario);

        return usuarioRepository.save(usuario);
    }

    private String gerarSenhaAleatoria() {

        byte[] bytes = new byte[32];
        new SecureRandom().nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }
}
