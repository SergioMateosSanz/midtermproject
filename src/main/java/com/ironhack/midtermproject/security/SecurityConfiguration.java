package com.ironhack.midtermproject.security;

import com.ironhack.midtermproject.classes.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
            .userDetailsService(userDetailsService)
            .passwordEncoder(passwordEncoder);

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.httpBasic();
        http.csrf().disable();
        http.authorizeRequests()
                .mvcMatchers(HttpMethod.POST, "/users").hasRole("ADMIN")
                .mvcMatchers(HttpMethod.DELETE,"/users/**").hasRole("ADMIN")
                .mvcMatchers(HttpMethod.PATCH,"/addresses/**").hasRole("ADMIN")
                .mvcMatchers(HttpMethod.GET,"/accounts").hasAnyRole("ADMIN", "HOLDER")
                .mvcMatchers(HttpMethod.POST,"/accounts/savings").hasRole("ADMIN")
                .mvcMatchers(HttpMethod.POST,"/accounts/credits").hasRole("ADMIN")
                .mvcMatchers(HttpMethod.POST,"/accounts/checkings").hasRole("ADMIN")
                .mvcMatchers(HttpMethod.GET,"/accounts/savings").hasRole("HOLDER")
                .mvcMatchers(HttpMethod.GET,"/accounts/checkings").hasRole("HOLDER")
                .mvcMatchers(HttpMethod.GET,"/accounts/students").hasRole("HOLDER")
                .mvcMatchers(HttpMethod.GET,"/accounts/credits").hasRole("HOLDER")
                .mvcMatchers(HttpMethod.GET,"/accounts/checkings/**").hasRole("HOLDER")
                .mvcMatchers(HttpMethod.GET,"/accounts/savings/**").hasRole("HOLDER")
                .mvcMatchers(HttpMethod.GET,"/accounts/students/**").hasRole("HOLDER")
                .mvcMatchers(HttpMethod.GET,"/accounts/credits/**").hasRole("HOLDER")
                .mvcMatchers(HttpMethod.PATCH,"/accounts/**").hasRole("ADMIN")
                .mvcMatchers(HttpMethod.POST,"/accounts/students/**/movements").hasRole("HOLDER")
                .mvcMatchers(HttpMethod.POST,"/accounts/credits/**/movements").hasRole("HOLDER")
                .mvcMatchers(HttpMethod.POST,"/accounts/checkings/**/movements").hasRole("HOLDER")
                .mvcMatchers(HttpMethod.POST,"/accounts/savings/**/movements").hasRole("HOLDER")
                .mvcMatchers(HttpMethod.GET,"/accounts/students/**/movements").hasRole("HOLDER")
                .mvcMatchers(HttpMethod.GET,"/accounts/credits/**/movements").hasRole("HOLDER")
                .mvcMatchers(HttpMethod.GET,"/accounts/checkings/**/movements").hasRole("HOLDER")
                .mvcMatchers(HttpMethod.GET,"/accounts/savings/**/movements").hasRole("HOLDER")
                .mvcMatchers(HttpMethod.POST,"/thirdparty/sendmoney").hasRole("THIRD PARTY")
                .mvcMatchers(HttpMethod.POST,"/thirdparty/receivemoney").hasRole("THIRD PARTY")
                .anyRequest().permitAll();

    }
}
