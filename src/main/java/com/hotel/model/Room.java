package com.hotel.model;
import java.io.Serializable;

public class Room implements Serializable{
    private static final long serialVersionUID = 1L;

    private int roomNumber;
    private RoomType roomType;
    private double pricePerNight;
    private boolean available;
    private String description;

    public Room(int roomNumber , RoomType roomType){
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = roomType.getPricePerNight();
        this.available = true;
        this.description = roomType.getDisplayName();
    }

    public Room(int roomNumber , RoomType roomType , double customPrice){
        this.roomNumber = roomNumber;
        this.roomType = roomType;
        this.pricePerNight = customPrice;
        this.available = true;
        this.description = roomType.getDisplayName();
    }

    public int getRoomNumber(){
        return roomNumber;
    }

    public RoomType getRoomType(){
        return roomType;
    }

    public double getPricePerNight(){
        return pricePerNight;
    }

    public boolean isAvailable(){
        return available;
    }

    public String getDescription(){
        return description;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public void setPricePerNight(double price) {
        if (price > 0) {
            this.pricePerNight = price;
        }
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return available ? "Available" : "Booked";
    }

    @Override
    public String toString() {
        return "Room " + roomNumber + " | "
                + roomType.getDisplayName()
                + " | Rs" + pricePerNight
                + " | " + getStatus();
    }
}


