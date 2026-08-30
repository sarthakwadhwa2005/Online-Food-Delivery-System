package com.ofds.service;

import com.ofds.model.*;
import com.ofds.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UserService {

    @Autowired private UserRepository userRepository;
    @Autowired private CustomerRepository customerRepository;
    @Autowired private RestaurantOwnerRepository ownerRepository;
    @Autowired private DeliveryPartnerRepository deliveryPartnerRepository;
    @Autowired private AdminRepository adminRepository;
    @Autowired private PasswordEncoder passwordEncoder;

    @Transactional
    public Customer registerCustomer(String name, String email, String phone, String password, String address) {
        if (userRepository.existsByEmail(email)) return customerRepository.findByEmail(email).orElseThrow();
        Customer c = new Customer(name, email, phone, passwordEncoder.encode(password), address);
        return customerRepository.save(c);
    }

    @Transactional
    public RestaurantOwner registerOwner(String name, String email, String phone, String password) {
        if (userRepository.existsByEmail(email)) return ownerRepository.findByEmail(email).orElseThrow();
        RestaurantOwner o = new RestaurantOwner(name, email, phone, passwordEncoder.encode(password));
        return ownerRepository.save(o);
    }

    @Transactional
    public DeliveryPartner registerDeliveryPartner(String name, String email, String phone, String password, VehicleType vehicleType) {
        if (userRepository.existsByEmail(email)) return deliveryPartnerRepository.findByEmail(email).orElseThrow();
        DeliveryPartner dp = new DeliveryPartner(name, email, phone, passwordEncoder.encode(password), vehicleType);
        return deliveryPartnerRepository.save(dp);
    }

    @Transactional
    public String encodePassword(String raw) { return passwordEncoder.encode(raw); }

    @Transactional
    public Admin saveAdmin(Admin admin) { return adminRepository.save(admin); }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<Customer> findCustomerByEmail(String email) {
        return customerRepository.findByEmail(email);
    }

    public Optional<RestaurantOwner> findOwnerByEmail(String email) {
        return ownerRepository.findByEmail(email);
    }

    public Optional<DeliveryPartner> findDeliveryPartnerByEmail(String email) {
        return deliveryPartnerRepository.findByEmail(email);
    }

    public List<User> getAllUsers() { return userRepository.findAll(); }
    public List<DeliveryPartner> getAllDeliveryPartners() { return deliveryPartnerRepository.findAll(); }
    public List<DeliveryPartner> getAvailableDeliveryPartners() { return deliveryPartnerRepository.findByAvailabilityStatusTrue(); }

    @Transactional
    public void deleteUser(Long id) { userRepository.deleteById(id); }

    @Transactional
    public User updateProfile(Long userId, String name, String phone) {
        User u = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        u.updateProfile(name, phone);
        return userRepository.save(u);
    }
}
