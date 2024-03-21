package kr.mooner510.konopuro.global.security.config

import com.fasterxml.jackson.databind.ObjectMapper
import kr.mooner510.konopuro.global.security.component.TokenProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer
import org.springframework.security.config.annotation.web.configurers.FormLoginConfigurer
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsUtils

@Configuration
@EnableWebSecurity
class SecurityConfig(
    private val tokenProvider: TokenProvider,
    private val objectMapper: ObjectMapper
) {
    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    @Throws(Exception::class)
    fun configure(httpSecurity: HttpSecurity): SecurityFilterChain {
        return httpSecurity
            .csrf(CsrfConfigurer<HttpSecurity>::disable)
            .formLogin(FormLoginConfigurer<HttpSecurity>::disable)
            .logout(LogoutConfigurer<HttpSecurity>::disable)
            .cors { }
            .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
            .authorizeHttpRequests {
                it.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()

//                it.requestMatchers("/ws/chat/**").permitAll()

//                it.requestMatchers(HttpMethod.DELETE, "/api/test").permitAll()
//                it.requestMatchers(HttpMethod.GET, "/api/test/push").permitAll()
//
//                it.requestMatchers(HttpMethod.GET, "/v3/api-docs/**", "/swagger-ui/**", "/swagger-resources/**").permitAll()
////                it.requestMatchers(HttpMethod.GET, "/error").permitAll()
//
//                it.requestMatchers(HttpMethod.POST, "/api/auth/sign-in", "/api/auth/id", "/api/auth/password").permitAll()
////                it.requestMatchers(HttpMethod.POST, "/api/auth/sign-up").permitAll()
//
//                it.requestMatchers(HttpMethod.GET, "/api/chat").authenticated()
//                it.requestMatchers(HttpMethod.POST, "/api/chat/file").authenticated()
//                it.requestMatchers(HttpMethod.POST, "/api/chat/room").authenticated()
//                it.requestMatchers(HttpMethod.GET, "/api/chat/room/club").authenticated()
//                it.requestMatchers(HttpMethod.GET, "/api/chat/room/user").authenticated()

                it.anyRequest().permitAll()
            }
            .with(FilterConfiguration(tokenProvider, objectMapper), Customizer.withDefaults())
            .build()
    }
}