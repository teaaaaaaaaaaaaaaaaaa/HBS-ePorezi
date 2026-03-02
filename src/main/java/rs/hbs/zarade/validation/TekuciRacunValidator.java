package rs.hbs.zarade.validation;

/**
 * Validator za tekući račun (TR).
 *
 * Tekući račun u Srbiji ima format: BBB-RRRRRRRRRRRRR-KK
 * - BBB: šifra banke (3 cifre)
 * - RRRRRRRRRRRRR: broj računa (do 13 cifara, vodeće nule opcione)
 * - KK: kontrolni broj (2 cifre, MOD 97)
 *
 * Ova implementacija je bazirana na VBA funkcijama TR_Provera,
 * TR_Formatiraj i KontrolniBroj iz AJ_KontrolniBroj.bas
 */
public final class TekuciRacunValidator {

    private static final int MODEL = 97;
    private static final int MIN_LENGTH = 6;
    private static final int MAX_LENGTH_WITH_DASHES = 20;
    private static final int MAX_LENGTH_WITHOUT_DASHES = 18;

    private TekuciRacunValidator() {
        // Utility class
    }

    /**
     * Proverava osnovnu validnost tekućeg računa.
     * Ne proverava kontrolni broj.
     *
     * @param tr tekući račun za proveru
     * @return true ako je format validan
     */
    public static boolean isFormatValid(String tr) {
        if (tr == null || tr.isEmpty()) {
            return false;
        }

        String trimmed = tr.trim();
        if (trimmed.isEmpty()) {
            return false;
        }

        // Proveri da li ima crtice
        int firstDash = trimmed.indexOf('-');
        int secondDash = trimmed.indexOf('-', firstDash + 1);
        boolean hasDashes = (firstDash > 0 && secondDash > firstDash);

        if (hasDashes) {
            // Format sa crticama: BBB-RRRR...-KK
            if (firstDash != 3) return false;
            int midLength = secondDash - firstDash - 1;
            if (midLength < 1 || midLength > 13) return false;
            int endLength = trimmed.length() - secondDash - 1;
            if (endLength != 2) return false;
            if (trimmed.length() < 8 || trimmed.length() > MAX_LENGTH_WITH_DASHES) return false;

            // Proveri da li su sve cifre (osim crtica)
            String withoutDashes = trimmed.replace("-", "");
            return isNumeric(withoutDashes);
        } else {
            // Format bez crtica: BBBRRR...KK
            if (trimmed.length() < MIN_LENGTH || trimmed.length() > MAX_LENGTH_WITHOUT_DASHES) return false;
            return isNumeric(trimmed);
        }
    }

    /**
     * Proverava validnost tekućeg računa uključujući kontrolni broj.
     *
     * @param tr tekući račun za proveru
     * @return true ako je TR validan
     */
    public static boolean isValid(String tr) {
        return isValid(tr, true);
    }

    /**
     * Proverava validnost tekućeg računa.
     *
     * @param tr tekući račun za proveru
     * @param checkControlNumber da li proveriti kontrolni broj
     * @return true ako je TR validan
     */
    public static boolean isValid(String tr, boolean checkControlNumber) {
        if (!isFormatValid(tr)) {
            return false;
        }

        if (!checkControlNumber) {
            return true;
        }

        // Parsiraj delove
        TekuciRacunParts parts = parse(tr.trim());
        if (parts == null) {
            return false;
        }

        // Izračunaj kontrolni broj
        String baseNumber = parts.getBanka() + parts.getBrojRacunaFull();
        String expectedKb = calculateControlNumber(baseNumber);

        return expectedKb.equals(parts.getKontrolniBroj());
    }

