package com.origin.auth.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Spring Security 配置类
 * 
 * @author origin
 * @since 2024-07-30
 */
@Configuration
@EnableWebSecurity
@EnableCaching
@ConfigurationProperties(prefix = "security")
public class SecurityConfig {

    @Value("${spring.security.user.name:admin}")
    private String username;

    @Value("${spring.security.user.password:123456}")
    private String password;

    @Value("${spring.security.user.roles:ADMIN}")
    private String roles;

    private final SecurityProperties securityProperties;

    public SecurityConfig(SecurityProperties securityProperties) {
        this.securityProperties = securityProperties;
    }

    /**
     * 密码加密器
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // 使用 BCrypt，强度为12，与数据库中的密码保持一致
    }

    /**
     * 用户详情服务 - 配置默认用户
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .roles(roles.split(","))
                .build();
        
        return new InMemoryUserDetailsManager(user);
    }

    /**
     * 安全过滤器链配置
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // 禁用 CSRF
            .csrf(AbstractHttpConfigurer::disable)
            // 禁用 Session 管理
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            // 禁用匿名认证
            .anonymous(AbstractHttpConfigurer::disable)
            // 配置请求授权
            .authorizeHttpRequests(authz -> {
                // 从配置中获取允许的路径
                List<String> permitAllPaths = securityProperties.getPermitAll();
                if (permitAllPaths != null && !permitAllPaths.isEmpty()) {
                    authz.requestMatchers(permitAllPaths.toArray(new String[0])).permitAll();
                }
                // 其他所有请求需要认证
                authz.anyRequest().authenticated();
            });

        return http.build();
    }

    /**
     * Security配置属性类
     */
    @Component
    @ConfigurationProperties(prefix = "security")
    public static class SecurityProperties {
        private List<String> permitAll;

        public List<String> getPermitAll() {
            return permitAll;
        }

        public void setPermitAll(List<String> permitAll) {
            this.permitAll = permitAll;
        }
    }
}