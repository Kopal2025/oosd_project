package com.hotel.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Booking implements Serializable {
    private static final long serialVersionUID = 3L;

    private static int bookingCounter = 1000;

    private int bookingId;
    private Customer customer;
    private Room room;
    private LocalDate checkIn;
    private LocalDate checkOut;
    private boolean active;

    public Booking(Customer customer, Room room, LocalDate checkIn, LocalDate checkOut) {
        this.bookingId = ++bookingCounter;
        this.customer = customer;
        this.room = room;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.active = true;
    }

    public long getNights() {
        return ChronoUnit.DAYS.between(checkIn, checkOut);
    }

    public double getRoomCharges() {
        return room.getPricePerNight() * getNights();
    }

    public double getGST() {
        return getRoomCharges() * 0.12;
    }

    public double getGrandTotal() {
        return getRoomCharges() + getGST();
    }

public String generateInvoice() {
    return String.format(
        "----- HOTEL INVOICE -----\n\n" +
        "Booking ID   : #%d\n" +
        "Status       : %s\n\n" +

        "GUEST DETAILS\n" +
        "--------------------------\n" +
        "Name         : %s\n" +
        "Phone        : %s\n" +
        "Email        : %s\n\n" +

        "ROOM DETAILS\n" +
        "--------------------------\n" +
        "Room Number  : %d\n" +
        "Room Type    : %s\n" +
        "Price/Night  : ₹%.2f\n\n" +

        "STAY DETAILS\n" +
        "--------------------------\n" +
        "Check-in     : %s\n" +
        "Check-out    : %s\n" +
        "Nights       : %d\n\n" +

        "BILLING\n" +
        "--------------------------\n" +
        "Room Charges : ₹%.2f\n" +
        "GST (12%%)   : ₹%.2f\n" +
        "--------------------------\n" +
        "TOTAL        : ₹%.2f\n",

        bookingId,
        active ? "ACTIVE" : "CHECKED OUT",
        customer.getName(),
        customer.getPhone(),
        customer.getEmail(),
        room.getRoomNumber(),
        room.getRoomType().getDisplayName(),
        room.getPricePerNight(),
        checkIn, checkOut, getNights(),
        getRoomCharges(), getGST(), getGrandTotal()
    );
}

    public int getBookingId() {
        return bookingId;
    }

    public Customer getCustomer() {
        return customer;
    }

    public Room getRoom() {
        return room;
    }

    public LocalDate getCheckIn() {
        return checkIn;
    }

    public LocalDate getCheckOut() {
        return checkOut;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        return "Booking #" + bookingId
                + " | " + customer.getName()
                + " | Room " + room.getRoomNumber()
                + " | ₹" + getGrandTotal();
    }
}

