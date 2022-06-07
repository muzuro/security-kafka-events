package com.mzr.sm.security

import com.mzr.sm.security.service.ServiceAuthenticationProvider
import com.mzr.sm.security.service.ServiceTokenAuthenticationFilter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.BeanIds
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter
import org.springframework.security.provisioning.InMemoryUserDetailsManager
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
class WebSecurityConfig(
    private val passwordEncoder: PasswordEncoder,
    private val serviceAuthenticationProvider: ServiceAuthenticationProvider,
) : WebSecurityConfigurerAdapter() {

    @Bean(name = [BeanIds.AUTHENTICATION_MANAGER])
    override fun authenticationManagerBean(): AuthenticationManager {
        return super.authenticationManagerBean()
    }

    override fun configure(builder: AuthenticationManagerBuilder) {
        builder.authenticationProvider(serviceAuthenticationProvider)
    }

    override fun configure(http: HttpSecurity) {

        http
            .addFilterAfter(
                ServiceTokenAuthenticationFilter(serviceAuthenticationProvider),
                BasicAuthenticationFilter::class.java)
            .cors()
            .and()
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests{ configurer ->
                    configurer
                        .antMatchers(
                            "/error",
                            "/login"
                        )
                        .permitAll()
                        .anyRequest()
                        .authenticated()
                }
            .oauth2ResourceServer { obj: OAuth2ResourceServerConfigurer<HttpSecurity?> -> obj
                .jwt()
                .jwtAuthenticationConverter(jwtAuthenticationConverter())
            }
    }

    private fun jwtAuthenticationConverter(): JwtAuthenticationConverter? {
        // create a custom JWT converter to map the "authorities" from the token as granted authorities
        val jwtGrantedAuthoritiesConverter = JwtGrantedAuthoritiesConverter()
        jwtGrantedAuthoritiesConverter.setAuthoritiesClaimName("authorities")
        jwtGrantedAuthoritiesConverter.setAuthorityPrefix("")
        val jwtAuthenticationConverter = JwtAuthenticationConverter()
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(jwtGrantedAuthoritiesConverter)
        return jwtAuthenticationConverter
    }

    @Bean
    override fun userDetailsService(): UserDetailsService {
        val user1 = User
            .withUsername("user@mail.com")
            .authorities("email.update")
            .passwordEncoder { rawPassword: String? -> passwordEncoder.encode(rawPassword) }
            .password("1234")
            .build()
        val userDetailsManager = InMemoryUserDetailsManager()
        userDetailsManager.createUser(user1)
        return userDetailsManager
    }
}