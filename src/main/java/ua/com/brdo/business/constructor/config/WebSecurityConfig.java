package ua.com.brdo.business.constructor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.sql.DataSource;

@Configuration
@EnableScheduling
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private AuthenticationSuccessHandler authenticationSuccessHandler;

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    DataSource dataSource;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/api/users/register/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/users").permitAll()
                .antMatchers(HttpMethod.GET, "/api/users/available**").permitAll()
                .antMatchers(HttpMethod.GET, "/api/questions/**", "/api/options/**", "/api/permits/**", "/api/procedures/**")
                .permitAll()
                .antMatchers(HttpMethod.POST, "/api/options/**", "/api/permits/**", "/api/procedures/**")
                .hasAnyRole("USER", "EXPERT")
                .antMatchers(HttpMethod.POST, "/api/questions/**", "/api/business-types/**")
                .hasAnyRole("EXPERT")
                .antMatchers(HttpMethod.GET, "/api/questionnaires/**", "/api/business-types/**")
                .permitAll()
                .antMatchers(HttpMethod.POST, "/api/questionnaires/**")
                .hasAnyRole("EXPERT")
                .antMatchers(HttpMethod.GET, "/api/procedures").hasAnyRole("USER", "EXPERT", "ADMIN")
                .antMatchers("/api/questions/**", "/api/options/**").hasRole("EXPERT")
                .antMatchers("/api/questionnaires/**", "/api/business-types/**").hasAnyRole("EXPERT")
                .antMatchers("/api/business/**", "/api/businesses/**").hasRole("USER")
                .antMatchers(HttpMethod.GET, "/api/laws/**").permitAll()
                .antMatchers("/api/laws/**").hasAnyRole("ADMIN", "EXPERT")
                .antMatchers("/api/**").hasAnyRole("ADMIN", "EXPERT")
                .antMatchers("/user/**").hasRole("USER")
                .antMatchers("/expert/**").hasRole("EXPERT")
                .antMatchers("/admin/**").hasAnyRole("ADMIN", "EXPERT")
                .and()
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(new Http403ForbiddenEntryPoint())
                .and()
                .formLogin().successHandler(authenticationSuccessHandler)
                .failureHandler(new SimpleUrlAuthenticationFailureHandler())
                .and()
                .rememberMe().rememberMeParameter("rememberme")
                .tokenRepository(persistentTokenRepository()).tokenValiditySeconds(864000)
                .and()
                .logout().logoutUrl("/api/logout")
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler(HttpStatus.OK));
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public PersistentTokenRepository persistentTokenRepository() {
        JdbcTokenRepositoryImpl tokenRepositoryImpl = new JdbcTokenRepositoryImpl();
        tokenRepositoryImpl.setDataSource(dataSource);
        return tokenRepositoryImpl;
    }
}
