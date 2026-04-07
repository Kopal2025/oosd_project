package com.hotel.ui;

import com.hotel.dao.HotelManager;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

public class MainApp extends Application {

    public static HotelManager manager;
    public static DashboardTab dashboardTab;
public static BillingTab billingTab;
public static RoomTab roomTabInstance;
public static BookingTab bookingTabInstance;

    @Override
    public void start(Stage primaryStage) {

        manager = new HotelManager();

        // TabPane - tabs are hidden, sidebar controls navigation
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
        tabPane.setTabMaxHeight(0);

       dashboardTab = new DashboardTab(manager);
Tab dashboard = new Tab("Dashboard", dashboardTab);

billingTab = new BillingTab(manager);
Tab billing = new Tab("Billing", billingTab);
        RoomTab roomTabInstance = new RoomTab(manager);
Tab roomTab = new Tab("Rooms", roomTabInstance);
Tab customerTab = new Tab("Customers", new CustomerTab(manager));
BookingTab bookingTabInstance = new BookingTab(manager);
Tab bookingTab = new Tab("Booking", bookingTabInstance);
        tabPane.getTabs().addAll(
    dashboard,
    roomTab,
    customerTab,
    bookingTab,
    billing
);

        // ── Sidebar ──────────────────────────────
        VBox sidebar = new VBox(4);
        sidebar.getStyleClass().add("sidebar");
        sidebar.setPadding(new Insets(24, 12, 24, 12));

        // App name at top of sidebar
        Label appName = new Label("Hotel MS");
        appName.setStyle(
            "-fx-text-fill: #f1f5f9;" +
            "-fx-font-size: 16px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 0 0 20 6;"
        );

        Label menuLabel = new Label("MENU");
        menuLabel.setStyle(
            "-fx-text-fill: #475569;" +
            "-fx-font-size: 10px;" +
            "-fx-font-weight: bold;" +
            "-fx-padding: 0 0 8 6;"
        );

        Button dashBtn  = makeSidebarBtn("  Dashboard");
        Button roomBtn  = makeSidebarBtn("  Rooms");
        Button custBtn  = makeSidebarBtn("  Customers");
        Button bookBtn  = makeSidebarBtn("  Booking");
        Button billBtn  = makeSidebarBtn("  Billing");
       
        Button[] allBtns = {
            dashBtn, roomBtn, custBtn, bookBtn, billBtn
        };


sidebar.getChildren().addAll(
    appName, menuLabel,
    dashBtn, roomBtn, custBtn,
    bookBtn, billBtn
);

        // ── Navigation with active highlight ─────
        for (int i = 0; i < allBtns.length; i++) {
            final int index = i;
            final Button btn = allBtns[i];
            btn.setOnAction(e -> {
                // Remove active from all
                for (Button b : allBtns) {
                    b.getStyleClass().remove("sidebar-active");
                }
                // Add active to clicked
                btn.getStyleClass().add("sidebar-active");
                tabPane.getSelectionModel().select(index);
            });
        }

        // Set dashboard as active by default
        dashBtn.getStyleClass().add("sidebar-active");

        // ── Root Layout ───────────────────────────
        BorderPane root = new BorderPane();
        root.setLeft(sidebar);
        root.setCenter(tabPane);
        root.setStyle("-fx-background-color: #0f172a;");

        Scene scene = new Scene(root, 1100, 700);

        try {
            scene.getStylesheets().add(
                getClass().getResource(
                    "/com/hotel/css/styles.css"
                ).toExternalForm()
            );
        } catch (Exception e) {
            System.out.println("CSS error: " + e.getMessage());
        }

        LoginView login = new LoginView(primaryStage, manager);
primaryStage.setScene(login.getScene());
primaryStage.setTitle("Hotel Management System");
primaryStage.show();
    }

    private static Button makeSidebarBtn(String text) {
        Button btn = new Button(text);
        btn.getStyleClass().add("button");
        btn.setPrefWidth(160);
        btn.setAlignment(Pos.CENTER_LEFT);
        return btn;
    }

    public static Scene createMainScene(Stage stage) {

    // COPY EVERYTHING inside start() AFTER manager initialization
    // (except primaryStage.show())

    TabPane tabPane = new TabPane();
    tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);
    tabPane.setTabMaxHeight(0);

    dashboardTab = new DashboardTab(manager);
    Tab dashboard = new Tab("Dashboard", dashboardTab);

    billingTab = new BillingTab(manager);
    Tab billing = new Tab("Billing", billingTab);

    RoomTab roomTabInstance = new RoomTab(manager);
Tab roomTab = new Tab("Rooms", roomTabInstance);
Tab customerTab = new Tab("Customers", new CustomerTab(manager));
BookingTab bookingTabInstance = new BookingTab(manager);
Tab bookingTab = new Tab("Booking", bookingTabInstance);

    tabPane.getTabs().addAll(
        dashboard, roomTab, customerTab, bookingTab, billing
    );

    VBox sidebar = new VBox(4);
    sidebar.getStyleClass().add("sidebar");
    sidebar.setPadding(new Insets(24, 12, 24, 12));

    Label appName = new Label("Hotel MS");
    appName.setStyle("-fx-text-fill: #f1f5f9; -fx-font-size: 16px; -fx-font-weight: bold;");

    Button dashBtn  = new Button("  Dashboard");
    Button roomBtn  = new Button("  Rooms");
    Button custBtn  = new Button("  Customers");
    Button bookBtn  = new Button("  Booking");
    Button billBtn  = new Button("  Billing");
     Button logoutBtn = makeSidebarBtn("  Logout");


    Button[] allBtns = { dashBtn, roomBtn, custBtn, bookBtn, billBtn };

    for (int i = 0; i < allBtns.length; i++) {
        final int index = i;
        final Button btn = allBtns[i];
        btn.setOnAction(e -> tabPane.getSelectionModel().select(index));
    }
    logoutBtn.setOnAction(e -> {
        LoginView login = new LoginView(stage, manager);
        stage.setScene(login.getScene());
    });
    sidebar.getChildren().addAll(appName, dashBtn, roomBtn, custBtn, bookBtn, billBtn, logoutBtn);

    BorderPane root = new BorderPane();
    root.setLeft(sidebar);
    root.setCenter(tabPane);

    Scene scene = new Scene(root, 1100, 700);

    scene.getStylesheets().add(
        MainApp.class.getResource("/com/hotel/css/styles.css").toExternalForm()
    );

    return scene;
}

    public static void main(String[] args) {
        launch(args);
    }

   public static void refreshAllTabs() {
    if (dashboardTab != null) dashboardTab.refresh();
    if (billingTab != null) billingTab.refresh();
    if (roomTabInstance != null) roomTabInstance.refreshTable();
    if (bookingTabInstance != null) bookingTabInstance.refreshBoxes();
}
}