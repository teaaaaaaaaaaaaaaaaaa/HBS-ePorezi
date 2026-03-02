package rs.hbs.zarade.util;

import rs.hbs.zarade.db.*;
import rs.hbs.zarade.domain.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

/**
 * Test utility za proveru DAO sloja sa pravom bazom.
 */
public class DaoTest {

    public static void main(String[] args) {
        System.out.println("==============================================");
        System.out.println("DAO TEST - Citanje podataka iz baze");
        System.out.println("==============================================\n");

        try {
            DatabaseConfig config = DatabaseConfig.load();
            DatabaseConnection dbConnection = new DatabaseConnection(config);
            PrimalacDao primalacDao = new PrimalacDao();
            OpstinaDao opstinaDao = new OpstinaDao();
            VrstaPrimaocaDao vrstaPrimaocaDao = new VrstaPrimaocaDao();
            VrstaIsplatiocaDao vrstaIsplatiocaDao = new VrstaIsplatiocaDao();
            VrstaPrihodaDao vrstaPrihodaDao = new VrstaPrihodaDao();

            try (Connection conn = dbConnection.getMainConnection()) {
                // Test 1: Broj primalaca
                System.out.println("[1] Brojanje primalaca...");
                long count = primalacDao.count(conn);
                System.out.println("    Ukupno primalaca u bazi: " + count);
                System.out.println();

                // Test 2: Lista prvih 10 primalaca
                System.out.println("[2] Prvih 10 primalaca (sortirano po prezimenu):");
                List<Primalac> sviPrimaoci = primalacDao.findAll(conn);
                int limit = Math.min(10, sviPrimaoci.size());
                for (int i = 0; i < limit; i++) {
                    Primalac p = sviPrimaoci.get(i);
                    System.out.printf("    %2d. %-25s MB: %-13s Status: %s%n",
                            i + 1,
                            p.getPunoIme(),
                            p.getMb() != null ? p.getMb() : "N/A",
                            p.getStatus() != null ? p.getStatus() : "N/A");
                }
                System.out.println();

                // Test 3: Pretraga po imenu
                System.out.println("[3] Pretraga primalaca cije prezime pocinje sa 'M':");
                List<Primalac> pretragaM = primalacDao.findByPrezimeAndIme(conn, "M", "");
                System.out.println("    Pronadjeno: " + pretragaM.size() + " primalaca");
                int limitM = Math.min(5, pretragaM.size());
                for (int i = 0; i < limitM; i++) {
                    Primalac p = pretragaM.get(i);
                    System.out.printf("    - %s (%s)%n", p.getPunoIme(), p.getMb());
                }
                System.out.println();

                // Test 4: Pronalazak po ID-u (prvi iz liste)
                if (!sviPrimaoci.isEmpty()) {
                    Integer prviId = sviPrimaoci.get(0).getIdZaposleni();
                    System.out.println("[4] Pronalazak primaoca po ID=" + prviId + ":");
                    primalacDao.findById(conn, prviId).ifPresent(p -> {
                        System.out.println("    Ime: " + p.getPunoIme());
                        System.out.println("    JMBG: " + p.getMb());
                        System.out.println("    Adresa: " + p.getPunaAdresa());
                        System.out.println("    Email: " + (p.getEmail() != null ? p.getEmail() : "N/A"));
                        System.out.println("    Procenat zaposlenja: " +
                            (p.getProcenatZaposlenja() != null ? p.getProcenatZaposlenja() + "%" : "N/A"));
                    });
                }

                System.out.println();

                // Test 5: Sifarnici
                System.out.println("[5] SFR_OPSTINA:");
                List<Opstina> opstine = opstinaDao.findAll(conn);
                System.out.println("    Ucitano opstina: " + opstine.size());
                opstine.stream().limit(5).forEach(o ->
                    System.out.println("    - " + o.getDisplayName()));
                System.out.println();

                System.out.println("[6] SFR_VRSTAPRIMAOCA:");
                List<VrstaPrimaoca> vrstePrimaoca = vrstaPrimaocaDao.findAll(conn);
                System.out.println("    Ucitano vrsta primaoca: " + vrstePrimaoca.size());
                vrstePrimaoca.forEach(vp ->
                    System.out.println("    - " + vp.getId() + " | " + vp.getNaziv()));
                System.out.println();

                System.out.println("[7] SFR_VRSTAISPLATIOCA:");
                List<VrstaIsplatioca> vrsteIsplatioca = vrstaIsplatiocaDao.findAll(conn);
                System.out.println("    Ucitano vrsta isplatioca: " + vrsteIsplatioca.size());
                vrsteIsplatioca.forEach(vi ->
                    System.out.println("    - " + vi.getId() + " | " + vi.getNaziv()));
                System.out.println();

                System.out.println("[8] SFR_VRSTAPRIHODA:");
                List<VrstaPrihoda> vrstePrihoda = vrstaPrihodaDao.findAll(conn);
                System.out.println("    Ucitano vrsta prihoda: " + vrstePrihoda.size());
                vrstePrihoda.forEach(vp ->
                    System.out.println("    - " + vp.getVpz() + " | " + vp.getNaziv() + " | Grupa: " + vp.getGrupaSvr()));
                System.out.println();

                System.out.println("==============================================");
                System.out.println("DAO TEST USPESNO ZAVRSEN!");
                System.out.println("==============================================");
            }

        } catch (DatabaseConfigException e) {
            System.err.println("Greska konfiguracije: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("SQL Greska: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
