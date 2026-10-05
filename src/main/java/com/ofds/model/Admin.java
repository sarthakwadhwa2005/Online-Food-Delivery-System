package com.ofds.model;

import jakarta.persistence.*;

@Entity
@Table(name = "admins")
@DiscriminatorValue("ADMIN")
public class Admin extends User {

    private int adminLevel;
    private String department;

    public Admin() {}

    public Admin(String name, String email, String phone, String password, int adminLevel, String department) {
        super(name, email, phone, password);
        this.adminLevel = adminLevel;
        this.department = department;
    }

    public void manageUsers() {}
    public void manageRestaurants() {}
    public void assignDeliveryPartner() {}
    public void monitorOrders() {}
    public void generateReports() {}

    public int getAdminLevel() { return adminLevel; }
    public void setAdminLevel(int adminLevel) { this.adminLevel = adminLevel; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
