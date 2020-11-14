package no.toreb.parallelprocessing.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private final String username;
    private final String password;
    private final List<String> roles;

    @Autowired
    public SecurityConfig(@Value("${spring.security.user.name}") final String username,
                          @Value("${spring.security.user.password}") final String password,
                          @Value("${spring.security.user.roles}") final List<String> roles) {
        this.username = username;
        this.password = password;
        this.roles = new ArrayList<>(roles);
    }

    @Bean
    SecurityWebFilterChain securityWebFilterChain(final ServerHttpSecurity security) {
        return security.csrf().disable()
                       .httpBasic()
                       .and()
                       .authorizeExchange()
                       .pathMatchers("/actuator/health").permitAll()
                       .anyExchange().authenticated()
                       .and()
                       .build();
    }

    @Bean
    MapReactiveUserDetailsService mapReactiveUserDetailsService() {
        final var passwordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        final var userDetails = User.withUsername(username)
                                    .passwordEncoder(passwordEncoder::encode)
                                    .password(password)
                                    .roles(roles.toArray(new String[0]))
                                    .build();
        return new MapReactiveUserDetailsService(userDetails);
    }
}
