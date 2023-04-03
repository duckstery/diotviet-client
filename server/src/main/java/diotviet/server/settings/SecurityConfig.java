package diotviet.server.settings;

import diotviet.server.filters.AuthTokenFilter;
import diotviet.server.services.UserService;
import diotviet.server.exceptions.AuthEntryPointJwt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity //(securedEnabled = true, jsr250Enabled = true, prePostEnabled = true)
public class SecurityConfig {

    // ****************************
    // Properties
    // ****************************

    /**
     * User service. This service will be used to load User data for performing authentication
     */
    @Autowired
    UserService userService;
    /**
     * Handle unauthorized request
     */
    @Autowired
    private AuthEntryPointJwt unauthorizedHandler;

    // ****************************
    // Public
    // ****************************

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter() {
        return new AuthTokenFilter();
    }

//    /**
//     * Export WebCustomizer to exclude route with this prefix to be authenticated
//     *
//     * @return
//     */
//    @Bean
//    public WebSecurityCustomizer webSecurityCustomizer() {
//        return (web) -> web.ignoring().requestMatchers("/js/**", "/images/**");
//    }

    /**
     * AuthenticationProvider bean. Seam like it'll like Laravel User Provider
     *
     * @return
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        // Create Provider
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }

    /**
     * Return AuthenticationManager from AuthenticationConfiguration
     *
     * @param authConfiguration
     * @return
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfiguration) throws Exception {
        return authConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Request will be filtered through this chain (authorization process)<br>
     * <li>1. This logic will tell Spring Security how we configure CORS and CSRF</li>
     * <li>2. After that, it'll tell to authenticate or not based on AuthTokenFilter result</li>
     * <li>3. Finally, if AuthTokenFilter is failed, it'll tell to authenticate user by using Username... filter</li>
     *
     * @param http
     * @return
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors().disable()
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(unauthorizedHandler).and()
                .authenticationProvider(authenticationProvider())
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeHttpRequests()
                .requestMatchers("/api/fallback/**").permitAll()
                .requestMatchers("/api/v1/**", "/api/v2/**", "api/auth/logout").authenticated()
                .anyRequest().permitAll();

        // Set authentication token filter
        // Look like request will be filtered by JWT Token, if failed, proceed to filter by username and password
        http.addFilterBefore(authenticationJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);

        // Set AuthenticationProvider
        http.authenticationProvider(authenticationProvider());

        return http.build();
    }
}
