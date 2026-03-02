package rs.hbs.zarade.validation;

/**
 * Kombinovani validator za identifikatore fizičkih i pravnih lica.
 *
 * Ova klasa služi kao fasada za JmbgValidator i MbValidator,
 * omogućavajući automatsku detekciju tipa identifikatora.
 *
 * Identična VBA funkciji ProveriJMBG_MB iz AJ_KontrolniBroj.bas
 */
public final class IdentifikatorValidator {

    private IdentifikatorValidator() {
        // Utility class
    }

    /**
     * Tip identifikatora.
     */
    public enum TipIdentifikatora {
        JMBG("JMBG", "Jedinstveni matični broj građana", 13),
        MB("MB", "Matični broj pravnog lica", 8),
        NEPOZNAT("?", "Nepoznat tip", 0);

        private final String sifra;
        private final String opis;
        private final int duzina;

        TipIdentifikatora(String sifra, String opis, int duzina) {
            this.sifra = sifra;
            this.opis = opis;
            this.duzina = duzina;
        }

        public String getSifra() { return sifra; }
        public String getOpis() { return opis; }
        public int getDuzina() { return duzina; }
    }

    /**
     * Proverava validnost identifikatora (JMBG ili MB).
     * Automatski detektuje tip na osnovu dužine.
     *
     * Identična VBA funkciji ProveriJMBG_MB:
     * - 8 cifara = Matični broj
     * - 13 cifara = JMBG
     *
     * @param identifikator JMBG ili MB za proveru
     * @return true ako je identifikator validan
     */
    public static boolean isValid(String identifikator) {
        if (identifikator == null || identifikator.isEmpty()) {
            return false;
        }

        String trimmed = identifikator.trim();

        if (trimmed.length() == 8) {
            return MbValidator.isValid(trimmed);
        } else if (trimmed.length() == 13) {
            return JmbgValidator.isValid(trimmed);
        }

        return false;
    }

    /**
     * Detektuje tip identifikatora na osnovu dužine.
     *
     * @param identifikator JMBG ili MB
     * @return TipIdentifikatora
     */
    public static TipIdentifikatora detektujTip(String identifikator) {
        if (identifikator == null || identifikator.isEmpty()) {
            return TipIdentifikatora.NEPOZNAT;
        }

        String trimmed = identifikator.trim();

        if (trimmed.length() == 8) {
            return TipIdentifikatora.MB;
        } else if (trimmed.length() == 13) {
            return TipIdentifikatora.JMBG;
        }

        return TipIdentifikatora.NEPOZNAT;
    }

    /**
     * Validira identifikator i vraća rezultat sa opisom greške.
     *
     * @param identifikator JMBG ili MB za proveru
     * @return ValidationResult sa statusom i porukom
     */
    public static ValidationResult validate(String identifikator) {
        if (identifikator == null || identifikator.isEmpty()) {
            return new ValidationResult(false, "Identifikator je obavezan");
        }

        String trimmed = identifikator.trim();
        TipIdentifikatora tip = detektujTip(trimmed);

        switch (tip) {
            case MB:
                return MbValidator.validate(trimmed);
            case JMBG:
                return JmbgValidator.validate(trimmed);
            default:
                return new ValidationResult(false,
                        "Nepoznat tip identifikatora. Očekivano 8 cifara (MB) ili 13 cifara (JMBG)");
        }
    }

    /**
     * Validira identifikator određenog tipa.
     *
     * @param identifikator vrednost za proveru
     * @param ocekivaniTip očekivani tip identifikatora
     * @return ValidationResult sa statusom i porukom
     */
    public static ValidationResult validate(String identifikator, TipIdentifikatora ocekivaniTip) {
        if (identifikator == null || identifikator.isEmpty()) {
            return new ValidationResult(false, ocekivaniTip.getOpis() + " je obavezan");
        }

        String trimmed = identifikator.trim();
        TipIdentifikatora detektovaniTip = detektujTip(trimmed);

        if (ocekivaniTip != TipIdentifikatora.NEPOZNAT && detektovaniTip != ocekivaniTip) {
            return new ValidationResult(false,
                    String.format("Očekivan %s (%d cifara), a unet je %s",
                            ocekivaniTip.getOpis(), ocekivaniTip.getDuzina(), detektovaniTip.getOpis()));
        }

        return validate(trimmed);
    }
}
