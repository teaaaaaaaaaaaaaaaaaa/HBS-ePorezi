package rs.hbs.zarade.validation;

/**
 * Rezultat validacije sa statusom i porukom.
 *
 * Koristi se za vraćanje detaljnih informacija o validaciji,
 * uključujući opis greške kada validacija nije uspela.
 */
public class ValidationResult {

    private final boolean valid;
    private final String message;

    /**
     * Kreira novi ValidationResult.
     *
     * @param valid da li je validacija uspela
     * @param message poruka (opis greške ili potvrda uspeha)
     */
    public ValidationResult(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    /**
     * @return true ako je validacija uspela
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * @return poruka validacije
     */
    public String getMessage() {
        return message;
    }

    /**
     * Kreira uspešan rezultat validacije.
     *
     * @param message poruka potvrde
     * @return ValidationResult sa valid=true
     */
    public static ValidationResult success(String message) {
        return new ValidationResult(true, message);
    }

    /**
     * Kreira neuspešan rezultat validacije.
     *
     * @param message opis greške
     * @return ValidationResult sa valid=false
     */
    public static ValidationResult failure(String message) {
        return new ValidationResult(false, message);
    }

    @Override
    public String toString() {
        return (valid ? "VALID" : "INVALID") + ": " + message;
    }
}
