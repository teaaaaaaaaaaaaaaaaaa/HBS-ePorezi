package rs.hbs.zarade;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import rs.hbs.zarade.ui.AppLayout;
import rs.hbs.zarade.ui.LoginScreen;

/**
 * Glavna JavaFX aplikacija ePorezi.
 *
 * Ovo je ulazna tačka za GUI aplikaciju.
 */
public class HbsZaradeApp extends Application {

    private static Stage primaryStage;
    private static String currentUser = null;
    private static int userLevel = 0;

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        primaryStage.setTitle("ePorezi");
        primaryStage.setMinWidth(1024);
        primaryStage.setMinHeight(768);

        // Pokaži login ekran
        showLoginScreen();

        primaryStage.show();
    }

    public static void showLoginScreen() {
        LoginScreen loginScreen = new LoginScreen();
        boolean wasMaximized = primaryStage.isMaximized();
        Scene scene = new Scene(loginScreen.getRoot(), 1200, 800);
        scene.getStylesheets().add(HbsZaradeApp.class.getResource("/styles/main.css").toExternalForm());
        primaryStage.setScene(scene);
        if (wasMaximized) Platform.runLater(() -> primaryStage.setMaximized(true));
    }

    /** Otvara glavni layout sa sidebarom (koristi se umjesto showMainMenu). */
    public static void showAppLayout() {
        AppLayout.reset(); // svježa instanca
        AppLayout layout = AppLayout.getInstance();
        boolean wasMaximized = primaryStage.isMaximized();
        Scene scene = new Scene(layout.getRoot(), 1200, 800);
        scene.getStylesheets().add(HbsZaradeApp.class.getResource("/styles/main.css").toExternalForm());
        primaryStage.setScene(scene);
        if (wasMaximized) Platform.runLater(() -> primaryStage.setMaximized(true));
    }

    /** Zamjenjuje content u centru AppLayout-a. */
    public static void navigateTo(Node content) {
        AppLayout.setContent(content);
    }

    /** @deprecated Koristiti showAppLayout() */
    @Deprecated
    public static void showMainMenu() {
        showAppLayout();
    }

    public static void setCurrentUser(String username, int level) {
        currentUser = username;
        userLevel = level;
    }

    public static String getCurrentUser() {
        return currentUser;
    }

    public static int getUserLevel() {
        return userLevel;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
