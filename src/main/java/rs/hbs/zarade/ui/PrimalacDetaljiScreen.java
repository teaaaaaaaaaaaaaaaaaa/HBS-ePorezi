package rs.hbs.zarade.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import rs.hbs.zarade.HbsZaradeApp;
import rs.hbs.zarade.db.DatabaseConnection;
import rs.hbs.zarade.db.ObracunDao;
import rs.hbs.zarade.db.ObracunDefinicijaDao;
import rs.hbs.zarade.db.OpstinaDao;
import rs.hbs.zarade.db.PrimalacDao;
import rs.hbs.zarade.db.VrstaPrimaocaDao;
import rs.hbs.zarade.domain.Obracun;
import rs.hbs.zarade.domain.ObracunDefinicija;
import rs.hbs.zarade.domain.Opstina;
import rs.hbs.zarade.domain.Primalac;
import rs.hbs.zarade.domain.VrstaPrimaoca;
import rs.hbs.zarade.reporting.PdfReporter;
import rs.hbs.zarade.reporting.XmlExporter;
import rs.hbs.zarade.validation.IdentifikatorValidator;

import javafx.application.Platform;
import javafx.stage.Window;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Detalji primaoca prihoda - Screen 5 iz UI specifikacije.
 */
public class PrimalacDetaljiScreen {

    private static final String COLOR_VIEW    = "#343942"; // tamno siva: pogled
    private static final String COLOR_NEW     = "#0969DA"; // plava: novi
    private static final String COLOR_DIRTY   = "#9A6700"; // amber: nesačuvane izmene
    private static final String COLOR_SAVED   = "#1A7F37"; // zelena: sačuvano

    private BorderPane root;
    private HBox header;
    private Button saveBtn;
    private Button cancelBtn;

    private Primalac primalac;
    private boolean isNew;
    private boolean hasUnsavedChanges = false;
    private boolean loading = false;

    private TextField idField;
    private TextField prezimeField;
    private TextField imeField;
    private TextField adresaField;
    private TextField pbGradField;
    private ComboBox<Opstina> sifraOpstineCombo;
    private ComboBox<Opstina> nazivOpstineCombo;
    private TextField mbField;
    private TextField mobilniField;
    private TextField emailField;
    private ComboBox<String> statusCombo;
    private TextField procenatField;
    private ComboBox<VrstaPrimaoca> vrstaPrimaocaCombo;
    private Label mbValidationLabel;

    private List<VrstaPrimaoca> vrstePrimaoca = new ArrayList<>();
    private List<Opstina> opstine = new ArrayList<>();

    public PrimalacDetaljiScreen(Primalac primalac) {
        this.primalac = primalac;
        this.isNew = (primalac == null);
        if (this.isNew) {
            this.primalac = new Primalac();
        }
        loadLookupData();
        createUI();
        populateFields();
    }

    private void loadLookupData() {
        try {
            DatabaseConnection dbConn = DatabaseConnection.getInstance();
            Connection conn = dbConn.getMainConnection();
            if (conn != null) {
                VrstaPrimaocaDao vpDao = new VrstaPrimaocaDao();
                vrstePrimaoca = vpDao.findAll(conn);
                OpstinaDao oDao = new OpstinaDao();
                opstine = oDao.findAll(conn);
            }
        } catch (Exception e) {
            System.err.println("Greska pri ucitavanju sifarnika: " + e.getMessage());
        }
    }

    private void createUI() {
        root = new BorderPane();
        root.setStyle("-fx-background-color: #F6F8FA;");
        header = createHeader();
        root.setTop(header);
        HBox content = createFormContent();
        root.setCenter(content);
        HBox footer = createFooter();
        root.setBottom(footer);
    }

