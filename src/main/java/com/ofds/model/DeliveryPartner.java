package com.ofds.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "delivery_partners")
@DiscriminatorValue("DELIVERY_PARTNER")
public class DeliveryPartner extends User {

    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    private boolean availabilityStatus = true;

    @OneToMany(mappedBy = "deliveryPartner", fetch = FetchType.LAZY)
    private List<Order> assignedOrders = new ArrayList<>();

    public DeliveryPartner() {}

    public DeliveryPartner(String name, String email, String phone, String password, VehicleType vehicleType) {
        super(name, email, phone, password);
        this.vehicleType = vehicleType;
    }

    public void viewDeliveryRequests() {}
    public void acceptDeliveryTask() {}
    public void pickUpOrder() {}
    public void deliverOrder() {}
    public void updateDeliveryStatus() {}

    public VehicleType getVehicleType() { return vehicleType; }
    public void setVehicleType(VehicleType vehicleType) { this.vehicleType = vehicleType; }
    public boolean isAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(boolean availabilityStatus) { this.availabilityStatus = availabilityStatus; }
    public List<Order> getAssignedOrders() { return assignedOrders; }
}
