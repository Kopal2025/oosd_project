package com.hotel.ui;

import com.hotel.dao.HotelManager;
import com.hotel.model.Booking;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.chart.PieChart;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import com.hotel.model.Room;

public class DashboardTab extends BorderPane {

    private final HotelManager manager;
    private Label revenueLabel;
    private Timeline refreshTimer;

    public DashboardTab(HotelManager manager) {
        this.manager = manager;

        setCenter(buildUI()); // 🚨 ONLY UI ATTACH HERE
    }

    // 🔥 YOUR FULL UI (FIXED STRUCTURE)
    private VBox buildUI() {

        VBox root = new VBox(20);
        root.setPadding(new Insets(24));
        root.getStyleClass().add("page-background");

        Label title = new Label("Hotel Dashboard");
        title.getStyleClass().add("page-title");

        Label subtitle = new Label("Real-time hotel performance metrics");
        subtitle.getStyleClass().add("page-subtitle");

        // ── Stats Row ───────────────────────
        HBox statsRow = new HBox(16);
        statsRow.setAlignment(Pos.CENTER_LEFT);

        VBox totalCard = makeStatCard("🏢",
                String.valueOf(manager.getTotalRooms()),
                "Total Rooms", "accent-indigo");

        VBox availCard = makeStatCard("🟢",
                String.valueOf(manager.getAvailableRoomsCount()),
                "Available", "accent-green");

        VBox bookedCard = makeStatCard("🔴",
                String.valueOf(manager.getBookedRoomsCount()),
                "Booked", "accent-red");

        revenueLabel = new Label(String.format("₹%.0f", manager.getTotalRevenue()));
        VBox revenueCard = makeDynamicStatCard("💰",
                revenueLabel, "Total Revenue", "accent-purple");

        VBox bookingsCard = makeStatCard("📋",
                String.valueOf(manager.getAllBookings().size()),
                "Total Bookings", "accent-indigo");

        statsRow.getChildren().addAll(
                totalCard, availCard, bookedCard, revenueCard, bookingsCard
        );

        // ── Occupancy ───────────────────────
        VBox occupancyBox = createOccupancyCard();

        // ── Charts ──────────────────────────
        HBox chartsRow = new HBox(20);

        PieChart pieChart = createRoomTypeChart();
        VBox revenueBreakdown = createRevenueBreakdown();

        chartsRow.getChildren().addAll(pieChart, revenueBreakdown);
        HBox.setHgrow(pieChart, Priority.ALWAYS);
        HBox.setHgrow(revenueBreakdown, Priority.ALWAYS);

        // ── Recent Bookings ─────────────────
        VBox recentBox = createRecentBookingsBox();

        setupAutoRefresh(revenueLabel, occupancyBox);

        root.getChildren().addAll(
                new VBox(5, title, subtitle),
                statsRow,
                occupancyBox,
                chartsRow,
                recentBox
        );

        return root;
    }

    // ── CARDS ─────────────────────────────

    private VBox makeStatCard(String icon, String number, String label, String accentClass) {
        VBox card = new VBox(8);
        card.getStyleClass().addAll("card", "stat-card");

        Label iconLabel = new Label(icon);
        Label numLabel = new Label(number);
        numLabel.getStyleClass().add(accentClass);

        Label labelText = new Label(label);

        card.getChildren().addAll(iconLabel, numLabel, labelText);
        return card;
    }

    private VBox makeDynamicStatCard(String icon, Label dynamicLabel, String label, String accentClass) {
        VBox card = new VBox(8);
        card.getStyleClass().addAll("card", "stat-card");

        Label iconLabel = new Label(icon);
        dynamicLabel.getStyleClass().add(accentClass);

        Label labelText = new Label(label);

        card.getChildren().addAll(iconLabel, dynamicLabel, labelText);
        return card;
    }