    private HBox createHeader() {
        HBox hdr = new HBox(20);
        hdr.setStyle("-fx-background-color: " + (isNew ? COLOR_NEW : COLOR_VIEW) + ";");
        hdr.setPadding(new Insets(15));
        hdr.setAlignment(Pos.CENTER_LEFT);
        Label titleLabel = new Label(isNew ? "Novi primalac prihoda" : "Primalac prihoda");
        titleLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.WHITE);
        if (!isNew) {
            Label idLabel = new Label("ID: " + primalac.getIdZaposleni());
            idLabel.setTextFill(Color.web("#E6EDF3"));
            idLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
            Label nameLabel = new Label(primalac.getPrezime() + " " + primalac.getIme());
            nameLabel.setTextFill(Color.web("#E6EDF3"));
            nameLabel.setFont(Font.font("Segoe UI", 13));
            Region headerSpacer = new Region();
            HBox.setHgrow(headerSpacer, Priority.ALWAYS);
            Label sepLabel = new Label(" | ");
            sepLabel.setTextFill(Color.WHITE);
            hdr.getChildren().addAll(titleLabel, headerSpacer, idLabel, sepLabel, nameLabel);
        } else {
            hdr.getChildren().add(titleLabel);
        }
        return hdr;
    }

    private void setHeaderColor(String color) {
        if (header != null) {
            header.setStyle("-fx-background-color: " + color + ";");
        }
    }

    private HBox createFormContent() {
        HBox content = new HBox(30);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.TOP_CENTER);
        VBox leftColumn = createLeftColumn();
        VBox rightColumn = createRightColumn();
        content.getChildren().addAll(leftColumn, rightColumn);
        return content;
    }

    private VBox createLeftColumn() {
        VBox column = new VBox(10);
        column.setPrefWidth(550);
        column.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 8; " +
                "-fx-border-color: #D0D7DE; -fx-border-width: 1; -fx-border-radius: 8;");

        Label sectionLabel = new Label("Lični podaci");
        sectionLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        sectionLabel.setTextFill(Color.web("#1F2328"));

        idField = new TextField();
        idField.setEditable(false);
        idField.setStyle("-fx-background-color: #F6F8FA; -fx-border-color: #D0D7DE; -fx-border-width: 1; -fx-border-radius: 6; -fx-background-radius: 6; -fx-text-fill: #848D97;");

        prezimeField = new TextField();
        prezimeField.setStyle("-fx-background-color: white; -fx-border-color: #D0D7DE; -fx-border-width: 1; -fx-border-radius: 6; -fx-background-radius: 6;");
        prezimeField.setPromptText("Prezime primaoca");

        imeField = new TextField();
        imeField.setStyle("-fx-background-color: white; -fx-border-color: #D0D7DE; -fx-border-width: 1; -fx-border-radius: 6; -fx-background-radius: 6;");
        imeField.setPromptText("Ime primaoca");

        adresaField = new TextField();
        adresaField.setStyle("-fx-background-color: white; -fx-border-color: #D0D7DE; -fx-border-width: 1; -fx-border-radius: 6; -fx-background-radius: 6;");

        pbGradField = new TextField();
        pbGradField.setStyle("-fx-background-color: white; -fx-border-color: #D0D7DE; -fx-border-width: 1; -fx-border-radius: 6; -fx-background-radius: 6;");

        sifraOpstineCombo = new ComboBox<>();
        sifraOpstineCombo.setPrefWidth(100);
        sifraOpstineCombo.setStyle("-fx-font-size: 13px; -fx-background-color: white; -fx-border-color: #D0D7DE; -fx-border-radius: 6;");
        for (Opstina o : opstine) sifraOpstineCombo.getItems().add(o);
        sifraOpstineCombo.setCellFactory(lv -> new ListCell<Opstina>() {
            @Override protected void updateItem(Opstina item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getSifra());
            }
        });
        sifraOpstineCombo.setButtonCell(new ListCell<Opstina>() {
            @Override protected void updateItem(Opstina item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getSifra());
            }
        });

        nazivOpstineCombo = new ComboBox<>();
        nazivOpstineCombo.setPrefWidth(200);
        nazivOpstineCombo.setStyle("-fx-font-size: 13px; -fx-background-color: white; -fx-border-color: #D0D7DE; -fx-border-radius: 6;");
        for (Opstina o : opstine) nazivOpstineCombo.getItems().add(o);
        nazivOpstineCombo.setCellFactory(lv -> new ListCell<Opstina>() {
            @Override protected void updateItem(Opstina item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNaziv());
            }
        });
        nazivOpstineCombo.setButtonCell(new ListCell<Opstina>() {
            @Override protected void updateItem(Opstina item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getNaziv());
            }
        });

        // Sinhronizacija dva combo-a za opštinu
        sifraOpstineCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals(nazivOpstineCombo.getValue()))
                nazivOpstineCombo.setValue(newVal);
        });
        nazivOpstineCombo.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newVal.equals(sifraOpstineCombo.getValue()))
                sifraOpstineCombo.setValue(newVal);
        });

        // Scroll do selektovane opštine pri otvaranju comboya
        sifraOpstineCombo.setOnShowing(e -> Platform.runLater(() ->
                scrollComboToSelected(sifraOpstineCombo)));
        nazivOpstineCombo.setOnShowing(e -> Platform.runLater(() ->
                scrollComboToSelected(nazivOpstineCombo)));

        VBox mbBox = new VBox(4);
        Label mbLabel = new Label("MB/JMBG: *");
        mbLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        mbLabel.setStyle("-fx-text-fill: #0969DA;");
        mbField = new TextField();
        mbField.setStyle("-fx-background-color: white; -fx-border-color: #D0D7DE; -fx-border-width: 1; -fx-border-radius: 6; -fx-background-radius: 6;");
        mbValidationLabel = new Label();
        mbValidationLabel.setFont(Font.font("Segoe UI", 11));
        mbField.textProperty().addListener((obs, oldVal, newVal) -> validateMb(newVal));
        mbBox.getChildren().addAll(mbLabel, mbField, mbValidationLabel);

        mobilniField = new TextField();
        mobilniField.setStyle("-fx-background-color: white; -fx-border-color: #D0D7DE; -fx-border-width: 1; -fx-border-radius: 6; -fx-background-radius: 6;");

        emailField = new TextField();
        emailField.setStyle("-fx-background-color: white; -fx-border-color: #D0D7DE; -fx-border-width: 1; -fx-border-radius: 6; -fx-background-radius: 6;");

        HBox statusBox = new HBox(10);
        statusBox.setAlignment(Pos.CENTER_LEFT);
        Label statusLabel = new Label("Status:");
        statusLabel.setPrefWidth(120);
        statusLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        statusLabel.setStyle("-fx-text-fill: #57606A;");
        statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Aktivan", "Neaktivan");
        statusCombo.setValue("Aktivan");
        statusBox.getChildren().addAll(statusLabel, statusCombo);

        HBox opstinaRow = new HBox(10);
        opstinaRow.setAlignment(Pos.CENTER_LEFT);
        Label opstinaLabel = new Label("Opština:");
        opstinaLabel.setPrefWidth(120);
        opstinaLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        opstinaLabel.setStyle("-fx-text-fill: #57606A;");
        opstinaRow.getChildren().addAll(opstinaLabel, sifraOpstineCombo, nazivOpstineCombo);

        column.getChildren().addAll(
                sectionLabel, new Separator(),
                createFieldRow("ID:", idField),
                createFieldRow("Prezime: *", prezimeField),
                createFieldRow("Ime: *", imeField),
                createFieldRow("Adresa:", adresaField),
                createFieldRow("PB/Grad:", pbGradField),
                opstinaRow, mbBox,
                createFieldRow("Mobilni:", mobilniField),
                createFieldRow("Email:", emailField),
                statusBox
        );

        // Dirty tracking
        prezimeField.textProperty().addListener((obs, o, n) -> markDirty());
        imeField.textProperty().addListener((obs, o, n) -> markDirty());
        adresaField.textProperty().addListener((obs, o, n) -> markDirty());
        pbGradField.textProperty().addListener((obs, o, n) -> markDirty());
        mbField.textProperty().addListener((obs, o, n) -> markDirty());
        mobilniField.textProperty().addListener((obs, o, n) -> markDirty());
        emailField.textProperty().addListener((obs, o, n) -> markDirty());
        sifraOpstineCombo.valueProperty().addListener((obs, o, n) -> markDirty());
        statusCombo.valueProperty().addListener((obs, o, n) -> markDirty());

        return column;
    }

    private VBox createRightColumn() {
        VBox column = new VBox(10);
        column.setPrefWidth(400);
        column.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 8; " +
                "-fx-border-color: #D0D7DE; -fx-border-width: 1; -fx-border-radius: 8;");

        TabPane tabPane = new TabPane();
        Tab zaposlenjeTab = new Tab("Zaposlenje");
        zaposlenjeTab.setClosable(false);

        VBox zaposlenjeContent = new VBox(15);
        zaposlenjeContent.setPadding(new Insets(15));
        zaposlenjeContent.setStyle("-fx-background-color: white;");
        zaposlenjeContent.setMaxWidth(Double.MAX_VALUE);

        HBox procenatBox = new HBox(10);
        procenatBox.setAlignment(Pos.CENTER_LEFT);
        Label procenatLabel = new Label("% zaposlenja:");
        procenatLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        procenatLabel.setTextFill(Color.web("#57606A"));
        procenatLabel.setPrefWidth(120);
        procenatField = new TextField("100");
        procenatField.setPrefWidth(60);
        procenatBox.getChildren().addAll(procenatLabel, procenatField);

        vrstaPrimaocaCombo = new ComboBox<>();
        vrstaPrimaocaCombo.setMaxWidth(Double.MAX_VALUE);
        vrstaPrimaocaCombo.setVisibleRowCount(13);
        for (VrstaPrimaoca vp : vrstePrimaoca) vrstaPrimaocaCombo.getItems().add(vp);
        vrstaPrimaocaCombo.setCellFactory(lv -> new ListCell<VrstaPrimaoca>() {
            @Override protected void updateItem(VrstaPrimaoca item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "" : item.getDisplayName());
            }
        });
        vrstaPrimaocaCombo.setButtonCell(new ListCell<VrstaPrimaoca>() {
            @Override protected void updateItem(VrstaPrimaoca item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? "Izaberite vrstu primaoca..." : item.getDisplayName());
            }
        });

        Label vrstaPrimaocaLabel = new Label("Vrsta primaoca:");
        vrstaPrimaocaLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        vrstaPrimaocaLabel.setTextFill(Color.web("#57606A"));

        zaposlenjeContent.getChildren().addAll(
                procenatBox,
                vrstaPrimaocaLabel,
                vrstaPrimaocaCombo
        );

        // Dirty tracking
        procenatField.textProperty().addListener((obs, o, n) -> markDirty());
        vrstaPrimaocaCombo.valueProperty().addListener((obs, o, n) -> markDirty());

        zaposlenjeTab.setContent(zaposlenjeContent);
        tabPane.getTabs().add(zaposlenjeTab);
        column.getChildren().add(tabPane);
        return column;
    }

    private HBox createFooter() {
        HBox footer = new HBox(10);
        footer.setStyle("-fx-background-color: #F6F8FA; -fx-border-color: #D0D7DE transparent transparent transparent; -fx-border-width: 1 0 0 0;");
        footer.setPadding(new Insets(10, 20, 10, 20));
        footer.setAlignment(Pos.CENTER_LEFT);

        saveBtn = new Button("Snimi");
        saveBtn.setStyle("-fx-background-color: #1A7F37; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 7 18; -fx-background-radius: 6; -fx-cursor: hand;");
        saveBtn.setOnMouseEntered(e -> saveBtn.setStyle("-fx-background-color: #116329; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 7 18; -fx-background-radius: 6; -fx-cursor: hand;"));
        saveBtn.setOnMouseExited(e -> saveBtn.setStyle("-fx-background-color: #1A7F37; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 7 18; -fx-background-radius: 6; -fx-cursor: hand;"));
        saveBtn.setOnAction(e -> saveData());
        saveBtn.setDisable(!isNew);

        cancelBtn = new Button("Poništi");
        cancelBtn.setStyle("-fx-background-color: white; -fx-text-fill: #57606A; -fx-font-size: 13px; -fx-padding: 7 16; -fx-background-radius: 6; -fx-border-color: #D0D7DE; -fx-border-width: 1; -fx-border-radius: 6; -fx-cursor: hand;");
        cancelBtn.setOnAction(e -> {
            populateFields();
        });
        cancelBtn.setDisable(true);

        Button pppPoBtn = new Button("PPP-PO");
        pppPoBtn.setStyle("-fx-background-color: #9A6700; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 7 14; -fx-background-radius: 6; -fx-cursor: hand;");
        pppPoBtn.setOnMouseEntered(e -> pppPoBtn.setStyle("-fx-background-color: #7A5200; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 7 14; -fx-background-radius: 6; -fx-cursor: hand;"));
        pppPoBtn.setOnMouseExited(e -> pppPoBtn.setStyle("-fx-background-color: #9A6700; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 7 14; -fx-background-radius: 6; -fx-cursor: hand;"));
        pppPoBtn.setOnAction(e -> doPppPo());

        Button stampaBtn = new Button("Štampa");
        stampaBtn.setStyle("-fx-background-color: #0969DA; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 7 14; -fx-background-radius: 6; -fx-cursor: hand;");
        stampaBtn.setOnMouseEntered(e -> stampaBtn.setStyle("-fx-background-color: #0550AE; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 7 14; -fx-background-radius: 6; -fx-cursor: hand;"));
        stampaBtn.setOnMouseExited(e -> stampaBtn.setStyle("-fx-background-color: #0969DA; -fx-text-fill: white; -fx-font-size: 13px; -fx-padding: 7 14; -fx-background-radius: 6; -fx-cursor: hand;"));
        stampaBtn.setOnAction(e -> doStampa());

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button closeBtn = new Button("Zatvori");
        closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #CF222E; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 7 16; -fx-background-radius: 6; -fx-cursor: hand;");
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle("-fx-background-color: #FFEBE9; -fx-text-fill: #CF222E; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 7 16; -fx-background-radius: 6; -fx-cursor: hand;"));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle("-fx-background-color: transparent; -fx-text-fill: #CF222E; -fx-font-weight: bold; -fx-font-size: 13px; -fx-padding: 7 16; -fx-background-radius: 6; -fx-cursor: hand;"));
        closeBtn.setOnAction(e -> {
            if (hasUnsavedChanges) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Nesačuvane izmene");
                confirm.setHeaderText("Imate nesačuvane izmene.");
                confirm.setContentText("Da li ste sigurni da želite da izađete bez čuvanja?");
                Optional<ButtonType> result = confirm.showAndWait();
                if (result.isEmpty() || result.get() != ButtonType.OK) return;
            }
            HbsZaradeApp.navigateTo(new PrimaociListaScreen().getRoot());
        });

        footer.getChildren().addAll(saveBtn, cancelBtn, pppPoBtn, stampaBtn, spacer, closeBtn);
        return footer;
    }

    private void populateFields() {
        loading = true;
        try {
            if (!isNew && primalac != null) {
                idField.setText(primalac.getIdZaposleni() != null ? String.valueOf(primalac.getIdZaposleni()) : "");
                prezimeField.setText(primalac.getPrezime() != null ? primalac.getPrezime() : "");
                imeField.setText(primalac.getIme() != null ? primalac.getIme() : "");
                adresaField.setText(primalac.getAdresaP() != null ? primalac.getAdresaP() : "");
                pbGradField.setText(primalac.getPbGradP() != null ? primalac.getPbGradP() : "");

                String sifraOps = primalac.getOznakaPrebivalista();
                if (sifraOps != null && !sifraOps.isEmpty()) {
                    for (Opstina o : opstine) {
                        if (sifraOps.equals(o.getSifra())) {
                            sifraOpstineCombo.setValue(o);
                            nazivOpstineCombo.setValue(o);
                            break;
                        }
                    }
                }

                mbField.setText(primalac.getMb() != null ? primalac.getMb() : "");
                mobilniField.setText(primalac.getMobilni() != null ? primalac.getMobilni() : "");
                emailField.setText(primalac.getEmail() != null ? primalac.getEmail() : "");

                // Status: 1=Aktivan, 0=Neaktivan
                Integer status = primalac.getStatus();
                if (status != null && status == 0) {
                    statusCombo.setValue("Neaktivan");
                } else {
                    statusCombo.setValue("Aktivan");
                }

                // Procenat
                Integer proc = primalac.getProcenatZaposlenja();
                procenatField.setText(proc != null ? String.valueOf(proc) : "100");

                // Vrsta primaoca
                Integer vpId = primalac.getVrstaPrimaocaId();
                if (vpId != null) {
                    for (VrstaPrimaoca vp : vrstaPrimaocaCombo.getItems()) {
                        if (vpId.equals(vp.getId())) {
                            vrstaPrimaocaCombo.setValue(vp);
                            break;
                        }
                    }
                }
            } else {
                // Novi primalac - defaultne vrednosti
                idField.setText("(automatski)");
                statusCombo.setValue("Aktivan");
                procenatField.setText("100");
            }
        } finally {
            loading = false;
        }
        hasUnsavedChanges = false;
        if (saveBtn != null) saveBtn.setDisable(!isNew);
        if (cancelBtn != null) cancelBtn.setDisable(true);
        setHeaderColor(isNew ? COLOR_NEW : COLOR_VIEW);
    }

    private void saveData() {
        StringBuilder errors = new StringBuilder();

        if (prezimeField.getText().trim().isEmpty()) errors.append("- Prezime je obavezno\n");
        if (imeField.getText().trim().isEmpty()) errors.append("- Ime je obavezno\n");
        String mb = mbField.getText().trim();
        if (mb.isEmpty()) {
            errors.append("- MB/JMBG je obavezan\n");
        } else {
            var result = IdentifikatorValidator.validate(mb);
            if (!result.isValid()) errors.append("- MB/JMBG nije validan: ").append(result.getMessage()).append("\n");
        }

        if (errors.length() > 0) {
            showError("Validacija", "Popunite obavezna polja:\n\n" + errors.toString());
            return;
        }

        primalac.setPrezime(prezimeField.getText().trim());
        primalac.setIme(imeField.getText().trim());
        primalac.setAdresaP(adresaField.getText().trim());
        primalac.setPbGradP(pbGradField.getText().trim());

        Opstina odabranaOpstina = sifraOpstineCombo.getValue();
        primalac.setOznakaPrebivalista(odabranaOpstina != null ? odabranaOpstina.getSifra() : null);

        primalac.setMb(mb);

        // VrstaIdentifikatoraPrimaoca: 1=JMBG (13 cifara), 2=MB (8 cifara)
        if (mb.length() == 13) {
            primalac.setVrstaIdentifikatoraPrimaoca(1);
        } else if (mb.length() == 8) {
            primalac.setVrstaIdentifikatoraPrimaoca(2);
        } else {
            primalac.setVrstaIdentifikatoraPrimaoca(null);
        }

        primalac.setMobilni(mobilniField.getText().trim());
        primalac.setEmail(emailField.getText().trim());

        // Status
        primalac.setStatus("Neaktivan".equals(statusCombo.getValue()) ? 0 : 1);

        // Procenat
        try {
            String procStr = procenatField.getText().trim();
            primalac.setProcenatZaposlenja(procStr.isEmpty() ? 100 : Integer.parseInt(procStr));
        } catch (NumberFormatException e) {
            showError("Validacija", "Procenat zaposlenja mora biti broj!");
            return;
        }

        // Vrsta primaoca
        VrstaPrimaoca odabranaVP = vrstaPrimaocaCombo.getValue();
        primalac.setVrstaPrimaocaId(odabranaVP != null ? odabranaVP.getId() : null);

        try {
            DatabaseConnection dbConn = DatabaseConnection.getInstance();
            Connection conn = dbConn.getMainConnection();
            PrimalacDao dao = new PrimalacDao();

            if (isNew) {
                dao.save(conn, primalac);
                isNew = false;
                idField.setText(primalac.getIdZaposleni() != null ? String.valueOf(primalac.getIdZaposleni()) : "");
            } else {
                dao.update(conn, primalac);
            }
            markSaved();
        } catch (SQLException e) {
            showError("Greška pri čuvanju", e.getMessage());
        }
    }

    /**
     * Proverava nesačuvane izmene i pita korisnika šta želi.
     * @return true ako je OK da se nastavi (nema izmena, ili korisnik izabrao da snimi/odbaci)
     */
    private boolean checkUnsavedBeforeReport() {
        if (!hasUnsavedChanges) return true;
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Nesačuvane izmene");
        alert.setHeaderText("Postoje nesačuvane izmene.");
        alert.setContentText("Da li želite da snimite izmene pre generisanja izveštaja?");
        ButtonType saveBtn2 = new ButtonType("Snimi i nastavi");
        ButtonType discardBtn = new ButtonType("Nastavi bez snimanja");
        ButtonType cancelBtn2 = new ButtonType("Otkaži", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(saveBtn2, discardBtn, cancelBtn2);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isEmpty() || result.get() == cancelBtn2) return false;
        if (result.get() == saveBtn2) {
            saveData();
            if (hasUnsavedChanges) return false; // save failed
        }
        return true;
    }

    private void doPppPo() {
        if (!checkUnsavedBeforeReport()) return;
        if (isNew || primalac.getIdZaposleni() == null) {
            showInfo("PPP-PO", "Primalac mora biti sačuvan pre generisanja PPP-PO XML-a.");
            return;
        }
        try {
            DatabaseConnection dbConn = DatabaseConnection.getInstance();
            if (dbConn == null) {
                showError("Baza podataka nije dostupna.", "");
                return;
            }
            List<Obracun> stavke;
            List<ObracunDefinicija> definicije;
            try (Connection conn = dbConn.getMainConnection()) {
                ObracunDao obracunDao = new ObracunDao();
                stavke = obracunDao.findByLnkZap(conn, primalac.getIdZaposleni());
                ObracunDefinicijaDao defDao = new ObracunDefinicijaDao();
                definicije = defDao.findAll(conn);
            }
            if (stavke.isEmpty()) {
                showInfo("PPP-PO", "Nema obračuna za ovog primaoca.");
                return;
            }
            // Nađi prvu definiciju (najnoviju) za ovog primaoca
            ObracunDefinicija definicija = null;
            if (!stavke.isEmpty() && stavke.get(0).getLnkObracun() != null) {
                int lnkObracun = stavke.get(0).getLnkObracun();
                for (ObracunDefinicija d : definicije) {
                    if (d.getIdObracun() != null && d.getIdObracun() == lnkObracun) {
                        definicija = d;
                        break;
                    }
                }
            }
            if (definicija == null && !definicije.isEmpty()) {
                definicija = definicije.get(0);
            }
            if (definicija == null) {
                definicija = new ObracunDefinicija();
            }

            FileChooser chooser = new FileChooser();
            chooser.setTitle("Snimi PPP-PO XML");
            chooser.setInitialFileName("PPP-PO_" + primalac.getPrezime() + "_" + primalac.getIme() + ".xml");
            chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML fajlovi", "*.xml"));
            File file = chooser.showSaveDialog(HbsZaradeApp.getPrimaryStage());
            if (file == null) return;

            final ObracunDefinicija finalDef = definicija;
            XmlExporter.exportToFile(file, finalDef, stavke, null);
            showInfo("PPP-PO", "XML je uspešno sačuvan:\n" + file.getAbsolutePath());
        } catch (Exception e) {
            showError("Greška pri generisanju PPP-PO", e.getMessage());
        }
    }

    private void doStampa() {
        if (!checkUnsavedBeforeReport()) return;
        if (isNew || primalac.getIdZaposleni() == null) {
            showInfo("Štampa", "Primalac mora biti sačuvan pre štampe.");
            return;
        }
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Snimi PDF kartona primaoca");
        chooser.setInitialFileName("Primalac_" + primalac.getPrezime() + "_" + primalac.getIme() + ".pdf");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF fajlovi", "*.pdf"));
        File file = chooser.showSaveDialog(HbsZaradeApp.getPrimaryStage());
        if (file == null) return;
        try {
            PdfReporter.generatePrimalacKarton(file, primalac);
            showInfo("Štampa", "PDF je uspešno sačuvan:\n" + file.getAbsolutePath());
        } catch (Exception e) {
            showError("Greška pri generisanju PDF-a", e.getMessage());
        }
    }

    private void markDirty() {
        if (loading) return;
        if (!hasUnsavedChanges) {
            hasUnsavedChanges = true;
            setHeaderColor(COLOR_DIRTY);
            if (saveBtn != null) saveBtn.setDisable(false);
            if (cancelBtn != null) cancelBtn.setDisable(false);
        }
    }

    private void markSaved() {
        hasUnsavedChanges = false;
        setHeaderColor(COLOR_SAVED);
        if (saveBtn != null) saveBtn.setDisable(true);
        if (cancelBtn != null) cancelBtn.setDisable(true);
    }

    private void validateMb(String value) {
        if (value == null || value.isEmpty()) {
            mbField.setStyle("-fx-background-color: white; -fx-border-color: #D0D7DE; -fx-border-width: 1; -fx-border-radius: 6; -fx-background-radius: 6;");
            mbValidationLabel.setText("");
            return;
        }
        var result = IdentifikatorValidator.validate(value);
        if (result.isValid()) {
            mbField.setStyle("-fx-background-color: white; -fx-border-color: #1A7F37; -fx-border-width: 1.5; -fx-border-radius: 6; -fx-background-radius: 6;");
            mbValidationLabel.setTextFill(Color.web("#1A7F37"));
            mbValidationLabel.setText("✓ " + result.getMessage());
        } else {
            mbField.setStyle("-fx-background-color: white; -fx-border-color: #CF222E; -fx-border-width: 1.5; -fx-border-radius: 6; -fx-background-radius: 6;");
            mbValidationLabel.setTextFill(Color.web("#CF222E"));
            mbValidationLabel.setText("✗ " + result.getMessage());
        }
    }

    private HBox createFieldRow(String labelText, javafx.scene.Node field) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        Label label = new Label(labelText);
        label.setPrefWidth(130);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 12));
        if (labelText.contains("*")) {
            label.setStyle("-fx-text-fill: #0969DA;");
        } else {
            label.setStyle("-fx-text-fill: #57606A;");
        }
        HBox.setHgrow(field, Priority.ALWAYS);
        row.getChildren().addAll(label, field);
        return row;
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Greška");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /** Skroluje popup listu comboya do selektovane stavke. */
    private static <T> void scrollComboToSelected(ComboBox<T> combo) {
        T sel = combo.getValue();
        if (sel == null) return;
        int idx = combo.getItems().indexOf(sel);
        if (idx < 0) return;
        javafx.stage.Stage mainStage = HbsZaradeApp.getPrimaryStage();
        for (Window w : Window.getWindows()) {
            if (w != mainStage && w.isShowing() && w.getScene() != null) {
                javafx.scene.Node node = w.getScene().getRoot().lookup(".list-view");
                if (node instanceof ListView<?> lv && lv.getItems().size() == combo.getItems().size()) {
                    lv.scrollTo(idx);
                    return;
                }
            }
        }
    }

    public BorderPane getRoot() {
        return root;
    }
}
