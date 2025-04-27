package com.gestiongastos.sistema.views;

import com.gestiongastos.sistema.service.AuthService;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class MainView {
    private Stage stage;
    private Scene scene;
    private final AuthService authService;

    public MainView(AuthService authService) {
        this.authService = authService;
    }

    public void show(Stage stage) {
        this.stage = stage;
        createMainScene();
    }

    private void createMainScene() {
        // Configuración básica de la ventana
        stage.setTitle("Sistema de Gestión de Gastos Hormiga");

        // Crear el layout principal
        BorderPane mainLayout = new BorderPane();

        // Crear la barra de menú
        MenuBar menuBar = createMenuBar();
        mainLayout.setTop(menuBar);

        // Panel lateral izquierdo
        VBox sidePanel = createSidePanel();
        mainLayout.setLeft(sidePanel);

        // Área principal de contenido
        VBox mainContent = createMainContent();
        mainLayout.setCenter(mainContent);

        // Crear y configurar la escena
        scene = new Scene(mainLayout, 1000, 700);
        stage.setScene(scene);
        stage.centerOnScreen();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();

        // Menú Archivo
        Menu fileMenu = new Menu("Archivo");
        MenuItem newItem = new MenuItem("Nuevo Gasto");
        MenuItem exitItem = new MenuItem("Salir");
        exitItem.setOnAction(e -> Platform.exit());
        fileMenu.getItems().addAll(newItem, new SeparatorMenuItem(), exitItem);

        // Menú Reportes
        Menu reportsMenu = new Menu("Reportes");
        MenuItem monthlyReport = new MenuItem("Reporte Mensual");
        MenuItem annualReport = new MenuItem("Reporte Anual");
        reportsMenu.getItems().addAll(monthlyReport, annualReport);

        // Menú Usuario
        Menu userMenu = new Menu("Usuario");
        MenuItem profileItem = new MenuItem("Perfil");
        MenuItem logoutItem = new MenuItem("Cerrar Sesión");
        logoutItem.setOnAction(e -> handleLogout());
        userMenu.getItems().addAll(profileItem, new SeparatorMenuItem(), logoutItem);

        menuBar.getMenus().addAll(fileMenu, reportsMenu, userMenu);
        return menuBar;
    }

    private VBox createSidePanel() {
        VBox sidePanel = new VBox(10);
        sidePanel.setPadding(new Insets(10));
        sidePanel.setStyle("-fx-background-color: #f0f0f0;");
        sidePanel.setPrefWidth(200);

        Button btnNuevoGasto = new Button("Nuevo Gasto");
        Button btnVerGastos = new Button("Ver Gastos");
        Button btnMetas = new Button("Metas de Ahorro");
        Button btnCategorias = new Button("Categorías");

        // Configurar el ancho de los botones
        btnNuevoGasto.setMaxWidth(Double.MAX_VALUE);
        btnVerGastos.setMaxWidth(Double.MAX_VALUE);
        btnMetas.setMaxWidth(Double.MAX_VALUE);
        btnCategorias.setMaxWidth(Double.MAX_VALUE);

        // Configurar acciones de los botones
        btnNuevoGasto.setOnAction(e -> showNuevoGasto());
        btnVerGastos.setOnAction(e -> showVerGastos());
        btnMetas.setOnAction(e -> showMetas());
        btnCategorias.setOnAction(e -> showCategorias());

        sidePanel.getChildren().addAll(
                btnNuevoGasto,
                btnVerGastos,
                btnMetas,
                btnCategorias
        );

        return sidePanel;
    }

    private VBox createMainContent() {
        VBox mainContent = new VBox(20);
        mainContent.setPadding(new Insets(20));

        // Título de bienvenida
        Label welcomeLabel = new Label("¡Bienvenido al Sistema de Gestión de Gastos Hormiga!");
        welcomeLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold;");

        // Panel de resumen
        GridPane summaryPane = new GridPane();
        summaryPane.setHgap(20);
        summaryPane.setVgap(10);
        summaryPane.setPadding(new Insets(20));
        summaryPane.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-radius: 5;");

        // Añadir elementos al panel de resumen
        summaryPane.add(new Label("Gastos del Mes:"), 0, 0);
        summaryPane.add(new Label("$0.00"), 1, 0);
        summaryPane.add(new Label("Meta de Ahorro:"), 0, 1);
        summaryPane.add(new Label("$0.00"), 1, 1);
        summaryPane.add(new Label("Progreso:"), 0, 2);
        ProgressBar progressBar = new ProgressBar(0.0);
        progressBar.setPrefWidth(200);
        summaryPane.add(progressBar, 1, 2);

        mainContent.getChildren().addAll(welcomeLabel, summaryPane);
        return mainContent;
    }

    private void handleLogout() {
        // Limpiar el contexto de seguridad
        SecurityContextHolder.clearContext();
        // Volver a la pantalla de login
        LoginView loginView = new LoginView(authService);
        loginView.show(stage);
    }

    // Métodos para manejar las acciones de los botones del panel lateral
    private void showNuevoGasto() {
        showAlert(Alert.AlertType.INFORMATION, "Nuevo Gasto", "Funcionalidad en desarrollo");
    }

    private void showVerGastos() {
        showAlert(Alert.AlertType.INFORMATION, "Ver Gastos", "Funcionalidad en desarrollo");
    }

    private void showMetas() {
        showAlert(Alert.AlertType.INFORMATION, "Metas de Ahorro", "Funcionalidad en desarrollo");
    }

    private void showCategorias() {
        showAlert(Alert.AlertType.INFORMATION, "Categorías", "Funcionalidad en desarrollo");
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}