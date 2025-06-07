package kr.ac.hansung.cse.hellospringdatajpa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authz -> authz
                        // 정적 리소스와 공개 페이지
                        .requestMatchers("/", "/helloSpringDataJpa/", "/register", "/login", "/css/**", "/js/**", "/images/**").permitAll()

                        // 상품 목록 조회: USER, ADMIN 모두 가능
                        .requestMatchers("/products", "/products/").hasAnyRole("USER", "ADMIN")

                        // 상품 상세 조회: USER, ADMIN 모두 가능 (GET 요청만)
                        .requestMatchers("/products/*/view").hasAnyRole("USER", "ADMIN")

                        // 상품 등록/수정/삭제: ADMIN만 가능
                        .requestMatchers("/products/new").hasRole("ADMIN")
                        .requestMatchers("/products/*/edit").hasRole("ADMIN")
                        .requestMatchers("/products/*/delete").hasRole("ADMIN")

                        // POST, PUT, DELETE 요청: ADMIN만 가능
                        .requestMatchers("POST", "/products").hasRole("ADMIN")
                        .requestMatchers("PUT", "/products/*").hasRole("ADMIN")
                        .requestMatchers("DELETE", "/products/*").hasRole("ADMIN")

                        // 관리자 페이지
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        // 나머지 모든 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/products")
                        .failureUrl("/login?error=true")
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )
                .sessionManagement(session -> session
                        .maximumSessions(1)
                        .maxSessionsPreventsLogin(false)
                )
                // CSRF 설정 (필요시)
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/products/*/delete") // DELETE 요청을 위해
                );

        return http.build();
    }
}