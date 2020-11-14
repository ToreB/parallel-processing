package no.toreb.parallelprocessing.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.ReactiveAuditorAware;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import reactor.core.publisher.Mono;

@Configuration
@EnableR2dbcAuditing
@RequiredArgsConstructor
class DataR2dbcConfig {

    @Bean
    ReactiveAuditorAware<String> reactiveAuditorAware(@Value("${spring.application.name}") final String appName) {
        return () -> Mono.just(appName);
    }
}
