package com.hotel.dao;

import com.hotel.model.*;
import java.time.LocalDate;
import java.util.*;

public class HotelManager {

    private ArrayList<Room> rooms;
    private ArrayList<Customer> customers;
    private ArrayList<Booking> bookings;
    private HashMap<Integer, Booking> activeBookings;

    public HotelManager() {
        rooms = DataStore.loadRooms();
        customers = DataStore.loadCustomers();
        bookings = DataStore.loadBookings();
        activeBookings = new HashMap<>();

        for (Booking b : bookings) {
            if (b.isActive()) {
                activeBookings.put(b.getRoom().getRoomNumber(), b);
            }
        }

        if (rooms.isEmpty()) {
            System.out.println("No rooms found. Please add rooms via Room Management tab.");
        }
    }

    public void addRoom(Room room) {
        rooms.add(room);
        saveAll();
    }

    public boolean roomExists(int roomNumber) {
        for (Room r : rooms) {
            if (r.getRoomNumber() == roomNumber) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<Room> getAllRooms() {
        return rooms;
    }

    public ArrayList<Room> getAvailableRooms() {
        ArrayList<Room> available = new ArrayList<>();
        Iterator<Room> it = rooms.iterator();
        while (it.hasNext()) {
            Room r = it.next();
            if (r.isAvailable()) {
                available.add(r);
            }
        }
        return available;
    }

    public Room findRoom(int roomNumber) {
        for (Room r : rooms) {
            if (r.getRoomNumber() == roomNumber) {
                return r;
            }
        }
        return null;
    }

    public void sortRoomsByPrice() {
        Collections.sort(rooms, Comparator.comparingDouble(Room::getPricePerNight));
    }

    public void sortRoomsByNumber() {
        Collections.sort(rooms, Comparator.comparingInt(Room::getRoomNumber));
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
        saveAll();
    }

    public ArrayList<Customer> getAllCustomers() {
        return customers;
    }

    public Customer findCustomerById(int id) {
        for (Customer c : customers) {
            if (c.getCustomerId() == id) return c;
        }
        return null;
    }

    public ArrayList<Customer> searchCustomers(String query) {
        ArrayList<Customer> results = new ArrayList<>();
        for (Customer c : customers) {
            if (c.getName().toLowerCase().contains(query.toLowerCase())) {
                results.add(c);
            }
        }
        return results;
    }

    public String bookRoom(int roomNumber, Customer customer, LocalDate checkIn, LocalDate checkOut) {
        Room room = findRoom(roomNumber);
        if (room == null) {
            return "ERROR:Room " + roomNumber + " not found.";
        }
        if (!room.isAvailable()) {
            return "ERROR:Room " + roomNumber + " is already booked.";
        }
        if (!checkOut.isAfter(checkIn)) {
            return "ERROR:Check-out must be after check-in.";
        }
        if (checkIn.equals(checkOut)) {
            return "ERROR:Minimum stay is 1 night.";
        }

        Booking booking = new Booking(customer, room, checkIn, checkOut);
        room.setAvailable(false);
        bookings.add(booking);
        activeBookings.put(roomNumber, booking);
        saveAll();

        return "SUCCESS:" + booking.generateInvoice();
    }

    public String checkout(int roomNumber) {
        if (!activeBookings.containsKey(roomNumber)) {
            return "ERROR:No active booking for room " + roomNumber;
        }

        Booking booking = activeBookings.get(roomNumber);
        booking.setActive(false);
        booking.getRoom().setAvailable(true);
        activeBookings.remove(roomNumber);
        saveAll();

        return "SUCCESS:" + booking.generateInvoice();
    }

    public ArrayList<Booking> getAllBookings() {
        return bookings;
    }

    public ArrayList<Booking> getActiveBookings() {
        ArrayList<Booking> active = new ArrayList<>();
        for (Booking b : bookings) {
            if (b.isActive()) active.add(b);
        }
        return active;
    }

    public Booking getActiveBooking(int roomNumber) {
        return activeBookings.get(roomNumber);
    }

    public int getTotalRooms() {
        return rooms.size();
    }

    public int getAvailableRoomsCount() {
        return getAvailableRooms().size();
    }

    public int getBookedRoomsCount() {
        return getTotalRooms() - getAvailableRoomsCount();
    }

    public double getOccupancyRate() {
        if (rooms.isEmpty()) return 0;
        return ((double) getBookedRoomsCount() / getTotalRooms()) * 100;
    }

    public double getTotalRevenue() {
        double total = 0;
        for (Booking b : bookings) {
            total += b.getGrandTotal();
        }
        return total;
    }

    public HashMap<String, Double> getRevenueByRoomType() {
        HashMap<String, Double> revenueMap = new HashMap<>();
        for (Booking b : bookings) {
            String type = b.getRoom().getRoomType().getDisplayName();
            revenueMap.put(type, revenueMap.getOrDefault(type, 0.0) + b.getGrandTotal());
        }
        return revenueMap;
    }

    public String getMostBookedRoomType() {
        HashMap<String, Integer> countMap = new HashMap<>();
        for (Booking b : bookings) {
            String type = b.getRoom().getRoomType().getDisplayName();
            countMap.put(type, countMap.getOrDefault(type, 0) + 1);
        }
        if (countMap.isEmpty()) return "No bookings yet";
        return Collections.max(countMap.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    private void saveAll() {
        DataStore.saveRooms(rooms);
        DataStore.saveCustomers(customers);
        DataStore.saveBookings(bookings);
    }
}