    private VBox createOccupancyCard() {
        VBox card = new VBox(12);

        double occupancy = manager.getOccupancyRate();

        Label percentLabel = new Label(String.format("%.1f%%", occupancy));
        ProgressBar progressBar = new ProgressBar(occupancy / 100.0);

        card.getChildren().addAll(percentLabel, progressBar);
        return card;
    }

private PieChart createRoomTypeChart() {
    PieChart chart = new PieChart();
    chart.setTitle("Room Type Distribution");
    
    // FIX: Count rooms by type correctly
    java.util.Map<String, Long> typeCount = new java.util.HashMap<>();
    for (Room r : manager.getAllRooms()) {
        String type = r.getRoomType().getDisplayName();
        typeCount.put(type, typeCount.getOrDefault(type, 0L) + 1);
    }
    
    for (java.util.Map.Entry<String, Long> entry : typeCount.entrySet()) {
        PieChart.Data slice = new PieChart.Data(
            entry.getKey() + " (" + entry.getValue() + ")", 
            entry.getValue()
        );
        chart.getData().add(slice);
    }
    
    return chart;
}

private VBox createRevenueBreakdown() {
    VBox box = new VBox(10);
    box.setStyle("-fx-padding: 15; -fx-background-color: white; -fx-background-radius: 10;");
    
    Label title = new Label("Revenue by Room Type");
    title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
    box.getChildren().add(title);
    
    java.util.HashMap<String, Double> revenueMap = manager.getRevenueByRoomType();
    
    if (revenueMap.isEmpty()) {
        Label empty = new Label("No revenue data yet");
        empty.setStyle("-fx-text-fill: #94a3b8;");
        box.getChildren().add(empty);
        return box;
    }
    
    // Sort by revenue (highest first)
    java.util.List<java.util.Map.Entry<String, Double>> list = 
        new java.util.ArrayList<>(revenueMap.entrySet());
    list.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
    
    for (java.util.Map.Entry<String, Double> entry : list) {
        HBox row = new HBox(10);
        row.setStyle("-fx-padding: 5 0;");
        
        Label typeLabel = new Label(entry.getKey());
        typeLabel.setStyle("-fx-text-fill: #475569;");
        HBox.setHgrow(typeLabel, Priority.ALWAYS);
        
        Label amountLabel = new Label(String.format("Rs %.0f", entry.getValue()));
        amountLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #4f46e5;");
        
        row.getChildren().addAll(typeLabel, amountLabel);
        box.getChildren().add(row);
    }
    
    return box;
}

private VBox createRecentBookingsBox() {
    VBox box = new VBox(10);
    box.setStyle("-fx-padding: 15; -fx-background-color: white; -fx-background-radius: 10;");
    
    Label title = new Label("Recent Bookings");
    title.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
    box.getChildren().add(title);
    
    java.util.ArrayList<Booking> allBookings = manager.getAllBookings();
    int start = Math.max(0, allBookings.size() - 5);
    
    if (allBookings.isEmpty()) {
        Label empty = new Label("No bookings yet");
        empty.setStyle("-fx-text-fill: #94a3b8;");
        box.getChildren().add(empty);
        return box;
    }
    
    for (int i = allBookings.size() - 1; i >= start; i--) {
        Booking b = allBookings.get(i);
        HBox item = new HBox(10);
        item.setStyle("-fx-padding: 8 0; -fx-border-color: #f1f5f9; -fx-border-width: 0 0 1 0;");
        
        Label name = new Label(b.getCustomer().getName());
        name.setStyle("-fx-font-weight: bold; -fx-text-fill: #1e293b;");
        
        Label room = new Label("Room " + b.getRoom().getRoomNumber());
        room.setStyle("-fx-text-fill: #64748b;");
        
        Label amount = new Label(String.format("Rs %.0f", b.getGrandTotal()));
        amount.setStyle("-fx-font-weight: bold; -fx-text-fill: #4f46e5;");
        
        HBox.setHgrow(name, Priority.ALWAYS);
        item.getChildren().addAll(name, room, amount);
        box.getChildren().add(item);
    }
    
    return box;
}

    private void setupAutoRefresh(Label revenueLabel, VBox occupancyBox) {
        refreshTimer = new Timeline(new KeyFrame(Duration.seconds(10), e -> {
            revenueLabel.setText(
                    String.format("₹%.0f", manager.getTotalRevenue())
            );
        }));
        refreshTimer.setCycleCount(Timeline.INDEFINITE);
        refreshTimer.play();
    }
}