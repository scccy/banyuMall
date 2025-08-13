package com.origin.banyu.auth.config;

import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.cache.annotation.EnableCaching;
import com.origin.banyu.auth.util.JwtUtil;
import com.origin.banyu.auth.util.JwtTokenManager;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;

/**
 * Spring Security 配置类
 * 
 * @author origin
 * @since 2024-07-30
 */
@Configuration
@EnableWebSecurity
@EnableCaching
@RequiredArgsConstructor
public class SecurityConfig {

    @Value("${spring.security.user.name:admin}")
    private String username;

    @Value("${spring.security.user.password:123456}")
    private String password;

    @Value("${spring.security.user.roles:ADMIN}")
    private String roles;

    @Value("${security.permit-all:}")
    private List<String> permitAllPaths;

    private final JwtUtil jwtUtil;
    private final JwtTokenManager jwtTokenManager;

    // JwtAuthenticationFilter 改为静态类并构造注入依赖
    public static class JwtAuthenticationFilter extends OncePerRequestFilter {
        private final JwtUtil jwtUtil;
        private final JwtTokenManager jwtTokenManager;
        private final UserDetailsService userDetailsService;

        public JwtAuthenticationFilter(JwtUtil jwtUtil, JwtTokenManager jwtTokenManager, UserDetailsService userDetailsService) {
            this.jwtUtil = jwtUtil;
            this.jwtTokenManager = jwtTokenManager;
            this.userDetailsService = userDetailsService;
        }
        @Override
        protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
                throws ServletException, IOException {
            String tokenHeader = "Authorization";
            String token = request.getHeader(tokenHeader);
            if (token == null || token.isEmpty()) {
                token = request.getParameter("token");
            }
            if (token != null && !token.isEmpty()) {
                if (jwtTokenManager.isBlacklisted(token)) {
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    return;
                }
                if (jwtUtil.validateToken(token)) {
                    String username = jwtUtil.getUsernameFromToken(token);
                    UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            filterChain.doFilter(request, response);
        }
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

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter(JwtUtil jwtUtil, JwtTokenManager jwtTokenManager, UserDetailsService userDetailsService) {
        return new JwtAuthenticationFilter(jwtUtil, jwtTokenManager, userDetailsService);
    }

    /**
     * 安全过滤器链配置
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        // 打印permitAllPaths实际注入值
        System.out.println("[DEBUG] permitAllPaths = " + permitAllPaths);
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
                if (permitAllPaths != null && !permitAllPaths.isEmpty()) {
                    authz.requestMatchers(permitAllPaths.toArray(new String[0])).permitAll();
                }
                // 其他所有请求需要认证
                authz.anyRequest().authenticated();
            })
            // 注册 JWT 认证过滤器
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}