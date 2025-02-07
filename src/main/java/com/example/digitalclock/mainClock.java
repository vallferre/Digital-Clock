package com.example.digitalclock;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class mainClock extends Application {
    private Label clockLabel;
    private Label locationLabel;
    private DateTimeFormatter formatter;
    private ZoneId currentZone = ZoneId.of("UTC");
    private ObservableList<String> timeZones;
    private ComboBox<String> timeZoneComboBox;

    @Override
    public void start(Stage primaryStage) {
        clockLabel = new Label();
        clockLabel.getStyleClass().add("clock-label");

        locationLabel = new Label("Zona horaria: UTC");
        locationLabel.getStyleClass().add("location-label");

        formatter = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(currentZone);

        timeZones = FXCollections.observableArrayList(getFormattedTimeZones());

        timeZoneComboBox = new ComboBox<>();
        timeZoneComboBox.setEditable(true);
        timeZoneComboBox.setItems(timeZones);
        timeZoneComboBox.getEditor().setPromptText("Seleccione un horario");

        timeZoneComboBox.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue.isEmpty()) {
                timeZoneComboBox.getEditor().setPromptText("Seleccione un horario");
            } else {
                filterTimeZones(newValue);
            }
        });

        timeZoneComboBox.setOnAction(event -> {
            String selectedZone = timeZoneComboBox.getValue();
            if (selectedZone != null && timeZones.contains(selectedZone)) {
                String zoneId = selectedZone.substring(selectedZone.indexOf(']') + 2);
                changeTimeZone(zoneId);
            }
        });

        BorderPane root = new BorderPane();
        VBox topBox = new VBox(timeZoneComboBox);
        VBox centerBox = new VBox(clockLabel, locationLabel);
        topBox.setPadding(new Insets(10));
        centerBox.setPadding(new Insets(10));
        root.setTop(topBox);
        root.setCenter(centerBox);

        Scene scene = new Scene(root, 350, 200);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());

        primaryStage.setTitle("Reloj Digital");
        primaryStage.setScene(scene);
        primaryStage.show();

        startClock();
    }

    private void startClock() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            String currentTime = formatter.format(Instant.now());
            clockLabel.setText(currentTime);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void changeTimeZone(String timeZoneID) {
        currentZone = ZoneId.of(timeZoneID);
        formatter = DateTimeFormatter.ofPattern("HH:mm:ss").withZone(currentZone);

        String location = formatLocation(timeZoneID);
        locationLabel.setText("Zona horaria: " + location);
    }

    private List<String> getFormattedTimeZones() {
        return ZoneId.getAvailableZoneIds().stream()
                .sorted(Comparator.comparing(zone -> ZoneId.of(zone).getRules().getOffset(Instant.now())))
                .map(zone -> {
                    ZoneId z = ZoneId.of(zone);
                    String offset = z.getRules().getOffset(Instant.now()).toString();
                    return "[" + offset + "] " + zone;
                })
                .collect(Collectors.toList());
    }

    private void filterTimeZones(String search) {
        ObservableList<String> filteredList = FXCollections.observableArrayList();

        for (String zone : timeZones) {
            if (zone.toLowerCase().contains(search.toLowerCase())) {
                filteredList.add(zone);
            }
        }

        timeZoneComboBox.setItems(filteredList);
    }

    private String formatLocation(String timeZoneID) {
        String[] parts = timeZoneID.split("/");
        if (parts.length == 1) {
            return timeZoneID;
        }

        String formattedCity = parts[parts.length - 1].replace("_", " ");
        String formattedCountry = parts[0].replace("_", " ");

        return formattedCity + ", " + formattedCountry;
    }

    public static void main(String[] args) {
        launch(args);
    }
}