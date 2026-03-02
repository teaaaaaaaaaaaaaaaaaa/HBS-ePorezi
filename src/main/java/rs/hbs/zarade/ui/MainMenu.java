package rs.hbs.zarade.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import rs.hbs.zarade.HbsZaradeApp;

/**
 * Glavni meni - Screen 2 iz UI specifikacije.
 */
public class MainMenu {

    private BorderPane root;

    public MainMenu() {
        createUI();
    }

    private void createUI() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #7b1fa2;"); // Ljubičasta

        // Leva vertikalna traka sa info
        VBox leftBar = createLeftBar();
        root.setLeft(leftBar);

        // Centralni deo - dugmad menija
        VBox center = createCenterMenu();
        root.setCenter(center);
    }

    private VBox createLeftBar() {
        VBox leftBar = new VBox();
        leftBar.setPrefWidth(200);
        leftBar.setStyle("-fx-background-color: #9c4dcc;"); // Svetlija ljubičasta
        leftBar.setPadding(new Insets(20));
        leftBar.setAlignment(Pos.BOTTOM_CENTER);

        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Info o korisniku
        VBox userInfo = new VBox(5);
        userInfo.setAlignment(Pos.CENTER);

        Label userLabel = new Label(HbsZaradeApp.getCurrentUser());
        userLabel.setTextFill(Color.WHITE);
        userLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

        Label levelLabel = new Label("Level: " + HbsZaradeApp.getUserLevel());
        levelLabel.setTextFill(Color.WHITE);
        levelLabel.setFont(Font.font("System", 12));

        userInfo.getChildren().addAll(userLabel, levelLabel);

        leftBar.getChildren().addAll(spacer, userInfo);
        return leftBar;
    }

    private VBox createCenterMenu() {
        VBox center = new VBox(30);
        center.setAlignment(Pos.CENTER);
        center.setPadding(new Insets(50));

        // Naslov
        Label titleLabel = new Label("GLAVNI MENI");
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 28));

        // Dugmad menija
        Button sifarniciBtn = createMenuButton("Šifrarnici");
        Button obracuniBtn = createMenuButton("Obračuni");
        Button podesavanjaBtn = createMenuButton("Podešavanja");

        // Separator
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Exit dugme
        Button exitBtn = new Button("E X I T");
        exitBtn.setStyle("-fx-background-color: white; -fx-text-fill: #7b1fa2; " +
                "-fx-font-size: 16px; -fx-font-weight: bold; " +
                "-fx-padding: 15 60; -fx-background-radius: 5;");
        exitBtn.setOnAction(e -> HbsZaradeApp.showLoginScreen());

        // Akcije
        sifarniciBtn.setOnAction(e -> showSifarniciMenu());
        obracuniBtn.setOnAction(e -> showObracuniScreen());
        podesavanjaBtn.setOnAction(e -> showPodesavanjaScreen());

        Region gap = new Region();
        gap.setPrefHeight(30);
        center.getChildren().addAll(
                titleLabel,
                gap,
                sifarniciBtn,
                obracuniBtn,
                podesavanjaBtn,
                spacer,
                exitBtn
        );

        return center;
    }

    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setPrefWidth(300);
        btn.setPrefHeight(60);
        btn.setStyle("-fx-background-color: white; -fx-text-fill: #333; " +
                "-fx-font-size: 18px; -fx-font-weight: bold; " +
                "-fx-border-color: #ccc; -fx-border-width: 1; " +
                "-fx-background-radius: 5; -fx-border-radius: 5;");

        btn.setOnMouseEntered(e ->
                btn.setStyle("-fx-background-color: #e1bee7; -fx-text-fill: #333; " +
                        "-fx-font-size: 18px; -fx-font-weight: bold; " +
                        "-fx-border-color: #7b1fa2; -fx-border-width: 2; " +
                        "-fx-background-radius: 5; -fx-border-radius: 5;"));

        btn.setOnMouseExited(e ->
                btn.setStyle("-fx-background-color: white; -fx-text-fill: #333; " +
                        "-fx-font-size: 18px; -fx-font-weight: bold; " +
                        "-fx-border-color: #ccc; -fx-border-width: 1; " +
                        "-fx-background-radius: 5; -fx-border-radius: 5;"));

        return btn;
    }

    private void showSifarniciMenu() {
        SifarniciMenu sifarniciMenu = new SifarniciMenu();
        root.setCenter(sifarniciMenu.getContent());
    }

    private void showObracuniScreen() {
        ObracuniScreen obracuniScreen = new ObracuniScreen();
        HbsZaradeApp.getPrimaryStage().getScene().setRoot(obracuniScreen.getRoot());
    }

    private void showPodesavanjaScreen() {
        PodesavanjaScreen podesavanjaScreen = new PodesavanjaScreen();
        HbsZaradeApp.getPrimaryStage().getScene().setRoot(podesavanjaScreen.getRoot());
    }

    public BorderPane getRoot() {
        return root;
    }
}
