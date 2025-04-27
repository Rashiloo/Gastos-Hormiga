package com.gestiongastos.sistema.views;

import com.gestiongastos.sistema.dto.LoginDTO;
import com.gestiongastos.sistema.service.AuthService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

@Component
public class LoginView {
    private Stage stage;
    private Scene scene;
    private TextField emailField;
    private PasswordField passwordField;
    private final AuthService authService;

    public LoginView(AuthService authService) {
        this.authService = authService;
    }

    public void show(Stage stage) {
        this.stage = stage;
        createLoginScene();
        stage.show();
    }

    private void createLoginScene() {
        VBox mainContainer = new VBox(20);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40));
        mainContainer.setStyle("-fx-background-color: white;");

        // Título
        Label titleLabel = new Label("Sistema de Gestión de Gastos Hormiga");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        // Formulario de login
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(10);
        formGrid.setAlignment(Pos.CENTER);

        Label emailLabel = new Label("Email:");
        emailField = new TextField();
        emailField.setPromptText("Ingresa tu email");
        emailField.setPrefWidth(250);

        Label passwordLabel = new Label("Contraseña:");
        passwordField = new PasswordField();
        passwordField.setPromptText("Ingresa tu contraseña");
        passwordField.setPrefWidth(250);

        formGrid.add(emailLabel, 0, 0);
        formGrid.add(emailField, 1, 0);
        formGrid.add(passwordLabel, 0, 1);
        formGrid.add(passwordField, 1, 1);

        // Botones
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button loginButton = new Button("Iniciar Sesión");
        loginButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
        Button registerButton = new Button("Registrarse");
        registerButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        buttonBox.getChildren().addAll(loginButton, registerButton);

        // Eventos de los botones
        loginButton.setOnAction(e -> handleLogin());
        registerButton.setOnAction(e -> showRegisterForm());

        mainContainer.getChildren().addAll(titleLabel, formGrid, buttonBox);

        scene = new Scene(mainContainer, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Login - Sistema de Gestión de Gastos");
        stage.centerOnScreen();
    }

    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "Por favor complete todos los campos.");
            return;
        }

        try {
            LoginDTO loginDTO = new LoginDTO();
            loginDTO.setEmail(email);
            loginDTO.setPassword(password);

            String token = authService.login(loginDTO);
            // TODO: Guardar el token para futuras peticiones
            showMainApplication();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error de autenticación", e.getMessage());
        }
    }

    private void showRegisterForm() {
        RegisterView registerView = new RegisterView(authService);
        registerView.show(stage, scene);
    }

    private void showMainApplication() {
        MainView mainView = new MainView(authService);
        mainView.show(stage);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}