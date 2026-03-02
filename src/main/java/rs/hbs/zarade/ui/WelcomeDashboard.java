package rs.hbs.zarade.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import rs.hbs.zarade.HbsZaradeApp;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Welcome/dashboard panel — prikazuje se u centru AppLayout-a kad nijedan ekran nije aktivan.
 */
public class WelcomeDashboard {

    private VBox root;

    public WelcomeDashboard() {
        createUI();
    }

    private void createUI() {
        root = new VBox(24);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(60));
        root.setStyle("-fx-background-color: #F6F8FA;");

        // Logo / naslov
        Label appLabel = new Label("ePorezi");
        appLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 44));
        appLabel.setTextFill(Color.web("#0969DA"));

        Label subtitleLabel = new Label("HBS Zarade — Obračun honorara i isplata");
        subtitleLabel.setFont(Font.font("Segoe UI", 17));
        subtitleLabel.setTextFill(Color.web("#57606A"));

        // Korisnik i datum
        String username = HbsZaradeApp.getCurrentUser();
        String datum = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy."));

        Label welcomeLabel = new Label("Dobrodošli, " + (username != null ? username : "korisnik") + "!");
        welcomeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 19));
        welcomeLabel.setTextFill(Color.web("#1F2328"));

        Label dateLabel = new Label(datum);
        dateLabel.setFont(Font.font("Segoe UI", 14));
        dateLabel.setTextFill(Color.web("#848D97"));

        // Instrukcija
        Label hintLabel = new Label("Izaberite opciju iz bočnog menija s leve strane.");
        hintLabel.setFont(Font.font("Segoe UI", 14));
        hintLabel.setTextFill(Color.web("#57606A"));
        hintLabel.setStyle("-fx-background-color: #EEF2F7; -fx-padding: 14 28; -fx-background-radius: 8; " +
                "-fx-border-color: #D0D7DE; -fx-border-width: 1; -fx-border-radius: 8;");

        Region spacer = new Region();
        spacer.setPrefHeight(8);
        root.getChildren().addAll(appLabel, subtitleLabel, spacer, welcomeLabel, dateLabel, hintLabel);
    }

    public VBox getRoot() {
        return root;
    }
}
