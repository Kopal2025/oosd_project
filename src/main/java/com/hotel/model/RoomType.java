package com.hotel.model;

public enum RoomType{
    SINGLE("Single Room", 1500),
    DOUBLE("Double Room", 2500),
    DELUXE("Deluxe Room", 4000),
    SUITE("Suite", 7000);

    private final String displayName;
    private final double pricePerNight;

    RoomType(String displayName , double pricePerNight){
        this.displayName = displayName;
        this.pricePerNight = pricePerNight;
    }

    public String getDisplayName(){
        return displayName;
    }

    public double getPricePerNight(){
        return pricePerNight;
    }

    public double calculateCost(int nights){
        return pricePerNight*nights;
    }

   @Override
public String toString(){
    return displayName;
}
}