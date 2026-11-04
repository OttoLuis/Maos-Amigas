package com.ottoluis.MaosAmigas.config;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ResourceServerConfig {

    @Value("${cors.origins}")
    private String corsOrigins;

    @Bean
    @Profile("test")
    @Order(1)
    public SecurityFilterChain h2SecurityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .securityMatcher(PathRequest.toH2Console())
                .csrf(csrf -> csrf.disable())
                .headers(headers ->
                        headers.frameOptions(frameOptions ->
                                frameOptions.disable()
                        )
                );

        return http.build();
    }

    @Bean
    @Order(3)
    public SecurityFilterChain rsSecurityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .csrf(csrf -> csrf.disable())

                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(
                                        "/auth/login",
                                        "/auth/register",
                                        "/v3/api-docs",
                                        "/v3/api-docs/**",
                                        "/swagger-ui/**",
                                        "/swagger-ui.html"
                                ).permitAll()
                                .anyRequest().authenticated()
                )

                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(
                                        jwtAuthenticationConverter()
                                )
                        )
                )

                .cors(cors ->
                        cors.configurationSource(
                                corsConfigurationSource()
                        )
                );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter =
                new JwtGrantedAuthoritiesConverter();

        /*
         * O JWT possui:
         *
         * "authorities": [
         *     "ROLE_USUARIO"
         * ]
         *
         * Portanto, o Spring Security deve ler as autoridades
         * diretamente dessa propriedade.
         */
        grantedAuthoritiesConverter.setAuthoritiesClaimName(
                "authorities"
        );

        /*
         * As authorities já possuem ROLE_USUARIO,
         * então não adicionamos outro prefixo.
         */
        grantedAuthoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter jwtAuthenticationConverter =
                new JwtAuthenticationConverter();

        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(
                grantedAuthoritiesConverter
        );

        /*
         * IMPORTANTE:
         *
         * O JWT do Marcos possui:
         *
         * "sub": "myclientid"
         * "username": "marcos@email.com"
         *
         * Por padrão, o Spring usaria o "sub" como
         * authentication.getName().
         *
         * Aqui estamos dizendo para utilizar o campo
         * "username" como principal.
         *
         * Dessa forma:
         *
         * authentication.getName()
         *
         * retorna:
         *
         * marcos@email.com
         */
        jwtAuthenticationConverter.setPrincipalClaimName(
                "username"
        );

        return jwtAuthenticationConverter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        String[] origins = corsOrigins.split(",");

        CorsConfiguration corsConfig =
                new CorsConfiguration();

        corsConfig.setAllowedOriginPatterns(
                Arrays.asList(origins)
        );

        corsConfig.setAllowedMethods(
                Arrays.asList(
                        "GET",
                        "POST",
                        "PUT",
                        "DELETE",
                        "PATCH",
                        "OPTIONS"
                )
        );

        corsConfig.setAllowCredentials(true);

        corsConfig.setAllowedHeaders(
                Arrays.asList(
                        "Authorization",
                        "Content-Type"
                )
        );

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration(
                "/**",
                corsConfig
        );

        return source;
    }

    @Bean
    public FilterRegistrationBean<CorsFilter>
    filterRegistrationBeanCorsFilter() {

        FilterRegistrationBean<CorsFilter> bean =
                new FilterRegistrationBean<>(
                        new CorsFilter(
                                corsConfigurationSource()
                        )
                );

        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);

        return bean;
    }
}
