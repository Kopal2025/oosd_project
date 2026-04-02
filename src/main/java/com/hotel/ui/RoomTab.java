package com.hotel.ui;

import com.hotel.dao.HotelManager;
import com.hotel.model.Room;
import com.hotel.model.RoomType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;

public class RoomTab extends BorderPane {

    private final HotelManager manager;
    private TableView<Room> tableView;
    private ObservableList<Room> masterList;

    public RoomTab(HotelManager manager) {
        this.manager = manager;
        setStyle("-fx-background-color: #f1f5f9;");
        setCenter(buildUI());
    }

    private VBox buildUI() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(28));
        root.setStyle("-fx-background-color: #f1f5f9;");

        // ── Title ─────────────────────────────────────
        Label title = new Label("Room Management");
        title.setStyle(
            "-fx-font-size: 22px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #0f172a;"
        );

        Label subtitle = new Label(
            "Add and manage hotel rooms"
        );
        subtitle.setStyle("-fx-text-fill: #64748b;");

        VBox titleBox = new VBox(4, title, subtitle);

        // ── Add Room Form Card ─────────────────────────
        VBox formCard = new VBox(16);
        formCard.setPadding(new Insets(20));
        formCard.setStyle(
            "-fx-background-color: white;" +
            "-fx-background-radius: 12;" +
            "-fx-border-radius: 12;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 1;"
        );

        Label formTitle = new Label("Add New Room");
        formTitle.setStyle(
            "-fx-font-size: 14px;" +
            "-fx-font-weight: bold;" +
            "-fx-text-fill: #1e293b;"
        );

        // Row 1 — Room number + Type
        GridPane grid = new GridPane();
        grid.setHgap(16);
        grid.setVgap(14);

        ColumnConstraints labelCol = new ColumnConstraints();
        labelCol.setPrefWidth(110);
        ColumnConstraints fieldCol = new ColumnConstraints();
        fieldCol.setHgrow(Priority.ALWAYS);
        grid.getColumnConstraints().addAll(
            labelCol, fieldCol, labelCol, fieldCol
        );

        // Room Number
        Label numLbl = makeFormLabel("Room Number");
        TextField roomNumField = makeInput("e.g. 305");
        grid.add(numLbl, 0, 0);
        grid.add(roomNumField, 1, 0);

        // Room Type
        Label typeLbl = makeFormLabel("Room Type");
        ComboBox<RoomType> typeCombo = new ComboBox<>(
            FXCollections.observableArrayList(RoomType.values())
        );
        typeCombo.setPromptText("Select type");
        typeCombo.setMaxWidth(Double.MAX_VALUE);
        styleCombo(typeCombo);
        grid.add(typeLbl, 2, 0);
        grid.add(typeCombo, 3, 0);

        // Custom Price
        Label priceLbl = makeFormLabel("Custom Price");
        TextField priceField = makeInput("Leave blank for default");
        grid.add(priceLbl, 0, 1);
        grid.add(priceField, 1, 1);

        // Description
        Label descLbl = makeFormLabel("Description");
        TextField descField = makeInput("e.g. Sea view, AC");
        grid.add(descLbl, 2, 1);
        grid.add(descField, 3, 1);

        // Price preview
        Label pricePreview = new Label("");
        pricePreview.setStyle(
            "-fx-text-fill: #4f46e5;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;"
        );

        // Buttons + message
        Button addBtn = makeBtn("Add Room", "#4f46e5");
        Button clearBtn = makeBtn("Clear", "#64748b");
        Label msgLabel = new Label("");
        msgLabel.setStyle("-fx-font-size: 12px;");

        HBox btnRow = new HBox(12,
            addBtn, clearBtn, msgLabel
        );
        btnRow.setAlignment(Pos.CENTER_LEFT);

        formCard.getChildren().addAll(
            formTitle, grid, pricePreview, btnRow
        );

        // ── Filter / Search Bar ───────────────────────
        HBox filterBar = new HBox(10);
        filterBar.setAlignment(Pos.CENTER_LEFT);
        filterBar.setPadding(new Insets(4, 0, 4, 0));

        TextField searchField = new TextField();
        searchField.setPromptText("Search rooms...");
        searchField.setPrefWidth(240);
        searchField.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #cbd5e1;" +
            "-fx-border-radius: 20;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 8 14 8 14;" +
            "-fx-font-size: 13px;" +
            "-fx-text-fill: #0f172a;" +
            "-fx-prompt-text-fill: #94a3b8;"
        );

        Button allBtn     = makeFilterBtn("All Rooms",  "#475569");
        Button availBtn   = makeFilterBtn("Available",  "#16a34a");
        Button bookedBtn  = makeFilterBtn("Booked",     "#dc2626");
        Button sortPrice  = makeFilterBtn("Sort by Price", "#475569");
        Button sortNum    = makeFilterBtn("Sort by No.", "#475569");

        Label countLabel = new Label("");
        countLabel.setStyle(
            "-fx-text-fill: #94a3b8;" +
            "-fx-font-size: 12px;"
        );
        HBox.setHgrow(searchField, Priority.ALWAYS);

        filterBar.getChildren().addAll(
            searchField, allBtn, availBtn,
            bookedBtn, sortPrice, sortNum, countLabel
        );

        // ── Table ─────────────────────────────────────
        tableView = buildTable();
        VBox.setVgrow(tableView, Priority.ALWAYS);
        refreshTable();
        countLabel.setText(
            masterList.size() + " rooms"
        );

        // ── Events ────────────────────────────────────

        // Price preview on type select
        typeCombo.setOnAction(e -> {
            RoomType t = typeCombo.getValue();
            if (t == null) return;
            String custom = priceField.getText().trim();
            if (custom.isEmpty()) {
                pricePreview.setText(
                    "Default: Rs " +
                    (int) t.getPricePerNight() +
                    " per night"
                );
            }
        });

        priceField.textProperty().addListener(
            (obs, o, n) -> {
                RoomType t = typeCombo.getValue();
                if (t == null) return;
                if (n.trim().isEmpty()) {
                    pricePreview.setText(
                        "Default: Rs " +
                        (int) t.getPricePerNight() +
                        " per night"
                    );
                } else {
                    try {
                        double p = Double.parseDouble(n);
                        pricePreview.setText(
                            "Custom price: Rs " +
                            (int) p + " per night"
                        );
                    } catch (NumberFormatException ex) {
                        pricePreview.setText(
                            "Invalid price"
                        );
                    }
                }
            }
        );

        // Real time search
        searchField.textProperty().addListener(
            (obs, o, n) -> {
                filterTable(n);
                countLabel.setText(
                    masterList.size() + " rooms"
                );
            }
        );

        // Add room
        addBtn.setOnAction(e -> {
            msgLabel.setStyle(
                "-fx-font-size: 12px;" +
                "-fx-text-fill: #dc2626;"
            );
            String numText =
                roomNumField.getText().trim();
            RoomType type = typeCombo.getValue();

            if (numText.isEmpty()) {
                msgLabel.setText(
                    "Room number required."
                );
                return;
            }
            if (type == null) {
                msgLabel.setText("Select a room type.");
                return;
            }
            try {
                int num = Integer.parseInt(numText);
                if (manager.roomExists(num)) {
                    msgLabel.setText(
                        "Room " + num + " already exists."
                    );
                    return;
                }

                Room room;
                String pt = priceField.getText().trim();
                if (!pt.isEmpty()) {
                    double p = Double.parseDouble(pt);
                    if (p <= 0) {
                        msgLabel.setText(
                            "Price must be positive."
                        );
                        return;
                    }
                    room = new Room(num, type, p);
                } else {
                    room = new Room(num, type);
                }

                String desc = descField.getText().trim();
                if (!desc.isEmpty()) {
                    room.setDescription(desc);
                }

                manager.addRoom(room);
                refreshTable();
                countLabel.setText(
                    masterList.size() + " rooms"
                );

                roomNumField.clear();
                typeCombo.setValue(null);
                priceField.clear();
                descField.clear();
                pricePreview.setText("");

                msgLabel.setStyle(
                    "-fx-font-size: 12px;" +
                    "-fx-text-fill: #16a34a;" +
                    "-fx-font-weight: bold;"
                );
                msgLabel.setText(
                    "Room " + num + " added!"
                );

            } catch (NumberFormatException ex) {
                msgLabel.setText(
                    "Invalid number or price."
                );
            }
        });

        clearBtn.setOnAction(e -> {
            roomNumField.clear();
            typeCombo.setValue(null);
            priceField.clear();
            descField.clear();
            pricePreview.setText("");
            msgLabel.setText("");
        });

        allBtn.setOnAction(e -> {
            searchField.clear();
            masterList.setAll(manager.getAllRooms());
            countLabel.setText(
                masterList.size() + " rooms"
            );
        });

        availBtn.setOnAction(e -> {
            searchField.clear();
            masterList.setAll(
                manager.getAvailableRooms()
            );
            countLabel.setText(
                masterList.size() + " rooms"
            );
        });

        bookedBtn.setOnAction(e -> {
            searchField.clear();
            masterList.setAll(
                manager.getAllRooms().stream()
                    .filter(r -> !r.isAvailable())
                    .toList()
            );
            countLabel.setText(
                masterList.size() + " rooms"
            );
        });

        sortPrice.setOnAction(e -> {
            manager.sortRoomsByPrice();
            refreshTable();
        });

        sortNum.setOnAction(e -> {
            manager.sortRoomsByNumber();
            refreshTable();
        });

        root.getChildren().addAll(
            titleBox, formCard, filterBar, tableView
        );
        return root;
    }

    // ── Build the table ───────────────────────────
    @SuppressWarnings("unchecked")
    private TableView<Room> buildTable() {
        TableView<Room> tv = new TableView<>();
        tv.setColumnResizePolicy(
            TableView.CONSTRAINED_RESIZE_POLICY
        );
        tv.setStyle(
            "-fx-background-color: white;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-radius: 12;" +
            "-fx-background-radius: 12;"
        );
        tv.setPlaceholder(
            new Label("No rooms found.")
        );

        // Room No column
        TableColumn<Room, Integer> numCol =
            new TableColumn<>("Room No.");
        numCol.setCellValueFactory(
            new PropertyValueFactory<>("roomNumber")
        );
        numCol.setStyle(
            "-fx-alignment: CENTER_LEFT;"
        );

        // Type column — shows just "Single Room"
        TableColumn<Room, RoomType> typeCol =
            new TableColumn<>("Type");
        typeCol.setCellValueFactory(
            new PropertyValueFactory<>("roomType")
        );
        // Custom cell so it shows displayName only
        typeCol.setCellFactory(col ->
            new TableCell<>() {
                @Override
                protected void updateItem(
                    RoomType type, boolean empty) {
                    super.updateItem(type, empty);
                    if (empty || type == null) {
                        setText(null);
                        return;
                    }
                    setText(type.getDisplayName());
                    setStyle(
                        "-fx-text-fill: #1e293b;" +
                        "-fx-font-weight: 500;"
                    );
                }
            }
        );

        // Description column
        TableColumn<Room, String> descCol =
            new TableColumn<>("Description");
        descCol.setCellValueFactory(
            new PropertyValueFactory<>("description")
        );
        descCol.setCellFactory(col ->
            new TableCell<>() {
                @Override
                protected void updateItem(
                    String desc, boolean empty) {
                    super.updateItem(desc, empty);
                    if (empty || desc == null) {
                        setText(null);
                        return;
                    }
                    setText(desc);
                    setStyle(
                        "-fx-text-fill: #64748b;"
                    );
                }
            }
        );

        // Price column
        TableColumn<Room, Double> priceCol =
            new TableColumn<>("Price / Night");
        priceCol.setCellValueFactory(
            new PropertyValueFactory<>("pricePerNight")
        );
        priceCol.setCellFactory(col ->
            new TableCell<>() {
                @Override
                protected void updateItem(
                    Double price, boolean empty) {
                    super.updateItem(price, empty);
                    if (empty || price == null) {
                        setText(null);
                        return;
                    }
                    setText("Rs " + (int)(double) price);
                    setStyle(
                        "-fx-text-fill: #4f46e5;" +
                        "-fx-font-weight: bold;"
                    );
                }
            }
        );

        // Status column with colored badge
        TableColumn<Room, Boolean> statusCol =
            new TableColumn<>("Status");
        statusCol.setCellValueFactory(
            new PropertyValueFactory<>("available")
        );
        statusCol.setCellFactory(col ->
            new TableCell<>() {
                @Override
                protected void updateItem(
                    Boolean avail, boolean empty) {
                    super.updateItem(avail, empty);
                    if (empty || avail == null) {
                        setText(null);
                        setGraphic(null);
                        return;
                    }
                    Label badge = new Label(
                        avail ? "Available" : "Booked"
                    );
                    if (avail) {
                        badge.setStyle(
                            "-fx-background-color: #dcfce7;" +
                            "-fx-text-fill: #16a34a;" +
                            "-fx-background-radius: 20;" +
                            "-fx-padding: 4 12 4 12;" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 11px;"
                        );
                    } else {
                        badge.setStyle(
                            "-fx-background-color: #fee2e2;" +
                            "-fx-text-fill: #dc2626;" +
                            "-fx-background-radius: 20;" +
                            "-fx-padding: 4 12 4 12;" +
                            "-fx-font-weight: bold;" +
                            "-fx-font-size: 11px;"
                        );
                    }
                    setGraphic(badge);
                    setText(null);

                    // Color whole row
                    if (getTableRow() != null) {
                        getTableRow().setStyle(
                            avail
                            ? "-fx-background-color: white;"
                            : "-fx-background-color: #fff5f5;"
                        );
                    }
                }
            }
        );

        tv.getColumns().addAll(
            numCol, typeCol, descCol,
            priceCol, statusCol
        );

        // Style alternating rows
        tv.setRowFactory(t -> new TableRow<>() {
            @Override
            protected void updateItem(
                Room item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                    return;
                }
                if (!item.isAvailable()) {
                    setStyle(
                        "-fx-background-color: #fff5f5;"
                    );
                } else {
                    setStyle(
                        getIndex() % 2 == 0
                        ? "-fx-background-color: white;"
                        : "-fx-background-color: #f8fafc;"
                    );
                }
            }
        });

        return tv;
    }

    // ── Helpers ───────────────────────────────────

    private void filterTable(String query) {
        if (query == null || query.trim().isEmpty()) {
            masterList.setAll(manager.getAllRooms());
            return;
        }
        String lower = query.toLowerCase();
        masterList.setAll(
            manager.getAllRooms().stream()
                .filter(r ->
                    String.valueOf(r.getRoomNumber())
                          .contains(lower)
                    || r.getRoomType().getDisplayName()
                         .toLowerCase().contains(lower)
                    || r.getDescription()
                         .toLowerCase().contains(lower)
                )
                .toList()
        );
    }

    public void refreshTable() {
        masterList = FXCollections.observableArrayList(
            manager.getAllRooms()
        );
        tableView.setItems(masterList);
    }

    private Label makeFormLabel(String text) {
        Label l = new Label(text);
        l.setStyle(
            "-fx-text-fill: #64748b;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;"
        );
        return l;
    }

    private TextField makeInput(String prompt) {
        TextField tf = new TextField();
        tf.setPromptText(prompt);
        tf.setStyle(
            "-fx-background-color: #f8fafc;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 9 12 9 12;" +
            "-fx-font-size: 13px;" +
            "-fx-text-fill: #0f172a;" +
            "-fx-prompt-text-fill: #94a3b8;"
        );
        tf.setMaxWidth(Double.MAX_VALUE);
        return tf;
    }

    private void styleCombo(ComboBox<?> cb) {
        cb.setStyle(
            "-fx-background-color: #f8fafc;" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-radius: 8;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 4 8 4 8;" +
            "-fx-font-size: 13px;"
        );
    }

    private Button makeBtn(String text, String color) {
        Button btn = new Button(text);
        btn.setStyle(
            "-fx-background-color: " + color + ";" +
            "-fx-text-fill: white;" +
            "-fx-font-weight: bold;" +
            "-fx-background-radius: 8;" +
            "-fx-padding: 9 20 9 20;" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 13px;"
        );
        return btn;
    }

    private Button makeFilterBtn(
        String text, String color) {
        Button btn = new Button(text);
        btn.setStyle(
            "-fx-background-color: white;" +
            "-fx-text-fill: " + color + ";" +
            "-fx-border-color: #e2e8f0;" +
            "-fx-border-width: 1;" +
            "-fx-border-radius: 20;" +
            "-fx-background-radius: 20;" +
            "-fx-padding: 7 16 7 16;" +
            "-fx-cursor: hand;" +
            "-fx-font-size: 12px;" +
            "-fx-font-weight: bold;"
        );
        return btn;
    }
}