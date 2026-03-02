package rs.hbs.zarade.validation;

/**
 * Validator za PIB (Poreski Identifikacioni Broj).
 *
 * PIB ima 9 cifara gde je poslednja cifra kontrolna.
 * Koristi se algoritam MOD 11/10 za računanje kontrolnog broja.
 *
 * Ova implementacija je identična VBA funkciji ProveriPIB / KontrolaModul11_PIB
 * iz AJ_KontrolniBroj.bas
 */
public final class PibValidator {

    private static final int MODEL = 11;
    private static final int PIB_LENGTH = 9;

    private PibValidator() {
        // Utility class
    }

    /**
     * Proverava validnost PIB-a.
     * Koristi algoritam identičan VBA funkciji KontrolaModul11_PIB.
     *
     * @param pib PIB za proveru (9 cifara)
     * @return true ako je PIB validan, false inače
     */
    public static boolean isValid(String pib) {
        if (pib == null || pib.isEmpty()) {
            return false;
        }

        String trimmed = pib.trim();

        // Mora imati tačno 9 karaktera
        if (trimmed.length() != PIB_LENGTH) {
            return false;
        }

        // Mora biti numerički
        if (!isNumeric(trimmed)) {
            return false;
        }

        // Algoritam iz VBA (KontrolaModul11_PIB):
        // tmpBR = 10
        // For i = 1 To Len(nData) - 1
        //     tmpBR = (CInt(Mid(nData, i, 1)) + tmpBR) Mod 10
        //     If tmpBR = 0 Then tmpBR = 10
        //     tmpBR = (tmpBR * 2) Mod 11
        // Next i
        // tmpBR = (11 - tmpBR) Mod 10
        // Kontrolna cifra = tmpBR

        int tmpBR = 10;

        for (int i = 0; i < trimmed.length() - 1; i++) {
            int cifra = Character.getNumericValue(trimmed.charAt(i));
            tmpBR = (cifra + tmpBR) % 10;
            if (tmpBR == 0) {
                tmpBR = 10;
            }
            tmpBR = (tmpBR * 2) % MODEL;
        }

        tmpBR = (MODEL - tmpBR) % 10;

        int kontrolnaCifra = Character.getNumericValue(trimmed.charAt(trimmed.length() - 1));

        return tmpBR == kontrolnaCifra;
    }

    /**
     * Alternativna provera PIB-a korišćenjem razloženijeg algoritma.
     * Identična VBA funkciji ProveriPIB.
     *
     * @param pib PIB za proveru
     * @return true ako je PIB validan
     */
    public static boolean isValidAlternative(String pib) {
        if (pib == null || pib.isEmpty()) {
            return false;
        }

        String trimmed = pib.trim();

        if (trimmed.length() != PIB_LENGTH) {
            return false;
        }

        if (!isNumeric(trimmed)) {
            return false;
        }

        int zadnji = Character.getNumericValue(trimmed.charAt(8));

        // Algoritam iz VBA ProveriPIB - razložen po ciframa
        int c8 = (Character.getNumericValue(trimmed.charAt(0)) + 10) % 10;
        if (c8 == 0) c8 = 10;
        c8 = (c8 * 2) % 11;

        int c7 = (Character.getNumericValue(trimmed.charAt(1)) + c8) % 10;
        if (c7 == 0) c7 = 10;
        c7 = (c7 * 2) % 11;

        int c6 = (Character.getNumericValue(trimmed.charAt(2)) + c7) % 10;
        if (c6 == 0) c6 = 10;
        c6 = (c6 * 2) % 11;

        int c5 = (Character.getNumericValue(trimmed.charAt(3)) + c6) % 10;
        if (c5 == 0) c5 = 10;
        c5 = (c5 * 2) % 11;

        int c4 = (Character.getNumericValue(trimmed.charAt(4)) + c5) % 10;
        if (c4 == 0) c4 = 10;
        c4 = (c4 * 2) % 11;

        int c3 = (Character.getNumericValue(trimmed.charAt(5)) + c4) % 10;
        if (c3 == 0) c3 = 10;
        c3 = (c3 * 2) % 11;

        int c2 = (Character.getNumericValue(trimmed.charAt(6)) + c3) % 10;
        if (c2 == 0) c2 = 10;
        c2 = (c2 * 2) % 11;

        int c1 = (Character.getNumericValue(trimmed.charAt(7)) + c2) % 10;
        if (c1 == 0) c1 = 10;
        c1 = (c1 * 2) % 11;

        int c0 = (11 - c1) % 10;

        return c0 == zadnji;
    }

    /**
     * Validira PIB i vraća rezultat sa opisom greške.
     *
     * @param pib PIB za proveru
     * @return ValidationResult sa statusom i porukom
     */
    public static ValidationResult validate(String pib) {
        if (pib == null || pib.isEmpty()) {
            return new ValidationResult(false, "PIB je obavezan");
        }

        String trimmed = pib.trim();

        if (trimmed.length() != PIB_LENGTH) {
            return new ValidationResult(false, "PIB mora imati tačno 9 cifara");
        }

        if (!isNumeric(trimmed)) {
            return new ValidationResult(false, "PIB sme sadržati samo cifre");
        }

        if (!isValid(pib)) {
            return new ValidationResult(false, "PIB ima neispravan kontrolni broj");
        }

        return new ValidationResult(true, "PIB je validan");
    }

    private static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }
}
