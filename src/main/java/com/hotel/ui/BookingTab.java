package com.hotel.ui;

import com.hotel.dao.HotelManager;
import com.hotel.model.Customer;
import com.hotel.model.Room;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;

public class BookingTab extends VBox {

    private HotelManager manager;
    private ComboBox<Room> roomBox;
    private ComboBox<Customer> customerBox;
    private DatePicker checkIn;
    private DatePicker checkOut;
    private Label priceLabel;
    private Label messageLabel;

    public BookingTab(HotelManager manager) {
        this.manager = manager;
        setupUI();
    }

    private void setupUI() {
        setSpacing(15);
        setPadding(new Insets(20));
        setStyle("-fx-background-color: #f1f5f9;");

        Label title = new Label("Room Booking");
        title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        roomBox = new ComboBox<>();
        roomBox.setPromptText("Select Room");
        roomBox.setPrefWidth(300);

        customerBox = new ComboBox<>();
        customerBox.setPromptText("Select Customer");
        customerBox.setPrefWidth(300);

        checkIn = new DatePicker();
        checkIn.setPromptText("Check-in Date");
        checkIn.setPrefWidth(300);

        checkOut = new DatePicker();
        checkOut.setPromptText("Check-out Date");
        checkOut.setPrefWidth(300);

        priceLabel = new Label("Estimated: Rs 0");
        priceLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #4f46e5;");

        Button bookBtn = new Button("Book Room");
        bookBtn.setStyle("-fx-background-color: #16a34a; -fx-text-fill: white; -fx-padding: 10 20;");

        Button checkoutBtn = new Button("Checkout");
        checkoutBtn.setStyle("-fx-background-color: #dc2626; -fx-text-fill: white; -fx-padding: 10 20;");

        messageLabel = new Label();

        HBox buttonBox = new HBox(10, bookBtn, checkoutBtn);

        getChildren().addAll(title, roomBox, customerBox, checkIn, checkOut, priceLabel, buttonBox, messageLabel);

        refreshBoxes();

        roomBox.setOnAction(e -> updatePrice());
        checkIn.setOnAction(e -> updatePrice());
        checkOut.setOnAction(e -> updatePrice());

        bookBtn.setOnAction(e -> bookRoom());
        checkoutBtn.setOnAction(e -> doCheckout());
    }

    private void refreshBoxes() {
        roomBox.setItems(FXCollections.observableArrayList(manager.getAvailableRooms()));
        customerBox.setItems(FXCollections.observableArrayList(manager.getAllCustomers()));
    }

    private void updatePrice() {
        Room room = roomBox.getValue();
        LocalDate in = checkIn.getValue();
        LocalDate out = checkOut.getValue();
        
        if (room == null || in == null || out == null) return;
        
        long nights = java.time.temporal.ChronoUnit.DAYS.between(in, out);
        if (nights <= 0) return;
        
        double total = room.getPricePerNight() * nights * 1.12;
        priceLabel.setText(String.format("Estimated: Rs %.2f", total));
    }

    private void bookRoom() {
        Room room = roomBox.getValue();
        Customer customer = customerBox.getValue();
        LocalDate in = checkIn.getValue();
        LocalDate out = checkOut.getValue();

        if (room == null || customer == null || in == null || out == null) {
            messageLabel.setText("Please fill all fields!");
            return;
        }

        String result = manager.bookRoom(room.getRoomNumber(), customer, in, out);
        
        if (result.startsWith("SUCCESS")) {
            messageLabel.setText("Booking successful!");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Invoice");
            alert.setContentText(result.substring(8));
            alert.showAndWait();
            refreshBoxes();
            clearForm();
        } else {
            messageLabel.setText(result);
        }
    }

    private void doCheckout() {
        Room room = roomBox.getValue();
        if (room == null) {
            messageLabel.setText("Select a room to checkout!");
            return;
        }
        
        String result = manager.checkout(room.getRoomNumber());
        
        if (result.startsWith("SUCCESS")) {
            messageLabel.setText("Checkout successful!");
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Invoice");
            alert.setContentText(result.substring(8));
            alert.showAndWait();
            refreshBoxes();
            clearForm();
        } else {
            messageLabel.setText(result);
        }
    }

    private void clearForm() {
        roomBox.setValue(null);
        customerBox.setValue(null);
        checkIn.setValue(null);
        checkOut.setValue(null);
        priceLabel.setText("Estimated: Rs 0");
    }
}