    /**
     * Računa kontrolni broj za tekući račun.
     * Koristi MOD 97 algoritam.
     *
     * @param baseNumber šifra banke + broj računa (bez kontrolnog broja)
     * @return kontrolni broj (2 cifre)
     */
    public static String calculateControlNumber(String baseNumber) {
        if (baseNumber == null || baseNumber.isEmpty() || !isNumeric(baseNumber)) {
            return null;
        }

        // Algoritam iz VBA KontrolniBroj:
        // varInput_Value = CDec(inTekst) * 100
        // varResult = (varInput_Value / inModel) - Fix(varInput_Value / inModel)
        // varResult = Round(varResult, 2)
        // varResult = inModel + 1 - Round(varResult * inModel)

        java.math.BigDecimal value = new java.math.BigDecimal(baseNumber);
        java.math.BigDecimal multiplied = value.multiply(java.math.BigDecimal.valueOf(100));
        java.math.BigDecimal model = java.math.BigDecimal.valueOf(MODEL);

        java.math.BigDecimal divided = multiplied.divide(model, 10, java.math.RoundingMode.DOWN);
        java.math.BigDecimal floor = divided.setScale(0, java.math.RoundingMode.DOWN);
        java.math.BigDecimal fractional = divided.subtract(floor);
        fractional = fractional.setScale(2, java.math.RoundingMode.HALF_UP);

        java.math.BigDecimal result = model.add(java.math.BigDecimal.ONE)
                .subtract(fractional.multiply(model).setScale(0, java.math.RoundingMode.HALF_UP));

        int kb = result.intValue();
        if (kb < 10) {
            return "0" + kb;
        }
        return String.valueOf(kb);
    }

    /**
     * Formatira tekući račun u standardni format sa crticama.
     *
     * @param tr tekući račun
     * @param withLeadingZeros da li dodati vodeće nule u srednji deo
     * @return formatirani tekući račun ili originalni ako format nije validan
     */
    public static String format(String tr, boolean withLeadingZeros) {
        TekuciRacunParts parts = parse(tr);
        if (parts == null) {
            return tr;
        }

        String mid = withLeadingZeros ? parts.getBrojRacunaFull() : parts.getBrojRacunaTrimmed();

        return parts.getBanka() + "-" + mid + "-" + parts.getKontrolniBroj();
    }

    /**
     * Parsira tekući račun na komponente.
     *
     * @param tr tekući račun
     * @return TekuciRacunParts ili null ako format nije validan
     */
    public static TekuciRacunParts parse(String tr) {
        if (!isFormatValid(tr)) {
            return null;
        }

        String trimmed = tr.trim();
        String banka;
        String brojRacuna;
        String kontrolniBroj;

        int firstDash = trimmed.indexOf('-');
        int secondDash = trimmed.indexOf('-', firstDash + 1);
        boolean hasDashes = (firstDash > 0 && secondDash > firstDash);

        if (hasDashes) {
            banka = trimmed.substring(0, 3);
            brojRacuna = trimmed.substring(4, secondDash);
            kontrolniBroj = trimmed.substring(secondDash + 1);
        } else {
            banka = trimmed.substring(0, 3);
            kontrolniBroj = trimmed.substring(trimmed.length() - 2);
            brojRacuna = trimmed.substring(3, trimmed.length() - 2);
        }

        return new TekuciRacunParts(banka, brojRacuna, kontrolniBroj);
    }

    /**
     * Validira tekući račun i vraća rezultat sa opisom greške.
     *
     * @param tr tekući račun za proveru
     * @return ValidationResult sa statusom i porukom
     */
    public static ValidationResult validate(String tr) {
        if (tr == null || tr.isEmpty()) {
            return new ValidationResult(false, "Tekući račun je obavezan");
        }

        if (!isFormatValid(tr)) {
            return new ValidationResult(false, "Neispravan format tekućeg računa");
        }

        if (!isValid(tr)) {
            return new ValidationResult(false, "Tekući račun ima neispravan kontrolni broj");
        }

        return new ValidationResult(true, "Tekući račun je validan");
    }

    private static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Delovi tekućeg računa.
     */
    public static class TekuciRacunParts {
        private final String banka;
        private final String brojRacuna;
        private final String kontrolniBroj;

        public TekuciRacunParts(String banka, String brojRacuna, String kontrolniBroj) {
            this.banka = banka;
            this.brojRacuna = brojRacuna;
            this.kontrolniBroj = kontrolniBroj;
        }

        public String getBanka() { return banka; }
        public String getBrojRacunaTrimmed() { return brojRacuna.replaceFirst("^0+", ""); }
        public String getBrojRacunaFull() {
            StringBuilder sb = new StringBuilder(brojRacuna);
            while (sb.length() < 13) {
                sb.insert(0, '0');
            }
            return sb.toString();
        }
        public String getKontrolniBroj() { return kontrolniBroj; }

        @Override
        public String toString() {
            return banka + "-" + brojRacuna + "-" + kontrolniBroj;
        }
    }
}
