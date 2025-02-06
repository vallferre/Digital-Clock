package com.example.digitalclock;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class mainClock extends Application {
    private Label clockLabel;
    private SimpleDateFormat sdf;
    private Map<String, String> timeZones; // Mapa para almacenar las zonas horarias

    @Override
    public void start(Stage primaryStage) {
        clockLabel = new Label();
        clockLabel.getStyleClass().add("clock-label");

        // Inicializar el SimpleDateFormat aquí
        sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC")); // Inicializar con UTC

        // Inicializar el mapa de zonas horarias
        timeZones = new HashMap<>();
        timeZones.put("Buenos Aires - UTC-3", "GMT-3");
        // Puedes seguir añadiendo más zonas horarias aquí
        timeZones.put("New York - UTC-5", "GMT-5");
        timeZones.put("London - UTC", "UTC");

        // Crear la raíz con BorderPane
        BorderPane root = new BorderPane(clockLabel);

        // Crear la barra de menú
        MenuBar menuBar = new MenuBar();

        // Crear un menú con opciones
        Menu menu = new Menu("Opciones");

        // Crear los MenuItems dinámicamente
        for (Map.Entry<String, String> entry : timeZones.entrySet()) {
            MenuItem item = new MenuItem(entry.getKey());
            item.setOnAction(event -> changeTimeZone(entry.getValue())); // Cambiar zona horaria
            menu.getItems().add(item);
        }

        // Añadir el menú a la barra de menú
        menuBar.getMenus().add(menu);

        // Colocar el menuBar en la parte superior del BorderPane
        root.setTop(menuBar);

        // Crear la escena y aplicar el estilo CSS
        Scene scene = new Scene(root, 300, 150);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        // Configurar la ventana
        primaryStage.setTitle("Reloj Digital");
        primaryStage.setScene(scene);
        primaryStage.show();

        // Iniciar el reloj
        startClock();
    }

    private void startClock() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            String currentTime = sdf.format(new Date());
            clockLabel.setText(currentTime);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void changeTimeZone(String timeZoneID) {
        sdf.setTimeZone(TimeZone.getTimeZone(timeZoneID)); // Cambiar zona horaria
    }

    public static void main(String[] args) {
        launch(args);
    }
}