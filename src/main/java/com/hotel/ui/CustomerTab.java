package com.hotel.ui;

import com.hotel.dao.HotelManager;
import com.hotel.model.Customer;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;

public class CustomerTab extends BorderPane {

    private HotelManager manager;
    private TableView<Customer> table;

    public CustomerTab(HotelManager manager) {
        this.manager = manager;
        setupUI();
    }

    private void setupUI() {
        getStyleClass().add("page-background");
        setPadding(new Insets(24));

        Label title = new Label("Customer Management");
        title.getStyleClass().add("page-title");

        // Form
        TextField nameField = new TextField();
        nameField.setPromptText("Name");
        nameField.getStyleClass().add("form-input");

        TextField phoneField = new TextField();
        phoneField.setPromptText("Phone (10 digits)");
        phoneField.getStyleClass().add("form-input");

        TextField emailField = new TextField();
        emailField.setPromptText("Email");
        emailField.getStyleClass().add("form-input");

        TextField addressField = new TextField();
        addressField.setPromptText("Address");
        addressField.getStyleClass().add("form-input");

        Button addBtn = new Button("Add Customer");
        addBtn.getStyleClass().add("btn-primary");

        Label message = new Label();
        message.getStyleClass().addAll("form-message", "error");

        VBox form = new VBox(14);
        form.getStyleClass().add("card");
        form.setMaxWidth(360);
        form.setPrefWidth(320);
        form.getChildren().addAll(title, nameField, phoneField, emailField, addressField, addBtn, message);

        // Table
        table = new TableView<>();

        TableColumn<Customer, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("customerId"));

        TableColumn<Customer, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("name"));

        TableColumn<Customer, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("phone"));

        table.getColumns().addAll(idCol, nameCol, phoneCol);
        refreshTable();

        // Zebra rows + hover (UI-only)
        table.setRowFactory(t -> new TableRow<>() {
            @Override
            protected void updateItem(Customer item, boolean empty) {
                super.updateItem(item, empty);
                getStyleClass().removeAll("row-odd", "row-even");
                if (empty || item == null) return;
                getStyleClass().add((getIndex() % 2 == 0) ? "row-even" : "row-odd");
            }
        });

        // Search
        TextField searchField = new TextField();
        searchField.setPromptText("Search by name...");
        searchField.getStyleClass().add("form-input");
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            table.setItems(FXCollections.observableArrayList(manager.searchCustomers(newVal)));
        });

        addBtn.setOnAction(e -> {
            String name = nameField.getText();
            String phone = phoneField.getText();

            if (name.isEmpty() || phone.length() != 10) {
                message.setText("Invalid input!");
                message.getStyleClass().removeAll("success", "error");
                message.getStyleClass().add("error");
                return;
            }

            Customer c = new Customer(name, phone, emailField.getText(), addressField.getText());
            manager.addCustomer(c);

            message.setText("Customer added!");
            message.getStyleClass().removeAll("success", "error");
            message.getStyleClass().add("success");
            refreshTable();

            nameField.clear();
            phoneField.clear();
            emailField.clear();
            addressField.clear();
        });

        VBox centerCard = new VBox(12);
        centerCard.getStyleClass().add("card");
        centerCard.setFillWidth(true);

        centerCard.getChildren().addAll(searchField, table);
        VBox.setVgrow(table, Priority.ALWAYS);

        VBox root = new VBox(20);
root.getStyleClass().add("page-background");

Label subtitle = new Label("Manage customer records");
subtitle.getStyleClass().add("page-subtitle");

// Wrap form in proper card
VBox formCard = new VBox(12);
formCard.getStyleClass().add("card");
formCard.setMaxWidth(500);
formCard.getChildren().addAll(
        new Label("Add Customer"),
        nameField, phoneField, emailField, addressField, addBtn, message
);

// Table card already exists → reuse
VBox tableCard = new VBox(12);
tableCard.getStyleClass().add("card");
tableCard.getChildren().addAll(searchField, table);
VBox.setVgrow(table, Priority.ALWAYS);

// Layout like RoomTab
root.getChildren().addAll(
        title,
        subtitle,
        formCard,
        tableCard
);

setCenter(root);
    }

    private void refreshTable() {
        table.setItems(FXCollections.observableArrayList(manager.getAllCustomers()));
    }
}