package rs.hbs.zarade;

/**
 * Launcher klasa za pokretanje JavaFX aplikacije.
 *
 * Ova klasa NE extenduje Application, što omogućava pokretanje
 * bez eksplicitnog module-path podešavanja za JavaFX.
 *
 * Ovo je standardan workaround za distribuciju JavaFX aplikacija
 * kao standalone JAR sa svim zavisnostima.
 */
public class Launcher {

    public static void main(String[] args) {
        HbsZaradeApp.main(args);
    }
}
