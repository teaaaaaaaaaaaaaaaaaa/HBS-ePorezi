package rs.hbs.zarade.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import rs.hbs.zarade.HbsZaradeApp;
import rs.hbs.zarade.db.DatabaseConfig;
import rs.hbs.zarade.db.DatabaseConnection;
import rs.hbs.zarade.db.StaffDao;
import rs.hbs.zarade.domain.Staff;

import java.sql.Connection;
import java.util.List;
import java.util.Optional;

/**
 * Podešavanja sistema - Screen 18 iz UI specifikacije.
 */
public class PodesavanjaScreen {

    private BorderPane root;

    // Putanje
    private TextField exportPathField;
    private TextField dbCommonPathField;
    private TextField dbMainPathField;
    private TextField dbCalcPathField;

    // Konstante obračuna
    private TextField minCenaRadaField;
    private TextField poreskoOslobodjenjeField;

    // Korisnici
    private TableView<Staff> korisniciTable;

    public PodesavanjaScreen() {
        createUI();
        loadSettings();
    }

    private void createUI() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #F6F8FA;");

        HBox header = createHeader();
        root.setTop(header);

        TabPane tabPane = createTabs();
        tabPane.setStyle("-fx-background-color: white;");
        root.setCenter(tabPane);

        HBox footer = createFooter();
        root.setBottom(footer);
    }

    private HBox createHeader() {
        HBox header = new HBox();
        header.setStyle("-fx-background-color: #FFFFFF; -fx-border-color: transparent transparent #D0D7DE transparent; -fx-border-width: 0 0 1 0;");
        header.setPadding(new Insets(14, 16, 14, 16));
        header.setAlignment(Pos.CENTER_LEFT);

        Label titleLabel = new Label("Podešavanja");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        titleLabel.setTextFill(Color.web("#1F2328"));

        header.getChildren().add(titleLabel);
        return header;
    }

    private TabPane createTabs() {
        TabPane tabPane = new TabPane();

        Tab putanjeTab = new Tab("Putanje");
        putanjeTab.setClosable(false);
        putanjeTab.setContent(createPutanjeTab());

        Tab konstanteTab = new Tab("Konstante obračuna");
        konstanteTab.setClosable(false);
        konstanteTab.setContent(createKonstanteTab());

        Tab korisniciTab = new Tab("Korisnici");
        korisniciTab.setClosable(false);
        korisniciTab.setContent(createKorisniciTab());

        tabPane.getTabs().addAll(putanjeTab, konstanteTab, korisniciTab);
        return tabPane;
    }

    private VBox createPutanjeTab() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: white;");

        Label titleLabel = new Label("Putanje do fajlova i foldera");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        titleLabel.setTextFill(Color.web("#1F2328"));

        VBox exportBox = new VBox(5);
        Label exportLabel = new Label("Putanja za XML export:");
        exportLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        exportPathField = new TextField();
        exportPathField.setPromptText("C:\\PROJECTS\\HBS-ZARADE\\EXPORT-EPOREZI");
        HBox exportRow = new HBox(10);
        Button browseExportBtn = new Button("...");
        browseExportBtn.setOnAction(e -> browseFolder(exportPathField));
        HBox.setHgrow(exportPathField, Priority.ALWAYS);
        exportRow.getChildren().addAll(exportPathField, browseExportBtn);
        exportBox.getChildren().addAll(exportLabel, exportRow);

        Separator sep1 = new Separator();

        Label dbLabel = new Label("Putanje do baza podataka");
        dbLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        dbLabel.setTextFill(Color.web("#1F2328"));

        VBox commonBox = new VBox(5);
        Label commonLabel = new Label("Common baza (aj_fn_cmn.mdb):");
        commonLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        dbCommonPathField = new TextField();
        dbCommonPathField.setEditable(false);
        dbCommonPathField.setStyle("-fx-background-color: #F6F8FA; -fx-border-color: #D0D7DE; -fx-border-width: 1; -fx-border-radius: 6; -fx-text-fill: #848D97;");
        commonBox.getChildren().addAll(commonLabel, dbCommonPathField);

        VBox mainBox = new VBox(5);
        Label mainLabel = new Label("Main baza (data_zarade.mdb):");
        mainLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        mainLabel.setStyle("-fx-text-fill: #57606A;");
        dbMainPathField = new TextField();
        dbMainPathField.setEditable(false);
        dbMainPathField.setStyle("-fx-background-color: #F6F8FA; -fx-border-color: #D0D7DE; -fx-border-width: 1; -fx-border-radius: 6; -fx-text-fill: #848D97;");
        mainBox.getChildren().addAll(mainLabel, dbMainPathField);

        VBox calcBox = new VBox(5);
        Label calcLabel = new Label("Calc baza (obracun-honorara.mdb):");
        calcLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        calcLabel.setStyle("-fx-text-fill: #57606A;");
        dbCalcPathField = new TextField();
        dbCalcPathField.setEditable(false);
        dbCalcPathField.setStyle("-fx-background-color: #F6F8FA; -fx-border-color: #D0D7DE; -fx-border-width: 1; -fx-border-radius: 6; -fx-text-fill: #848D97;");
        calcBox.getChildren().addAll(calcLabel, dbCalcPathField);

        Label infoLabel = new Label(
                "Napomena: Putanje do baza podataka se podešavaju u fajlu app.local.properties"
        );
        infoLabel.setStyle("-fx-text-fill: #666;");

        content.getChildren().addAll(
                titleLabel, exportBox,
                sep1,
                dbLabel, commonBox, mainBox, calcBox, infoLabel
        );

        return content;
    }

    private VBox createKonstanteTab() {
        VBox content = new VBox(20);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: white;");

        Label titleLabel = new Label("Konstante za obračun");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        titleLabel.setTextFill(Color.web("#1F2328"));

        Label warningLabel = new Label(
                "Upozorenje: Promene ovih vrednosti utiču na sve buduće obračune!"
        );
        warningLabel.setStyle("-fx-background-color: #FFEBE9; -fx-padding: 10 14; -fx-text-fill: #A40E26; " +
                "-fx-border-color: transparent transparent transparent #CF222E; -fx-border-width: 0 0 0 3; -fx-font-size: 12px;");

        VBox minCenaBox = new VBox(5);
        Label minCenaLabel = new Label("Minimalna cena rada (RSD/sat):");
        minCenaLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        minCenaRadaField = new TextField();
        minCenaRadaField.setPromptText("npr. 271.00");
        minCenaBox.getChildren().addAll(minCenaLabel, minCenaRadaField);

        VBox poreskoBox = new VBox(5);
        Label poreskoLabel = new Label("Poresko oslobođenje (RSD):");
        poreskoLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
        poreskoOslobodjenjeField = new TextField();
        poreskoOslobodjenjeField.setPromptText("npr. 25000.00");
        poreskoBox.getChildren().addAll(poreskoLabel, poreskoOslobodjenjeField);

        Label stopeLabel = new Label("Poreske stope i doprinosi");
        stopeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        stopeLabel.setTextFill(Color.web("#1F2328"));

        TableView<String[]> stopeTable = new TableView<>();
        stopeTable.setPrefHeight(200);

        TableColumn<String[], String> tipCol = new TableColumn<>("Tip");
        tipCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[0]));
        tipCol.setPrefWidth(100);

        TableColumn<String[], String> porezCol = new TableColumn<>("Porez %");
        porezCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[1]));
        porezCol.setPrefWidth(80);

        TableColumn<String[], String> pioCol = new TableColumn<>("PIO %");
        pioCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[2]));
        pioCol.setPrefWidth(80);

        TableColumn<String[], String> zdrCol = new TableColumn<>("ZDR %");
        zdrCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[3]));
        zdrCol.setPrefWidth(80);

        TableColumn<String[], String> nezCol = new TableColumn<>("NEZ %");
        nezCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue()[4]));
        nezCol.setPrefWidth(80);

        stopeTable.getColumns().addAll(tipCol, porezCol, pioCol, zdrCol, nezCol);
        stopeTable.getItems().addAll(
                new String[]{"101 - Zarada", "10.00", "14.00 + 10.00", "5.15 + 5.15", "0.75"},
                new String[]{"405 - Autorski", "20.00", "-", "-", "-"},
                new String[]{"601 - Ugovor", "20.00", "14.00 + 10.00", "5.15 + 5.15", "-"}
        );

        Label stopeInfoLabel = new Label("Napomena: Stope se učitavaju iz tabele Obracun_Stope u bazi.");
        stopeInfoLabel.setStyle("-fx-text-fill: #666;");

        content.getChildren().addAll(
                titleLabel, warningLabel,
                minCenaBox, poreskoBox,
                new Separator(),
                stopeLabel, stopeTable, stopeInfoLabel
        );

        return content;
    }

    @SuppressWarnings("unchecked")
    private VBox createKorisniciTab() {
        VBox content = new VBox(16);
        content.setPadding(new Insets(30));
        content.setStyle("-fx-background-color: white;");

        Label titleLabel = new Label("Korisnički nalozi");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        titleLabel.setTextFill(Color.web("#1F2328"));

        // Napomena o pristupnim nivoima
        Label nivoiLabel = new Label("Nivoi pristupa:  4 = Ogranič. pristup (samo Primaoci prihoda)  |  8 = Moderator  |  9 = Administrator");
        nivoiLabel.setStyle("-fx-text-fill: #57606A; -fx-font-size: 11px;");

        korisniciTable = new TableView<>();
        korisniciTable.setPrefHeight(220);

        TableColumn<Staff, String> usernameCol = new TableColumn<>("Korisničko ime");
        usernameCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getUser()));
        usernameCol.setPrefWidth(200);

        TableColumn<Staff, String> levelCol = new TableColumn<>("Nivo");
        levelCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getLevel() != null ? String.valueOf(data.getValue().getLevel()) : ""));
        levelCol.setPrefWidth(60);

        TableColumn<Staff, String> levelNazivCol = new TableColumn<>("Opis nivoa");
        levelNazivCol.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getLevelNaziv()));
        levelNazivCol.setPrefWidth(140);

        korisniciTable.getColumns().addAll(usernameCol, levelCol, levelNazivCol);

        HBox buttonsBox = new HBox(10);
        Button addBtn = new Button("Dodaj korisnika");
        addBtn.setStyle("-fx-background-color: #0969DA; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 6 14; -fx-background-radius: 6; -fx-cursor: hand;");

        Button editBtn = new Button("Izmeni");
        editBtn.setStyle("-fx-background-color: white; -fx-text-fill: #57606A; -fx-font-size: 13px; -fx-padding: 6 14; -fx-background-radius: 6; -fx-border-color: #D0D7DE; -fx-border-width: 1; -fx-border-radius: 6; -fx-cursor: hand;");

        Button deleteBtn = new Button("Obriši");
        deleteBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #CF222E; -fx-font-size: 13px; -fx-padding: 6 14; -fx-cursor: hand;");

        Button refreshBtn = new Button("Osveži");
        refreshBtn.setStyle("-fx-background-color: white; -fx-text-fill: #57606A; -fx-font-size: 13px; -fx-padding: 6 14; -fx-background-radius: 6; -fx-border-color: #D0D7DE; -fx-border-width: 1; -fx-border-radius: 6; -fx-cursor: hand;");

        addBtn.setOnAction(e -> showAddUserDialog());
        editBtn.setOnAction(e -> {
            Staff selected = korisniciTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showInfo("Korisnici", "Izaberite korisnika za izmenu.");
                return;
            }
            showEditUserDialog(selected);
        });
        deleteBtn.setOnAction(e -> {
            Staff selected = korisniciTable.getSelectionModel().getSelectedItem();
            if (selected == null) {
                showInfo("Korisnici", "Izaberite korisnika za brisanje.");
                return;
            }
            deleteUser(selected);
        });
        refreshBtn.setOnAction(e -> loadKorisnici());

        buttonsBox.getChildren().addAll(addBtn, editBtn, deleteBtn, refreshBtn);

        Label infoLabel = new Label(
                "Napomena: Korisnički nalozi se čuvaju u tabeli Staff u aj_fn_cmn.mdb."
        );
        infoLabel.setStyle("-fx-text-fill: #666;");

        content.getChildren().addAll(titleLabel, nivoiLabel, korisniciTable, buttonsBox, infoLabel);

        return content;
    }

    private HBox createFooter() {
        HBox footer = new HBox(10);
        footer.setStyle("-fx-background-color: #F6F8FA; -fx-border-color: #D0D7DE transparent transparent transparent; -fx-border-width: 1 0 0 0;");
        footer.setPadding(new Insets(10, 20, 10, 20));
        footer.setAlignment(Pos.CENTER_RIGHT);

        Button nazadBtn = new Button("Nazad");
        nazadBtn.setStyle("-fx-background-color: white; -fx-text-fill: #57606A; -fx-font-size: 13px; -fx-padding: 7 16; -fx-background-radius: 6; -fx-border-color: #D0D7DE; -fx-border-width: 1; -fx-border-radius: 6; -fx-cursor: hand;");
        nazadBtn.setOnAction(e -> HbsZaradeApp.showMainMenu());

        Button saveBtn = new Button("Snimi");
        saveBtn.setStyle("-fx-background-color: #1A7F37; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 7 16; -fx-background-radius: 6; -fx-cursor: hand;");
        saveBtn.setOnAction(e -> saveSettings());

        footer.getChildren().addAll(nazadBtn, saveBtn);
        return footer;
    }

    private void loadSettings() {
        try {
            var dbConfig = DatabaseConfig.loadFromProperties();
            if (dbConfig != null) {
                dbCommonPathField.setText(dbConfig.getCommonDbPath());
                dbMainPathField.setText(dbConfig.getMainDbPath());
                dbCalcPathField.setText(dbConfig.getCalcDbPath());
            }
        } catch (Exception e) {
            System.err.println("Greška pri učitavanju konfiguracije: " + e.getMessage());
        }

        exportPathField.setText("C:\\PROJECTS\\HBS-ZARADE\\EXPORT-EPOREZI");
        minCenaRadaField.setText("271.00");
        poreskoOslobodjenjeField.setText("25000.00");

        loadKorisnici();
    }

    private void loadKorisnici() {
        if (korisniciTable == null) return;
        korisniciTable.getItems().clear();
        try {
            DatabaseConnection dbConn = DatabaseConnection.getInstance();
            if (dbConn == null) return;
            try (Connection conn = dbConn.getMainConnection()) {
                StaffDao dao = new StaffDao();
                List<Staff> list = dao.findAll(conn);
                korisniciTable.getItems().addAll(list);
            }
        } catch (Exception e) {
            System.err.println("Greška pri učitavanju korisnika: " + e.getMessage());
            showInfo("Greška", "Nije moguće učitati korisnike: " + e.getMessage());
        }
    }

    private void showAddUserDialog() {
        Dialog<Staff> dialog = new Dialog<>();
        dialog.setTitle("Dodaj korisnika");
        dialog.setHeaderText("Novi korisnički nalog");

        ButtonType saveType = new ButtonType("Snimi", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        VBox form = buildUserForm(null);
        dialog.getDialogPane().setContent(form);

        dialog.setResultConverter(bt -> {
            if (bt == saveType) {
                return extractFromForm(form, null);
            }
            return null;
        });

        Optional<Staff> result = dialog.showAndWait();
        result.ifPresent(staff -> {
            try {
                DatabaseConnection dbConn = DatabaseConnection.getInstance();
                if (dbConn == null) { showInfo("Greška", "Baza nije dostupna."); return; }
                try (Connection conn = dbConn.getMainConnection()) {
                    new StaffDao().save(conn, staff);
                }
                loadKorisnici();
            } catch (Exception e) {
                showInfo("Greška", "Greška pri dodavanju korisnika: " + e.getMessage());
            }
        });
    }

    private void showEditUserDialog(Staff staff) {
        int myLevel = HbsZaradeApp.getUserLevel();
        // Moderator (level 8) ne može da menja podatke i lozinku administratora (level 9)
        if (myLevel < 9 && staff.getLevel() != null && staff.getLevel() >= 9) {
            showInfo("Zabranjeno", "Moderator ne može da menja podatke ni lozinku administratorskog naloga.");
            return;
        }

        Dialog<Staff> dialog = new Dialog<>();
        dialog.setTitle("Izmeni korisnika");
        dialog.setHeaderText("Izmena korisničkog naloga: " + staff.getUser());

        ButtonType saveType = new ButtonType("Snimi", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveType, ButtonType.CANCEL);

        VBox form = buildUserForm(staff);
        dialog.getDialogPane().setContent(form);

        dialog.setResultConverter(bt -> {
            if (bt == saveType) {
                return extractFromForm(form, staff);
            }
            return null;
        });

        Optional<Staff> result = dialog.showAndWait();
        result.ifPresent(updatedStaff -> {
            try {
                DatabaseConnection dbConn = DatabaseConnection.getInstance();
                if (dbConn == null) { showInfo("Greška", "Baza nije dostupna."); return; }
                try (Connection conn = dbConn.getMainConnection()) {
                    String newPass = updatedStaff.getStaffLogin();
                    boolean changePass = newPass != null && !newPass.isEmpty();
                    new StaffDao().update(conn, updatedStaff, changePass);
                }
                loadKorisnici();
            } catch (Exception e) {
                showInfo("Greška", "Greška pri izmeni korisnika: " + e.getMessage());
            }
        });
    }

    private void deleteUser(Staff staff) {
        // Zaštita od brisanja administratora (level 9) od strane ne-admina
        int myLevel = HbsZaradeApp.getUserLevel();
        if (myLevel < 9 && staff.getLevel() != null && staff.getLevel() >= 9) {
            showInfo("Zabranjeno", "Nije dozvoljeno brisanje administratorskog naloga.");
            return;
        }
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Potvrda brisanja");
        confirm.setHeaderText("Obriši korisnika: " + staff.getUser() + "?");
        confirm.setContentText("Ova akcija je nepovratna.");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isEmpty() || result.get() != ButtonType.OK) return;

        try {
            DatabaseConnection dbConn = DatabaseConnection.getInstance();
            if (dbConn == null) { showInfo("Greška", "Baza nije dostupna."); return; }
            try (Connection conn = dbConn.getMainConnection()) {
                new StaffDao().delete(conn, staff.getIdUser());
            }
            loadKorisnici();
        } catch (Exception e) {
            showInfo("Greška", "Greška pri brisanju: " + e.getMessage());
        }
    }

    private VBox buildUserForm(Staff existing) {
        VBox form = new VBox(12);
        form.setPadding(new Insets(16));
        form.setPrefWidth(360);

        TextField userField = new TextField(existing != null ? existing.getUser() : "");
        userField.setPromptText("Korisničko ime");
        userField.setId("userField");

        PasswordField passField = new PasswordField();
        passField.setId("passField");
        passField.setPromptText(existing != null ? "(ostavite prazno da ne menjate lozinku)" : "Lozinka");

        ComboBox<Integer> levelCombo = new ComboBox<>();
        int myLevel = HbsZaradeApp.getUserLevel();
        // Administrator može da dodeli sve nivoe; Moderator ne može da dodeli nivo 9
        if (myLevel >= 9) {
            levelCombo.getItems().addAll(4, 8, 9);
        } else {
            levelCombo.getItems().addAll(4, 8);
        }
        levelCombo.setId("levelCombo");
        levelCombo.setCellFactory(lv -> new ListCell<>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(""); return; }
                setText(item + " — " + new Staff(null, null, null, item, null).getLevelNaziv());
            }
        });
        levelCombo.setButtonCell(new ListCell<>() {
            @Override protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(""); return; }
                setText(item + " — " + new Staff(null, null, null, item, null).getLevelNaziv());
            }
        });
        levelCombo.setValue(existing != null && existing.getLevel() != null ? existing.getLevel() : 4);

        form.getChildren().addAll(
                new Label("Korisničko ime:"), userField,
                new Label(existing != null ? "Nova lozinka (ostavite prazno za bez promene):" : "Lozinka:"), passField,
                new Label("Nivo pristupa:"), levelCombo
        );

        return form;
    }

    private Staff extractFromForm(VBox form, Staff existing) {
        TextField userField = (TextField) form.lookup("#userField");
        PasswordField passField = (PasswordField) form.lookup("#passField");
        ComboBox<?> levelCombo = (ComboBox<?>) form.lookup("#levelCombo");

        Staff s = existing != null ? existing : new Staff();
        if (userField != null) s.setUser(userField.getText().trim());
        if (passField != null) s.setStaffLogin(passField.getText());
        if (levelCombo != null && levelCombo.getValue() instanceof Integer) {
            s.setLevel((Integer) levelCombo.getValue());
        }
        return s;
    }

    private void saveSettings() {
        showInfo("Čuvanje", "Podešavanja bi bila sačuvana.\n(Funkcionalnost u razvoju)");
    }

    private void browseFolder(TextField targetField) {
        javafx.stage.DirectoryChooser chooser = new javafx.stage.DirectoryChooser();
        chooser.setTitle("Izaberite folder");

        java.io.File selectedDir = chooser.showDialog(HbsZaradeApp.getPrimaryStage());
        if (selectedDir != null) {
            targetField.setText(selectedDir.getAbsolutePath());
        }
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public BorderPane getRoot() {
        return root;
    }
}
