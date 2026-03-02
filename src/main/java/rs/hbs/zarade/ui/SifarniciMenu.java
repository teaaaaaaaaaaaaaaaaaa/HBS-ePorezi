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
 * Podmeni Šifrarnici - Screen 3 iz UI specifikacije.
 */
public class SifarniciMenu {

    private VBox content;

    public SifarniciMenu() {
        createUI();
    }

    private void createUI() {
        content = new VBox(30);
        content.setAlignment(Pos.CENTER);
        content.setPadding(new Insets(50));
        content.setStyle("-fx-background-color: #7b1fa2;");

        // Naslov
        Label titleLabel = new Label("ŠIFRARNICI");
        titleLabel.setTextFill(Color.WHITE);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 28));

        // Dugmad
        Button primaociBtn = createMenuButton("Primaoci prihoda");
        Button isplatiocBtn = createMenuButton("Isplatilac");

        // Spacer
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // Nazad dugme
        Button nazadBtn = new Button("<<< NAZAD");
        nazadBtn.setStyle("-fx-background-color: white; -fx-text-fill: #7b1fa2; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; " +
                "-fx-padding: 10 40; -fx-background-radius: 5;");

        // Akcije
        primaociBtn.setOnAction(e -> showPrimaociLista());
        isplatiocBtn.setOnAction(e -> showIsplatiocForm());
        nazadBtn.setOnAction(e -> HbsZaradeApp.showMainMenu());

        Region gap = new Region();
        gap.setPrefHeight(30);
        content.getChildren().addAll(
                titleLabel,
                gap,
                primaociBtn,
                isplatiocBtn,
                spacer,
                nazadBtn
        );
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

    private void showPrimaociLista() {
        PrimaociListaScreen primaociScreen = new PrimaociListaScreen();
        HbsZaradeApp.getPrimaryStage().getScene().setRoot(primaociScreen.getRoot());
    }

    private void showIsplatiocForm() {
        IsplatiocScreen isplatiocScreen = new IsplatiocScreen();
        HbsZaradeApp.getPrimaryStage().getScene().setRoot(isplatiocScreen.getRoot());
    }

    public VBox getContent() {
        return content;
    }
}
