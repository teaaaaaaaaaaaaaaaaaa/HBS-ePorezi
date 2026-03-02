package rs.hbs.zarade.ui;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.util.Duration;
import rs.hbs.zarade.HbsZaradeApp;
import rs.hbs.zarade.db.DatabaseConnection;
import rs.hbs.zarade.db.LogDao;

/**
 * Collapsible sidebar panel za navigaciju.
 * Expanded: 230px, Collapsed: 56px
 */
public class SidebarPanel {

    private static final double EXPANDED_WIDTH  = 230;
    private static final double COLLAPSED_WIDTH = 56;

    // Paleta boja — usklađena sa dizajn sistemom
    private static final String BG_MAIN     = "#2E3F5C";  // srednje tamno plavo
    private static final String BG_HEADER   = "#1E2D42";  // tamno plavo (ne crno)
    private static final String BG_HOVER    = "#3A4F6B";  // hover
    private static final String BG_ACTIVE   = "#455C7A";  // active
    private static final String ACCENT      = "#4493F8";  // brighter blue — usklađen sa #0969DA
    private static final String TEXT_MAIN   = "#E6EDF3";  // skoro bela
    private static final String TEXT_MUTED  = "#9BAFC6";  // svetlo plavo-siva
    private static final String TEXT_DANGER = "#FF7B72";  // svetlo crvena za odjavu

    private VBox root;
    private boolean collapsed = false;
    private Button toggleBtn;

    private Label appTitle;
    private VBox navItems;
    private VBox userInfo;
    private Button exitBtn;

    private record NavItem(String icon, String label, Runnable action) {}

    public SidebarPanel() {
        createUI();
    }

    private void createUI() {
        root = new VBox(0);
        root.setPrefWidth(EXPANDED_WIDTH);
        root.setMinWidth(EXPANDED_WIDTH);
        root.setMaxWidth(EXPANDED_WIDTH);
        root.setStyle("-fx-background-color: " + BG_MAIN + ";");

        // ── Header zona (logo + toggle) ──────────────────────────────────
        HBox headerRow = new HBox();
        headerRow.setAlignment(Pos.CENTER_LEFT);
        headerRow.setPadding(new Insets(14, 10, 14, 18));
        headerRow.setStyle("-fx-background-color: " + BG_HEADER + ";");
        headerRow.setSpacing(10);

        // Accent dot / logo mark
        Rectangle dot = new Rectangle(8, 8);
        dot.setFill(Color.web(ACCENT));
        dot.setArcWidth(4);
        dot.setArcHeight(4);

        appTitle = new Label("ePorezi");
        appTitle.setTextFill(Color.web(TEXT_MAIN));
        appTitle.setFont(Font.font("Segoe UI", FontWeight.BOLD, 15));
        HBox.setHgrow(appTitle, Priority.ALWAYS);

        toggleBtn = new Button("‹");
        toggleBtn.setStyle(
            "-fx-background-color: " + BG_HOVER + ";" +
            "-fx-text-fill: " + TEXT_MUTED + ";" +
            "-fx-font-size: 16px; -fx-font-weight: bold;" +
            "-fx-cursor: hand; -fx-padding: 2 8;" +
            "-fx-background-radius: 6;");
        toggleBtn.setOnMouseEntered(e -> toggleBtn.setStyle(
            "-fx-background-color: " + BG_ACTIVE + ";" +
            "-fx-text-fill: " + TEXT_MAIN + ";" +
            "-fx-font-size: 16px; -fx-font-weight: bold;" +
            "-fx-cursor: hand; -fx-padding: 2 8;" +
            "-fx-background-radius: 6;"));
        toggleBtn.setOnMouseExited(e -> toggleBtn.setStyle(
            "-fx-background-color: " + BG_HOVER + ";" +
            "-fx-text-fill: " + TEXT_MUTED + ";" +
            "-fx-font-size: 16px; -fx-font-weight: bold;" +
            "-fx-cursor: hand; -fx-padding: 2 8;" +
            "-fx-background-radius: 6;"));
        toggleBtn.setOnAction(e -> toggleCollapse());

        headerRow.getChildren().addAll(dot, appTitle, toggleBtn);

        // ── Nav sekcija ────────────────────────────────────────────────
        Label navLabel = new Label();  // prazan — samo čuva spacing
        navLabel.setPadding(new Insets(16, 0, 6, 18));

        navItems = new VBox(2);
        navItems.setPadding(new Insets(0, 8, 10, 8));
        VBox.setVgrow(navItems, Priority.ALWAYS);

        int userLevel = HbsZaradeApp.getUserLevel();
        NavItem[] items = {
            new NavItem("📊", "Obračuni",         () -> AppLayout.setContent(new ObracuniScreen().getRoot())),
            new NavItem("👥", "Primaoci prihoda",  () -> AppLayout.setContent(new PrimaociListaScreen().getRoot())),
            new NavItem("🏢", "Isplatilac",        () -> AppLayout.setContent(new IsplatiocScreen().getRoot())),
            new NavItem("⚙",  "Podešavanja",       () -> AppLayout.setContent(new PodesavanjaScreen().getRoot())),
        };

        for (NavItem item : items) {
            // Nivo < 8 (npr. nivo 4): vidi samo Primaoci prihoda
            if (userLevel < 8 && !item.label().equals("Primaoci prihoda")) continue;
            navItems.getChildren().add(createNavButton(item));
        }

        // ── Spacer ────────────────────────────────────────────────────
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);

