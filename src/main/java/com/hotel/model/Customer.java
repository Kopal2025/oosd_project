package com.hotel.model;

import java.io.Serializable;

public class Customer implements Serializable {
    private static final long serialVersionUID = 2L;

    private static int idCounter = 100;

    private int customerId;
    private String name;
    private String phone;
    private String email;
    private String address;

    public Customer(String name, String phone, String email, String address) {
        this.customerId = ++idCounter;
        this.name = name;
        this.phone = phone;
        this.email = email;
        this.address = address;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }

    public String getEmail() {
        return email;
    }

    public String getAddress() {
        return address;
    }

    public void setName(String name) {
        if (name != null && !name.isBlank()) {
            this.name = name;
        }
    }

    public void setPhone(String phone) {
        if (phone != null && phone.length() == 10) {
            this.phone = phone;
        }
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setAddress(String address) {
        this.address = address;
    }

   public static void setCounter(int value) {
    idCounter = value;
}

    @Override
    public String toString() {
        return "[" + customerId + "] " + name + " | " + phone;
    }
}

