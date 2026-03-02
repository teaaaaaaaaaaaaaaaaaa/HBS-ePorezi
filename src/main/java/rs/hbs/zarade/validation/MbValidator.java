package rs.hbs.zarade.validation;

/**
 * Validator za MB (Matični Broj pravnog lica).
 *
 * Matični broj ima 8 cifara gde je poslednja cifra kontrolna.
 * Koristi se algoritam MOD 11 sa težinskim koeficijentima.
 *
 * Ova implementacija je identična VBA funkciji ProveriMB iz AJ_KontrolniBroj.bas
 */
public final class MbValidator {

    private static final int MODEL = 11;
    private static final int MB_LENGTH = 8;

    private MbValidator() {
        // Utility class
    }

    /**
     * Proverava validnost Matičnog broja.
     *
     * Algoritam iz VBA:
     * - Koristi težinske koeficijente: 7, 6, 5, 4, 3, 2, 7 za prvih 7 cifara
     * - Kontrolna cifra = 11 - (suma mod 11)
     * - Ako je rezultat >= 10, kontrolna cifra je 0
     *
     * @param mb Matični broj za proveru (8 cifara)
     * @return true ako je MB validan, false inače
     */
    public static boolean isValid(String mb) {
        if (mb == null || mb.isEmpty()) {
            return false;
        }

        String trimmed = mb.trim();

        // Mora imati tačno 8 karaktera
        if (trimmed.length() != MB_LENGTH) {
            return false;
        }

        // Mora biti numerički
        if (!isNumeric(trimmed)) {
            return false;
        }

        // Algoritam iz VBA ProveriMB:
        // mMidMod = 7
        // For i = mMidMod To 1 Step -1
        //     a = a + 1
        //     tmpC1 = CInt(Mid(nData, a, 1))
        //     mnozilac = i + 1
        //     If mnozilac > 7 Then mnozilac = 2
        //     If mnozilac < 2 Then mnozilac = 7
        //     mSum = mSum + (mnozilac * tmpC1)
        // Next i
        // varResult = 11 - (mSum mod 11)
        // If varResult >= 10 Then varResult = 0

        int mMidMod = 7;
        long mSum = 0;
        int a = 0;

        for (int i = mMidMod; i >= 1; i--) {
            a++;
            int tmpC1 = Character.getNumericValue(trimmed.charAt(a - 1));

            int mnozilac = i + 1;
            if (mnozilac > 7) mnozilac = 2;
            if (mnozilac < 2) mnozilac = 7;

            mSum += mnozilac * tmpC1;
        }

        int varResult = MODEL - (int) (mSum % MODEL);

        // Ako je rezultat >= 10, kontrolna cifra je 0
        if (varResult < 1 || varResult > 9) {
            varResult = 0;
        }

        int kontrolnaCifra = Character.getNumericValue(trimmed.charAt(7));

        return varResult == kontrolnaCifra;
    }

    /**
     * Validira Matični broj i vraća rezultat sa opisom greške.
     *
     * @param mb Matični broj za proveru
     * @return ValidationResult sa statusom i porukom
     */
    public static ValidationResult validate(String mb) {
        if (mb == null || mb.isEmpty()) {
            return new ValidationResult(false, "Matični broj je obavezan");
        }

        String trimmed = mb.trim();

        if (trimmed.length() != MB_LENGTH) {
            return new ValidationResult(false, "Matični broj mora imati tačno 8 cifara");
        }

        if (!isNumeric(trimmed)) {
            return new ValidationResult(false, "Matični broj sme sadržati samo cifre");
        }

        if (!isValid(mb)) {
            return new ValidationResult(false, "Matični broj ima neispravan kontrolni broj");
        }

        return new ValidationResult(true, "Matični broj je validan");
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
