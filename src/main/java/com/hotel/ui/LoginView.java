package com.hotel.ui;

import com.hotel.dao.HotelManager;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;

public class LoginView {

    private final Stage stage;
    private final HotelManager manager;

    public LoginView(Stage stage, HotelManager manager) {
        this.stage = stage;
        this.manager = manager;
    }

public Scene getScene() {

    BorderPane root = new BorderPane();

    // 🔥 LEFT SIDE (same style as your app)
    VBox left = new VBox(10);
    left.setPadding(new Insets(40));
    left.setPrefWidth(250);
    left.setStyle("-fx-background-color: #0f172a;");

    Label appName = new Label("Hotel MS");
    appName.setStyle(
        "-fx-text-fill: #f1f5f9;" +
        "-fx-font-size: 18px;" +
        "-fx-font-weight: bold;"
    );

    Label tagline = new Label("Management System");
    tagline.setStyle("-fx-text-fill: #94a3b8;");

    left.getChildren().addAll(appName, tagline);

    // 🔥 RIGHT SIDE (login form)
    VBox form = new VBox(15);
    form.setAlignment(Pos.CENTER);
    form.setPadding(new Insets(40));

    Label title = new Label("Login");
    title.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

    TextField username = new TextField();
    username.setPromptText("Username");

    PasswordField password = new PasswordField();
    password.setPromptText("Password");

    Label error = new Label();
    error.setStyle("-fx-text-fill: red;");

    Button loginBtn = new Button("Login");
    loginBtn.setPrefWidth(200);

    loginBtn.setOnAction(e -> {
        if (username.getText().equals("admin") && password.getText().equals("1234")) {
            openDashboard();
        } else {
            error.setText("Invalid username or password");
        }
    });

    form.getChildren().addAll(title, username, password, loginBtn, error);

    root.setLeft(left);
    root.setCenter(form);

    Scene scene = new Scene(root, 700, 400);

    scene.getStylesheets().add(
        getClass().getResource("/com/hotel/css/styles.css").toExternalForm()
    );

    return scene;
}

   private void openDashboard() {
    Scene scene = MainApp.createMainScene(stage);
    stage.setScene(scene);
}
}