        // ── Tanki separator ────────────────────────────────────────────
        Separator sep = new Separator();
        sep.setStyle("-fx-background-color: " + BG_HOVER + "; -fx-padding: 0;");
        sep.setPadding(new Insets(0, 8, 0, 8));

        // ── User info ─────────────────────────────────────────────────
        userInfo = createUserInfo();

        // ── Exit dugme ───────────────────────────────────────────────
        exitBtn = new Button("🚪  Odjava");
        exitBtn.setMaxWidth(Double.MAX_VALUE);
        exitBtn.setAlignment(Pos.CENTER_LEFT);
        exitBtn.setPadding(new Insets(11, 15, 11, 18));
        exitBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + TEXT_DANGER + ";" +
            "-fx-font-size: 18px; -fx-cursor: hand;" +
            "-fx-background-radius: 0;");
        exitBtn.setOnMouseEntered(e -> exitBtn.setStyle(
            "-fx-background-color: #6B1A1A;" +
            "-fx-text-fill: #fca5a5;" +
            "-fx-font-size: 18px; -fx-cursor: hand;" +
            "-fx-background-radius: 0;"));
        exitBtn.setOnMouseExited(e -> {
            // Primeni ispravan stil zavisno od stanja collapsed
            if (collapsed) {
                exitBtn.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-text-fill: " + TEXT_DANGER + ";" +
                    "-fx-font-size: 22px; -fx-cursor: hand;" +
                    "-fx-background-radius: 0;");
            } else {
                exitBtn.setStyle(
                    "-fx-background-color: transparent;" +
                    "-fx-text-fill: " + TEXT_DANGER + ";" +
                    "-fx-font-size: 18px; -fx-cursor: hand;" +
                    "-fx-background-radius: 0;");
            }
        });
        exitBtn.setOnAction(e -> {
            // Log odjavu
            try {
                DatabaseConnection dbConn = DatabaseConnection.getInstance();
                if (dbConn != null) {
                    try (var conn = dbConn.getMainConnection()) {
                        LogDao.log(conn, null, HbsZaradeApp.getCurrentUser(),
                                "Sidebar", "LOGIN", 0, "QUIT",
                                "Odjava korisnika: " + HbsZaradeApp.getCurrentUser());
                    }
                }
            } catch (Exception ex) {
                System.err.println("Log odjave nije uspeo: " + ex.getMessage());
            }
            AppLayout.reset();
            HbsZaradeApp.showLoginScreen();
        });

        root.getChildren().addAll(headerRow, navLabel, navItems, spacer, sep, userInfo, exitBtn);
    }

    private Button createNavButton(NavItem item) {
        Button btn = new Button(item.icon() + "  " + item.label());
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setAlignment(Pos.CENTER_LEFT);
        btn.setPadding(new Insets(11, 14, 11, 14));
        btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + TEXT_MUTED + ";" +
            "-fx-font-size: 18px; -fx-cursor: hand;" +
            "-fx-background-radius: 8;");
        btn.setOnMouseEntered(e -> btn.setStyle(
            "-fx-background-color: " + BG_HOVER + ";" +
            "-fx-text-fill: " + TEXT_MAIN + ";" +
            "-fx-font-size: 18px; -fx-cursor: hand;" +
            "-fx-background-radius: 8;"));
        btn.setOnMouseExited(e -> btn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + TEXT_MUTED + ";" +
            "-fx-font-size: 18px; -fx-cursor: hand;" +
            "-fx-background-radius: 8;"));
        btn.setOnAction(e -> item.action().run());

        btn.getProperties().put("icon", item.icon());
        btn.getProperties().put("label", item.label());

        return btn;
    }

    private VBox createUserInfo() {
        VBox userBox = new VBox(3);
        userBox.setPadding(new Insets(10, 15, 10, 18));
        userBox.setStyle("-fx-background-color: " + BG_HEADER + ";");

        String username = HbsZaradeApp.getCurrentUser();
        int level = HbsZaradeApp.getUserLevel();

        Label userLabel = new Label("👤 " + (username != null ? username : "Korisnik"));
        userLabel.setTextFill(Color.web(TEXT_MAIN));
        userLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        Label levelLabel = new Label("Nivo pristupa: " + level);
        levelLabel.setTextFill(Color.web(TEXT_MUTED));
        levelLabel.setFont(Font.font("Segoe UI", 12));

        userBox.getChildren().addAll(userLabel, levelLabel);
        return userBox;
    }

    private void toggleCollapse() {
        collapsed = !collapsed;
        double targetWidth = collapsed ? COLLAPSED_WIDTH : EXPANDED_WIDTH;
        toggleBtn.setText(collapsed ? "›" : "‹");

        Timeline timeline = new Timeline(
            new KeyFrame(Duration.millis(200),
                new KeyValue(root.prefWidthProperty(), targetWidth),
                new KeyValue(root.minWidthProperty(), targetWidth),
                new KeyValue(root.maxWidthProperty(), targetWidth)
            )
        );

        if (collapsed) {
            timeline.setOnFinished(e -> updateNavButtonsCollapsed());
        } else {
            updateNavButtonsExpanded();
        }
        timeline.play();
    }

    private void updateNavButtonsCollapsed() {
        for (var node : navItems.getChildren()) {
            if (node instanceof Button btn) {
                String icon = (String) btn.getProperties().get("icon");
                if (icon != null) {
                    btn.setText(icon);
                    btn.setAlignment(Pos.CENTER);
                    btn.setPadding(new Insets(11, 4, 11, 4));
                    // Ikonica mora stati u 56px — koristimo 22px emoji font
                    btn.setStyle(
                        "-fx-background-color: transparent;" +
                        "-fx-text-fill: " + TEXT_MUTED + ";" +
                        "-fx-font-size: 22px; -fx-cursor: hand;" +
                        "-fx-background-radius: 8;");
                }
            }
        }
        appTitle.setVisible(false);
        appTitle.setManaged(false);
        for (var child : userInfo.getChildren()) {
            child.setVisible(false);
            child.setManaged(false);
        }
        exitBtn.setText("🚪");
        exitBtn.setAlignment(Pos.CENTER);
        exitBtn.setPadding(new Insets(11, 4, 11, 4));
        exitBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + TEXT_DANGER + ";" +
            "-fx-font-size: 22px; -fx-cursor: hand;" +
            "-fx-background-radius: 0;");
    }

    private void updateNavButtonsExpanded() {
        for (var node : navItems.getChildren()) {
            if (node instanceof Button btn) {
                String icon = (String) btn.getProperties().get("icon");
                String label = (String) btn.getProperties().get("label");
                if (icon != null && label != null) {
                    btn.setText(icon + "  " + label);
                    btn.setAlignment(Pos.CENTER_LEFT);
                    btn.setPadding(new Insets(11, 14, 11, 14));
                    btn.setStyle(
                        "-fx-background-color: transparent;" +
                        "-fx-text-fill: " + TEXT_MUTED + ";" +
                        "-fx-font-size: 18px; -fx-cursor: hand;" +
                        "-fx-background-radius: 8;");
                }
            }
        }
        appTitle.setVisible(true);
        appTitle.setManaged(true);
        for (var child : userInfo.getChildren()) {
            child.setVisible(true);
            child.setManaged(true);
        }
        exitBtn.setText("🚪  Odjava");
        exitBtn.setAlignment(Pos.CENTER_LEFT);
        exitBtn.setPadding(new Insets(11, 15, 11, 18));
        exitBtn.setStyle(
            "-fx-background-color: transparent;" +
            "-fx-text-fill: " + TEXT_DANGER + ";" +
            "-fx-font-size: 18px; -fx-cursor: hand;" +
            "-fx-background-radius: 0;");
    }

    public VBox getRoot() {
        return root;
    }
}
