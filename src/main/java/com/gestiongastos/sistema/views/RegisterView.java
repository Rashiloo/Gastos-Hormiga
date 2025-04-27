package com.gestiongastos.sistema.views;

import com.gestiongastos.sistema.dto.RegistroUsuarioDTO;
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
public class RegisterView {
    private Stage stage;
    private Scene scene;
    private final AuthService authService;
    private Scene loginScene;
    private TextField nombreField;
    private TextField apellidoField;
    private TextField emailField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;

    public RegisterView(AuthService authService) {
        this.authService = authService;
    }

    public void show(Stage stage, Scene loginScene) {
        this.stage = stage;
        this.loginScene = loginScene;
        createRegisterScene();
    }

    private void createRegisterScene() {
        VBox mainContainer = new VBox(15);
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setPadding(new Insets(40));
        mainContainer.setStyle("-fx-background-color: white;");

        // Título
        Label titleLabel = new Label("Registro de Usuario");
        titleLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold;");

        // Formulario de registro
        GridPane formGrid = new GridPane();
        formGrid.setHgap(10);
        formGrid.setVgap(15);
        formGrid.setAlignment(Pos.CENTER);

        // Campos del formulario
        Label nombreLabel = new Label("Nombre:");
        nombreField = new TextField();
        nombreField.setPromptText("Ingrese su nombre");

        Label apellidoLabel = new Label("Apellido:");
        apellidoField = new TextField();
        apellidoField.setPromptText("Ingrese su apellido");

        Label emailLabel = new Label("Email:");
        emailField = new TextField();
        emailField.setPromptText("Ingrese su email");

        Label passwordLabel = new Label("Contraseña:");
        passwordField = new PasswordField();
        passwordField.setPromptText("Mínimo 6 caracteres");

        Label confirmPasswordLabel = new Label("Confirmar Contraseña:");
        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Repita su contraseña");

        // Configurar el grid
        formGrid.add(nombreLabel, 0, 0);
        formGrid.add(nombreField, 1, 0);
        formGrid.add(apellidoLabel, 0, 1);
        formGrid.add(apellidoField, 1, 1);
        formGrid.add(emailLabel, 0, 2);
        formGrid.add(emailField, 1, 2);
        formGrid.add(passwordLabel, 0, 3);
        formGrid.add(passwordField, 1, 3);
        formGrid.add(confirmPasswordLabel, 0, 4);
        formGrid.add(confirmPasswordField, 1, 4);

        // Configurar ancho de los campos
        nombreField.setPrefWidth(250);
        apellidoField.setPrefWidth(250);
        emailField.setPrefWidth(250);
        passwordField.setPrefWidth(250);
        confirmPasswordField.setPrefWidth(250);

        // Botones
        Button registerButton = new Button("Registrarse");
        registerButton.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");

        Button backButton = new Button("Volver al Login");
        backButton.setStyle("-fx-background-color: #757575; -fx-text-fill: white;");

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(registerButton, backButton);

        // Eventos
        registerButton.setOnAction(e -> handleRegister());
        backButton.setOnAction(e -> stage.setScene(loginScene));

        // Agregar todo al contenedor principal
        mainContainer.getChildren().addAll(
                titleLabel,
                formGrid,
                buttonBox
        );

        // Crear y configurar la escena
        scene = new Scene(mainContainer, 600, 500);
        stage.setScene(scene);
        stage.setTitle("Registro - Sistema de Gestión de Gastos");
        stage.centerOnScreen();
    }

    private void handleRegister() {
        // Obtener valores de los campos
        String nombre = nombreField.getText();
        String apellido = apellidoField.getText();
        String email = emailField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validaciones
        if (nombre.isEmpty() || apellido.isEmpty() || email.isEmpty() ||
                password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Por favor complete todos los campos.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "Las contraseñas no coinciden.");
            return;
        }

        if (password.length() < 6) {
            showAlert(Alert.AlertType.ERROR, "Error",
                    "La contraseña debe tener al menos 6 caracteres.");
            return;
        }

        try {
            RegistroUsuarioDTO registroDTO = new RegistroUsuarioDTO();
            registroDTO.setNombre(nombre);
            registroDTO.setApellido(apellido);
            registroDTO.setEmail(email);
            registroDTO.setPassword(password);

            authService.register(registroDTO);
            showAlert(Alert.AlertType.INFORMATION, "Éxito",
                    "Usuario registrado correctamente.");
            stage.setScene(loginScene);
        } catch (Exception ex) {
            showAlert(Alert.AlertType.ERROR, "Error de registro",
                    ex.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}