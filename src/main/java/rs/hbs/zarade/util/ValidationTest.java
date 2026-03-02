package rs.hbs.zarade.util;

import rs.hbs.zarade.validation.*;

/**
 * Test klasa za proveru implementacije validatora.
 *
 * Pokreni sa: ./gradlew run --args="test-validation"
 */
public class ValidationTest {

    public static void main(String[] args) {
        System.out.println("=== ePorezi - Test Validatora ===\n");

        testJmbgValidator();
        testPibValidator();
        testMbValidator();
        testIdentifikatorValidator();
        testTekuciRacunValidator();

        System.out.println("\n=== Svi testovi završeni ===");
    }

    private static void testJmbgValidator() {
        System.out.println("--- JMBG Validator ---");

        // Poznati validni JMBG primeri za testiranje
        // NAPOMENA: Ovo su izmišljeni JMBG-ovi za test, ne stvarni podaci

        // Test dužine
        assertFalse(JmbgValidator.isValid(null), "null JMBG");
        assertFalse(JmbgValidator.isValid(""), "prazan JMBG");
        assertFalse(JmbgValidator.isValid("12345678901"), "11 cifara");
        assertFalse(JmbgValidator.isValid("12345678901234"), "14 cifara");

        // Test ne-numeričkih karaktera
        assertFalse(JmbgValidator.isValid("123456789012A"), "sadrži slovo");

        // ValidationResult test
        ValidationResult result = JmbgValidator.validate("");
        assertFalse(result.isValid(), "prazan JMBG ValidationResult");
        System.out.println("  Poruka za prazan: " + result.getMessage());

        result = JmbgValidator.validate("12345678901");
        assertFalse(result.isValid(), "pogrešna dužina ValidationResult");
        System.out.println("  Poruka za pogrešnu dužinu: " + result.getMessage());

        System.out.println("  JMBG testovi prošli!\n");
    }

    private static void testPibValidator() {
        System.out.println("--- PIB Validator ---");

        // Test dužine
        assertFalse(PibValidator.isValid(null), "null PIB");
        assertFalse(PibValidator.isValid(""), "prazan PIB");
        assertFalse(PibValidator.isValid("12345678"), "8 cifara");
        assertFalse(PibValidator.isValid("1234567890"), "10 cifara");

        // Test ne-numeričkih karaktera
        assertFalse(PibValidator.isValid("12345678A"), "sadrži slovo");

        // Poznati validni PIB (iz VBA test koda)
        // VBA test koristi: "102486895" - proverimo
        // I "69435151530" ali to ima 11 cifara, nije PIB

        // ValidationResult test
        ValidationResult result = PibValidator.validate("");
        assertFalse(result.isValid(), "prazan PIB ValidationResult");
        System.out.println("  Poruka za prazan: " + result.getMessage());

        result = PibValidator.validate("12345678");
        assertFalse(result.isValid(), "pogrešna dužina ValidationResult");
        System.out.println("  Poruka za pogrešnu dužinu: " + result.getMessage());

        System.out.println("  PIB testovi prošli!\n");
    }

    private static void testMbValidator() {
        System.out.println("--- MB Validator ---");

        // Test dužine
        assertFalse(MbValidator.isValid(null), "null MB");
        assertFalse(MbValidator.isValid(""), "prazan MB");
        assertFalse(MbValidator.isValid("1234567"), "7 cifara");
        assertFalse(MbValidator.isValid("123456789"), "9 cifara");

        // Test ne-numeričkih karaktera
        assertFalse(MbValidator.isValid("1234567A"), "sadrži slovo");

        // ValidationResult test
        ValidationResult result = MbValidator.validate("");
        assertFalse(result.isValid(), "prazan MB ValidationResult");
        System.out.println("  Poruka za prazan: " + result.getMessage());

        result = MbValidator.validate("1234567");
        assertFalse(result.isValid(), "pogrešna dužina ValidationResult");
        System.out.println("  Poruka za pogrešnu dužinu: " + result.getMessage());

        System.out.println("  MB testovi prošli!\n");
    }

    private static void testIdentifikatorValidator() {
        System.out.println("--- Identifikator Validator ---");

        // Test detekcije tipa
        assertEquals(IdentifikatorValidator.TipIdentifikatora.MB,
                IdentifikatorValidator.detektujTip("12345678"), "8 cifara = MB");
        assertEquals(IdentifikatorValidator.TipIdentifikatora.JMBG,
                IdentifikatorValidator.detektujTip("1234567890123"), "13 cifara = JMBG");
        assertEquals(IdentifikatorValidator.TipIdentifikatora.NEPOZNAT,
                IdentifikatorValidator.detektujTip("12345"), "5 cifara = NEPOZNAT");

        // Test kombinovane validacije
        assertFalse(IdentifikatorValidator.isValid(null), "null identifikator");
        assertFalse(IdentifikatorValidator.isValid(""), "prazan identifikator");
        assertFalse(IdentifikatorValidator.isValid("12345"), "pogrešna dužina");

        System.out.println("  Identifikator testovi prošli!\n");
    }

    private static void testTekuciRacunValidator() {
        System.out.println("--- Tekući Račun Validator ---");

        // Test formata
        assertFalse(TekuciRacunValidator.isFormatValid(null), "null TR");
        assertFalse(TekuciRacunValidator.isFormatValid(""), "prazan TR");
        assertFalse(TekuciRacunValidator.isFormatValid("12345"), "prekratak TR");

        // Test formata sa crticama
        assertTrue(TekuciRacunValidator.isFormatValid("265-1-16"), "minimalni TR sa crticama");
        assertTrue(TekuciRacunValidator.isFormatValid("265-1100310000496-16"), "puni TR sa crticama");

        // Test formata bez crtica
        assertTrue(TekuciRacunValidator.isFormatValid("265116"), "minimalni TR bez crtica");

        // Test parsiranja
        TekuciRacunValidator.TekuciRacunParts parts =
                TekuciRacunValidator.parse("265-1100310000496-16");
        if (parts != null) {
            assertEquals("265", parts.getBanka(), "banka");
            assertEquals("16", parts.getKontrolniBroj(), "kontrolni broj");
            System.out.println("  Parsirani TR: " + parts);
        }

        // Test formatiranja
        String formatted = TekuciRacunValidator.format("265110031000049616", true);
        System.out.println("  Formatirani TR: " + formatted);

        System.out.println("  Tekući račun testovi prošli!\n");
    }

    // Pomoćne metode za testiranje

    private static void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("GREŠKA: Očekivano TRUE za: " + message);
        }
        System.out.println("  ✓ " + message);
    }

    private static void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError("GREŠKA: Očekivano FALSE za: " + message);
        }
        System.out.println("  ✓ " + message + " -> false");
    }

    private static void assertEquals(Object expected, Object actual, String message) {
        if (expected == null && actual == null) {
            System.out.println("  ✓ " + message);
            return;
        }
        if (expected == null || !expected.equals(actual)) {
            throw new AssertionError("GREŠKA: " + message +
                    " - očekivano: " + expected + ", dobijeno: " + actual);
        }
        System.out.println("  ✓ " + message + " = " + actual);
    }
}
