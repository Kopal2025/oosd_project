package com.hotel.dao;

import com.hotel.model.Booking;
import com.hotel.model.Customer;
import com.hotel.model.Room;

import java.io.*;
import java.util.ArrayList;

// Week 6: Serialization and Deserialization
// This class handles ALL file operations
// Permanent storage - data survives after app closes
public class DataStore {

    // File names where data is saved on disk
    private static final String ROOMS_FILE     = "rooms.dat";
    private static final String CUSTOMERS_FILE = "customers.dat";
    private static final String BOOKINGS_FILE  = "bookings.dat";

    // ── SAVE methods (Serialization) ──────────────────

    public static void saveRooms(ArrayList<Room> rooms) {
        serialize(rooms, ROOMS_FILE);
    }

    public static void saveCustomers(ArrayList<Customer> customers) {
        serialize(customers, CUSTOMERS_FILE);
    }

    public static void saveBookings(ArrayList<Booking> bookings) {
        serialize(bookings, BOOKINGS_FILE);
    }

    // ── LOAD methods (Deserialization) ────────────────

    @SuppressWarnings("unchecked")
    public static ArrayList<Room> loadRooms() {
        Object obj = deserialize(ROOMS_FILE);
        return obj != null ? (ArrayList<Room>) obj : new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Customer> loadCustomers() {
        Object obj = deserialize(CUSTOMERS_FILE);
        return obj != null ? (ArrayList<Customer>) obj 
                           : new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Booking> loadBookings() {
        Object obj = deserialize(BOOKINGS_FILE);
        return obj != null ? (ArrayList<Booking>) obj 
                           : new ArrayList<>();
    }

    // ── Core serialization logic ──────────────────────

    // Converts object → bytes → saves to file
    private static void serialize(Object data, String filename) {
        try (ObjectOutputStream oos = 
                new ObjectOutputStream(
                    new FileOutputStream(filename))) {
            oos.writeObject(data);
            System.out.println("Saved: " + filename);
        } catch (IOException e) {
            System.out.println("Save error: " + e.getMessage());
        }
    }

    // Reads file → bytes → converts back to object
    private static Object deserialize(String filename) {
        File file = new File(filename);

        // If file doesn't exist yet, return null
        // (first time app runs, no data file exists)
        if (!file.exists()) return null;

        try (ObjectInputStream ois = 
                new ObjectInputStream(
                    new FileInputStream(file))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Load error: " + e.getMessage());
            return null;
        }
    }
}

