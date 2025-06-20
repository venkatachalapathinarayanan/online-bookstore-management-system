package com.bookstore.ordermanagement.config

import com.bookstore.ordermanagement.security.JwtAuthenticationFilter
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

@Configuration
@EnableWebSecurity
class SecurityConfig {

    @Autowired
    private lateinit var jwtAuthenticationFilter: JwtAuthenticationFilter

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http.authorizeHttpRequests { requests ->
            requests
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/cart/**").hasAnyAuthority("USERS", "ADMIN", "SUPERADMIN")
                .requestMatchers("/orders/**").hasAnyAuthority("USERS", "ADMIN", "SUPERADMIN")
                .anyRequest().hasAnyAuthority("ADMIN", "SUPERADMIN")
        }
        http.csrf { it.disable() }
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
        return http.build()
    }
}

