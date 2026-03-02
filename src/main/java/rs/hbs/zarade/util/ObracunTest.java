package rs.hbs.zarade.util;

import rs.hbs.zarade.domain.Obracun;
import rs.hbs.zarade.domain.ObracunStope;
import rs.hbs.zarade.service.ObracunService;

import java.math.BigDecimal;

/**
 * Test klasa za proveru implementacije obračuna.
 *
 * Pokreni sa: ./gradlew testObracun
 */
public class ObracunTest {

    public static void main(String[] args) {
        System.out.println("=== ePorezi - Test Obračuna ===\n");

        ObracunService service = new ObracunService();

        testZaradaStandard(service);
        testAutorskiHonorar(service);
        testUgovorODelu(service);

        System.out.println("\n=== Svi testovi završeni ===");
    }

    /**
     * Test za tip 101 (zarada) - standardni obračun
     */
    private static void testZaradaStandard(ObracunService service) {
        System.out.println("--- Test: Zarada Standard (tip 101) ---");

        // Kreiraj test stope (približne vrednosti za 2024)
        ObracunStope stope = new ObracunStope();
        stope.setTip("101");
        stope.setPorezP(0.10);  // 10% porez
        stope.setPioZ(0.14);    // 14% PIO zaposleni
        stope.setZdrZ(0.0515);  // 5.15% ZDR zaposleni
        stope.setNezZ(0.0075);  // 0.75% NEZ zaposleni
        stope.setPioP(0.10);    // 10% PIO poslodavac
        stope.setZdrP(0.0515);  // 5.15% ZDR poslodavac
        stope.setNezP(0.0);     // 0% NEZ poslodavac
        stope.setpOsl(new BigDecimal("25000")); // Poresko oslobođenje

        BigDecimal neto = new BigDecimal("100000");

        Obracun rezultat = service.izracunaj(neto, stope, BigDecimal.ONE, "01", "101");

        System.out.println("  NETO: " + neto);
        System.out.println("  BRUTO: " + rezultat.getBruto());
        System.out.println("  Osnovica porez: " + rezultat.getOsnovicaPorez());
        System.out.println("  Porez: " + rezultat.getPorez());
        System.out.println("  PIO ukupno: " + rezultat.getPio());
        System.out.println("  ZDR ukupno: " + rezultat.getZdr());
        System.out.println("  NEZ ukupno: " + rezultat.getNez());
        System.out.println("  PIO zaposleni: " + rezultat.getzPio());
        System.out.println("  PIO poslodavac: " + rezultat.getpPio());

        // Provera kontrolnog zbira
        // NETO = BRUTO - Porez - PIO_Z - ZDR_Z - NEZ_Z
        BigDecimal kontrolniNeto = rezultat.getBruto()
                .subtract(rezultat.getPorez())
                .subtract(rezultat.getzPio())
                .subtract(rezultat.getzZdr())
                .subtract(rezultat.getzNez());

        System.out.println("  Kontrolni NETO: " + kontrolniNeto);

        // Dozvoljena razlika zbog zaokruživanja
        BigDecimal razlika = neto.subtract(kontrolniNeto).abs();
        if (razlika.compareTo(new BigDecimal("5")) <= 0) {
            System.out.println("  ✓ Kontrolni zbir OK (razlika: " + razlika + ")\n");
        } else {
            System.out.println("  ✗ GREŠKA u kontrolnom zbiru! Razlika: " + razlika + "\n");
        }
    }

