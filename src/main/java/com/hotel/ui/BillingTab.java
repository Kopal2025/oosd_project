package com.hotel.ui;

import com.hotel.dao.HotelManager;
import com.hotel.model.Booking;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class BillingTab extends BorderPane {

    private HotelManager manager;
    private Label revenueLabel;
    private Label occupancyLabel;
    private TableView<Booking> table;

    public BillingTab(HotelManager manager) {
        this.manager = manager;
        setupUI();
    }

    private void setupUI() {
        getStyleClass().add("page-background");
        setPadding(new Insets(24));

        Label title = new Label("Billing & Reports");
        title.getStyleClass().add("page-title");

        revenueLabel = new Label();
        revenueLabel.getStyleClass().addAll("stat-number", "accent-indigo");

        occupancyLabel = new Label();
        occupancyLabel.getStyleClass().addAll("stat-number", "accent-purple");

        VBox statsBox = new VBox(10);
        statsBox.getStyleClass().add("card");

        Label revenueTitle = new Label("Total Revenue");
        revenueTitle.getStyleClass().add("muted-text");
        revenueTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: 900;");

        Label occupancyTitle = new Label("Occupancy Rate");
        occupancyTitle.getStyleClass().add("muted-text");
        occupancyTitle.setStyle("-fx-font-size: 12px; -fx-font-weight: 900;");

        statsBox.getChildren().addAll(revenueTitle, revenueLabel, occupancyTitle, occupancyLabel);

        table = new TableView<>();

        TableColumn<Booking, Integer> idCol = new TableColumn<>("Booking ID");
        idCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("bookingId"));

        TableColumn<Booking, String> customerCol = new TableColumn<>("Customer");
        customerCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getCustomer().getName()));

        TableColumn<Booking, String> roomCol = new TableColumn<>("Room");
        roomCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty("Room " + data.getValue().getRoom().getRoomNumber()));

        TableColumn<Booking, String> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(String.format("Rs %.2f", data.getValue().getGrandTotal())));

        table.getColumns().addAll(idCol, customerCol, roomCol, amountCol);

        // Zebra + active status highlight (UI-only)
        table.setRowFactory(t -> new TableRow<>() {
            @Override
            protected void updateItem(Booking item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("row-odd", "row-even", "row-available", "row-booked");
                if (empty || item == null) return;

                getStyleClass().add((getIndex() % 2 == 0) ? "row-even" : "row-odd");
                getStyleClass().add(item.isActive() ? "row-available" : "row-booked");
            }
        });

        Button refreshBtn = new Button("Refresh");
        refreshBtn.getStyleClass().add("btn-secondary");
        refreshBtn.setOnAction(e -> refresh());

        VBox left = new VBox(16, title, statsBox, refreshBtn);
        left.setPrefWidth(330);

        VBox tableCard = new VBox(table);
        tableCard.getStyleClass().add("card");
        tableCard.setFillWidth(true);
        VBox.setVgrow(table, Priority.ALWAYS);

        setLeft(left);
        setCenter(tableCard);
        
        refresh();
    }

    private void refresh() {
        table.setItems(FXCollections.observableArrayList(manager.getAllBookings()));
        revenueLabel.setText(String.format("Rs %.2f", manager.getTotalRevenue()));
        occupancyLabel.setText(String.format("%.2f%%", manager.getOccupancyRate()));
    }
}