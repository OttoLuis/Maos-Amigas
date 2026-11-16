
package com.ottoluis.MaosAmigas.services;

import java.time.Instant;
import java.util.List;

import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.stereotype.Service;

@Service
public class JwtTokenService {

    private final JwtEncoder jwtEncoder;
    private final long jwtDurationSeconds;

    public JwtTokenService(
            JWKSource<SecurityContext> jwkSource,
            @Value("${security.jwt.duration}") long jwtDurationSeconds) {

        this.jwtEncoder = new NimbusJwtEncoder(jwkSource);
        this.jwtDurationSeconds = jwtDurationSeconds;
    }

    public String gerarToken(UserDetails usuario) {

        Instant agora = Instant.now();
        Instant expiracao = agora.plusSeconds(jwtDurationSeconds);

        List<String> authorities = usuario.getAuthorities()
                .stream()
                .map(authority -> authority.getAuthority())
                .toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .subject(usuario.getUsername())
                .issuedAt(agora)
                .expiresAt(expiracao)
                .claim("username", usuario.getUsername())
                .claim("authorities", authorities)
                .build();

        JwsHeader header = JwsHeader
                .with(SignatureAlgorithm.RS256)
                .build();

        return jwtEncoder.encode(
                JwtEncoderParameters.from(header, claims)
        ).getTokenValue();
    }

    public long getJwtDurationSeconds() {
        return jwtDurationSeconds;
    }
}
