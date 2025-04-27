package com.gestiongastos.sistema.controller;

import com.gestiongastos.sistema.model.Gasto;
import com.gestiongastos.sistema.service.GastoService;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.Pane;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MainController {

    private final GastoService gastoService;

    @FXML private Label lblTotalGastos;
    @FXML private Label lblGastosHormiga;
    @FXML private Label lblGastosBase;
    @FXML private Pane pieChartContainer;
    @FXML private Pane barChartContainer;
    @FXML private TableView<Gasto> tblUltimosGastos;
    @FXML private TableColumn<Gasto, String> colFecha;
    @FXML private TableColumn<Gasto, String> colDescripcion;
    @FXML private TableColumn<Gasto, String> colCategoria;
    @FXML private TableColumn<Gasto, Double> colMonto;
    @FXML private TableColumn<Gasto, String> colTipo;

    @FXML
    public void initialize() {
        actualizarDashboard();
    }

    private void actualizarDashboard() {
        // TODO: Implementar la lógica para actualizar el dashboard
    }

    @FXML
    public void cerrarSesion() {
        // TODO: Implementar cierre de sesión
    }

    @FXML
    public void salir() {
        System.exit(0);
    }

    @FXML
    public void nuevoGasto() {
        // TODO: Abrir ventana de nuevo gasto
    }

    @FXML
    public void verGastos() {
        // TODO: Abrir ventana de lista de gastos
    }

    @FXML
    public void nuevaMeta() {
        // TODO: Abrir ventana de nueva meta
    }

    @FXML
    public void verMetas() {
        // TODO: Abrir ventana de lista de metas
    }

    @FXML
    public void verResumenMensual() {
        // TODO: Abrir ventana de resumen mensual
    }

    @FXML
    public void verGastosPorCategoria() {
        // TODO: Abrir ventana de gastos por categoría
    }
}