    /**
     * Test za tip 405 (autorski honorar) - samo porez
     */
    private static void testAutorskiHonorar(ObracunService service) {
        System.out.println("--- Test: Autorski Honorar (tip 405) ---");

        ObracunStope stope = new ObracunStope();
        stope.setTip("405");
        stope.setPorezP(0.20);      // 20% porez
        stope.setNormTrosak(0.34);  // 34% normirani troškovi
        // Nema doprinosa za autorski honorar

        BigDecimal neto = new BigDecimal("50000");

        Obracun rezultat = service.izracunaj(neto, stope, BigDecimal.ONE, "01", "405");

        System.out.println("  NETO: " + neto);
        System.out.println("  BRUTO: " + rezultat.getBruto());
        System.out.println("  Normirani troškovi: " + rezultat.getNormTroskovi());
        System.out.println("  Osnovica porez: " + rezultat.getOsnovicaPorez());
        System.out.println("  Porez: " + rezultat.getPorez());
        System.out.println("  PIO: " + rezultat.getPio() + " (očekivano 0)");
        System.out.println("  ZDR: " + rezultat.getZdr() + " (očekivano 0)");

        // Provera: NETO = BRUTO - Porez
        BigDecimal kontrolniNeto = rezultat.getBruto().subtract(rezultat.getPorez());
        System.out.println("  Kontrolni NETO: " + kontrolniNeto);

        BigDecimal razlika = neto.subtract(kontrolniNeto).abs();
        if (razlika.compareTo(new BigDecimal("5")) <= 0) {
            System.out.println("  ✓ Kontrolni zbir OK (razlika: " + razlika + ")\n");
        } else {
            System.out.println("  ✗ GREŠKA u kontrolnom zbiru! Razlika: " + razlika + "\n");
        }
    }

    /**
     * Test za tip 305/601/605 (ugovor o delu) - normirani troškovi + doprinosi
     *
     * NAPOMENA: Za ugovor o delu, SVI doprinosi (i P i Z) se oduzimaju od bruto
     * da bi se dobio neto. Ovo je specifično za ovaj tip ugovora.
     */
    private static void testUgovorODelu(ObracunService service) {
        System.out.println("--- Test: Ugovor o Delu (tip 601) ---");

        ObracunStope stope = new ObracunStope();
        stope.setTip("601");
        stope.setPorezP(0.20);      // 20% porez
        stope.setNormTrosak(0.20);  // 20% normirani troškovi
        stope.setPioZ(0.14);        // 14% PIO zaposleni
        stope.setZdrZ(0.0515);      // 5.15% ZDR zaposleni
        stope.setPioP(0.10);        // 10% PIO poslodavac
        stope.setZdrP(0.0515);      // 5.15% ZDR poslodavac
        // Nema NEZ za ugovor o delu

        BigDecimal neto = new BigDecimal("80000");

        Obracun rezultat = service.izracunaj(neto, stope, BigDecimal.ONE, "01", "601");

        System.out.println("  NETO: " + neto);
        System.out.println("  BRUTO: " + rezultat.getBruto());
        System.out.println("  Normirani troškovi: " + rezultat.getNormTroskovi());
        System.out.println("  Osnovica porez: " + rezultat.getOsnovicaPorez());
        System.out.println("  Osnovica doprinosi: " + rezultat.getOsnovicaDoprinosi());
        System.out.println("  Porez: " + rezultat.getPorez());
        System.out.println("  PIO ukupno: " + rezultat.getPio());
        System.out.println("  ZDR ukupno: " + rezultat.getZdr());
        System.out.println("  PIO (P+Z): " + rezultat.getpPio() + " + " + rezultat.getzPio());
        System.out.println("  ZDR (P+Z): " + rezultat.getpZdr() + " + " + rezultat.getzZdr());

        // Za ugovor o delu: NETO = BRUTO - Porez - SVI doprinosi (i P i Z)
        // Ovo je specifično za ovaj tip ugovora
        BigDecimal kontrolniNeto = rezultat.getBruto()
                .subtract(rezultat.getPorez())
                .subtract(rezultat.getPio())  // PIO ukupno (P + Z)
                .subtract(rezultat.getZdr()); // ZDR ukupno (P + Z)

        System.out.println("  Kontrolni NETO: " + kontrolniNeto);

        BigDecimal razlika = neto.subtract(kontrolniNeto).abs();
        if (razlika.compareTo(new BigDecimal("5")) <= 0) {
            System.out.println("  ✓ Kontrolni zbir OK (razlika: " + razlika + ")\n");
        } else {
            System.out.println("  ✗ GREŠKA u kontrolnom zbiru! Razlika: " + razlika + "\n");
        }
    }
}
