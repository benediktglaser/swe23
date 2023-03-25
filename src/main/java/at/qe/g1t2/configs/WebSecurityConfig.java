package at.qe.g1t2.configs;

import javax.sql.DataSource;

import at.qe.g1t2.model.UserRole;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;

/**
 * Spring configuration for web security.
 *
 * This class is part of the skeleton project provided for students of the
 * course "Software Engineering" offered by the University of Innsbruck.
 */
@Configuration
@EnableWebSecurity()
public class WebSecurityConfig {

    private static final String ADMIN = UserRole.ADMIN.name();
    private static final String USER = UserRole.USER.name();
    private static final String GARDENER = UserRole.GARDENER.name();

    @Autowired
    DataSource dataSource;


    

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        try {
            http.csrf().disable();
            http.headers().frameOptions().disable(); // needed for H2 console

            http.authorizeHttpRequests(authorize -> authorize.requestMatchers("/api/**").authenticated()).httpBasic()
                    .and()
                .authorizeHttpRequests(authorize -> authorize

                .requestMatchers("/").permitAll()
                .requestMatchers("/**.jsf").permitAll()
                .requestMatchers(antMatcher("/h2-console/**")).permitAll()
                .requestMatchers("/jakarta.faces.resource/**").permitAll()
                .requestMatchers("/error/**").permitAll()
                .requestMatchers("/admin/**").hasAnyAuthority(ADMIN)
                .requestMatchers("/secured/**").hasAnyAuthority(ADMIN, GARDENER, USER)
                        .requestMatchers("/adminGardener/**").hasAnyAuthority(ADMIN,GARDENER)
                .requestMatchers("/omnifaces.push/**").hasAnyAuthority(ADMIN, GARDENER, USER)
                    .anyRequest().authenticated())
                    .formLogin()
                    .loginPage("/login.xhtml")
                    .permitAll()
                    .failureUrl("/error/access_denied.xhtml")
                    .defaultSuccessUrl("/secured/welcome.xhtml")
                .loginProcessingUrl("/login")
                    .successHandler(successHandler())
                    .and()
                    .logout()
                    .logoutSuccessUrl("/login.xhtml")
                    .deleteCookies("JSESSIONID");
            
//            http.exceptionHandling().accessDeniedPage("/error/access_denied.xhtml");
            http.sessionManagement().invalidSessionUrl("/error/invalid_session.xhtml");
			return http.build();
            }
            catch (Exception ex) {
                    throw new BeanCreationException("Wrong spring security configuration", ex);
            }
                
        // :TODO: user failureUrl(/login.xhtml?error) and make sure that a corresponding message is displayed

    }

    private AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            for (GrantedAuthority grantedAuthority : authorities) {
                // Todo replace equals with contains
                if ((grantedAuthority.getAuthority().equals("ADMIN")
                        && grantedAuthority.getAuthority().equals("GARDENER"))
                        || grantedAuthority.getAuthority().equals("GARDENER")       ) {
                    response.sendRedirect("/adminGardener/sensorStations/sensorStations.xhtml");
                    return;
                }
                if((grantedAuthority.getAuthority().equals("ADMIN"))){
                    response.sendRedirect("/adminGardener/sensorStations/sensorStations.xhtml");
                    return;
                }
            }
            response.sendRedirect("/secured/welcome.xhtml");
        };
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        //Configure roles and passwords via datasource
        auth.jdbcAuthentication().dataSource(dataSource)
                .usersByUsernameQuery("select username, password, enabled from userx where username=?")
                .authoritiesByUsernameQuery("select userx_username, roles from userx_user_role where userx_username=?")
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //test
}