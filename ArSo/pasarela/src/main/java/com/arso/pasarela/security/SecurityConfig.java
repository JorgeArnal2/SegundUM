package com.arso.pasarela.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtValidationFilter jwtValidationFilter;
    private final OAuth2LoginSuccessHandler oauth2LoginSuccessHandler;
    private final String githubClientId;

    public SecurityConfig(JwtValidationFilter jwtValidationFilter,
                          OAuth2LoginSuccessHandler oauth2LoginSuccessHandler,
                          @Value("${GITHUB_CLIENT_ID:}") String githubClientId) {
        this.jwtValidationFilter = jwtValidationFilter;
        this.oauth2LoginSuccessHandler = oauth2LoginSuccessHandler;
        this.githubClientId = githubClientId;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/auth/**", "/oauth2/**", "/login/oauth2/**").permitAll()
            .anyRequest().permitAll()
            .and()
            .addFilterBefore(jwtValidationFilter, UsernamePasswordAuthenticationFilter.class);

        if (githubClientId != null && !githubClientId.isBlank()) {
            http.oauth2Login().successHandler(oauth2LoginSuccessHandler);
        }
    }
}
