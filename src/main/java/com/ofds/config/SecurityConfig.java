package com.ofds.config;

import com.ofds.model.*;
import com.ofds.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired private CustomerRepository customerRepository;
    @Autowired private RestaurantOwnerRepository ownerRepository;
    @Autowired private DeliveryPartnerRepository deliveryPartnerRepository;
    @Autowired private AdminRepository adminRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/register", "/login", "/css/**", "/js/**").permitAll()
                .requestMatchers("/customer/**").hasRole("CUSTOMER")
                .requestMatchers("/owner/**").hasRole("RESTAURANT_OWNER")
                .requestMatchers("/delivery/**").hasRole("DELIVERY_PARTNER")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/payment/**").hasAnyRole("CUSTOMER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .successHandler((req, res, auth) -> {
                    String role = auth.getAuthorities().iterator().next().getAuthority();
                    switch (role) {
                        case "ROLE_CUSTOMER"         -> res.sendRedirect("/customer/dashboard");
                        case "ROLE_RESTAURANT_OWNER" -> res.sendRedirect("/owner/dashboard");
                        case "ROLE_DELIVERY_PARTNER" -> res.sendRedirect("/delivery/dashboard");
                        case "ROLE_ADMIN"            -> res.sendRedirect("/admin/dashboard");
                        default                      -> res.sendRedirect("/login?error=true");
                    }
                })
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .permitAll()
            );
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            // Check each concrete repository in order — avoids discriminator null issues
            var customer = customerRepository.findByEmail(email);
            if (customer.isPresent()) {
                return build(customer.get().getEmail(), customer.get().getPassword(), "ROLE_CUSTOMER");
            }
            var owner = ownerRepository.findByEmail(email);
            if (owner.isPresent()) {
                return build(owner.get().getEmail(), owner.get().getPassword(), "ROLE_RESTAURANT_OWNER");
            }
            var partner = deliveryPartnerRepository.findByEmail(email);
            if (partner.isPresent()) {
                return build(partner.get().getEmail(), partner.get().getPassword(), "ROLE_DELIVERY_PARTNER");
            }
            var admin = adminRepository.findByEmail(email);
            if (admin.isPresent()) {
                return build(admin.get().getEmail(), admin.get().getPassword(), "ROLE_ADMIN");
            }
            throw new UsernameNotFoundException("No user found with email: " + email);
        };
    }

    private UserDetails build(String email, String password, String role) {
        return new org.springframework.security.core.userdetails.User(
                email, password, List.of(new SimpleGrantedAuthority(role)));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
