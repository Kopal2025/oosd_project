package com.hotel.ui;

import com.hotel.dao.HotelManager;
import com.hotel.model.Customer;
import com.hotel.model.Room;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.time.LocalDate;

public class BookingTab extends BorderPane {

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

       Button refreshBtn = new Button("Refresh");
refreshBtn.setStyle("-fx-background-color: #64748b; -fx-text-fill: white; -fx-padding: 10 20;");

HBox buttonBox = new HBox(10, bookBtn, checkoutBtn, refreshBtn);
refreshBtn.setOnAction(e -> refreshBoxes());
refreshBtn.setOnMouseEntered(e -> refreshBtn.setStyle("-fx-background-color: #475569; -fx-text-fill: white;"));
refreshBtn.setOnMouseExited(e -> refreshBtn.setStyle("-fx-background-color: #64748b; -fx-text-fill: white;"));

        getChildren().addAll(title, roomBox, customerBox, checkIn, checkOut, priceLabel, buttonBox, messageLabel);
        this.setOnMouseEntered(e -> refreshBoxes());

        refreshBoxes();

        roomBox.setOnAction(e -> updatePrice());
        checkIn.setOnAction(e -> updatePrice());
        checkOut.setOnAction(e -> updatePrice());

        bookBtn.setOnAction(e -> bookRoom());
        checkoutBtn.setOnAction(ev -> doCheckout());

        VBox root = new VBox(20);
root.getStyleClass().add("page-background");

title.getStyleClass().add("page-title");

Label subtitle = new Label("Book and manage room reservations");
subtitle.getStyleClass().add("page-subtitle");

// ── FORM CARD ─────────────────────────
VBox formCard = new VBox(12);
formCard.getStyleClass().add("card");

formCard.getChildren().addAll(
        new Label("New Booking"),
        roomBox,
        customerBox,
        checkIn,
        checkOut,
        priceLabel,
        buttonBox,
        messageLabel
);

// ── MAIN LAYOUT ───────────────────────
root.getChildren().addAll(
        title,
        subtitle,
        formCard
);

setCenter(root);
    }

    public void refreshBoxes() {
        roomBox.setItems(FXCollections.observableArrayList(manager.getAllRooms()));
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
            MainApp.dashboardTab.refresh();
MainApp.billingTab.refresh();
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
            roomBox.getItems().clear();
roomBox.setItems(FXCollections.observableArrayList(manager.getAllRooms()));
MainApp.dashboardTab.refresh();
MainApp.billingTab.refresh();
MainApp.roomTabInstance.refreshTable();
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