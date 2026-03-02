package rs.hbs.zarade.service;

import java.math.BigDecimal;

/**
 * Rezultat obračuna sa svim izračunatim vrednostima.
 *
 * Ova klasa služi kao DTO za prikazivanje rezultata obračuna.
 */
public class ObracunResult {

    // Ulazni podaci
    private BigDecimal neto;
    private BigDecimal procenat;
    private String svp3;

    // Bruto i troškovi
    private BigDecimal bruto;
    private BigDecimal normTroskovi;

    // Osnovice
    private BigDecimal osnovicaPorez;
    private BigDecimal osnovicaDoprinosi;

    // Porez
    private BigDecimal poreskoOslobodjenje;
    private BigDecimal porez;

    // Doprinosi ukupno
    private BigDecimal pio;
    private BigDecimal zdr;
    private BigDecimal nez;

    // Doprinosi - na teret poslodavca
    private BigDecimal pPio;
    private BigDecimal pZdr;
    private BigDecimal pNez;

    // Doprinosi - na teret zaposlenog
    private BigDecimal zPio;
    private BigDecimal zZdr;
    private BigDecimal zNez;

    // Izvedene vrednosti
    private BigDecimal ukupnoDoprinosiPoslodavac;
    private BigDecimal ukupnoDoprinosiZaposleni;
    private BigDecimal ukupnoDoprinosi;
    private BigDecimal ukupnoPorezIDoprinosi;
    private BigDecimal troskoviPoslodavca;

    // Getters and Setters

    public BigDecimal getNeto() {
        return neto;
    }

    public void setNeto(BigDecimal neto) {
        this.neto = neto;
    }

    public BigDecimal getProcenat() {
        return procenat;
    }

    public void setProcenat(BigDecimal procenat) {
        this.procenat = procenat;
    }

    public String getSvp3() {
        return svp3;
    }

    public void setSvp3(String svp3) {
        this.svp3 = svp3;
    }

    public BigDecimal getBruto() {
        return bruto;
    }

    public void setBruto(BigDecimal bruto) {
        this.bruto = bruto;
    }

    public BigDecimal getNormTroskovi() {
        return normTroskovi;
    }

    public void setNormTroskovi(BigDecimal normTroskovi) {
        this.normTroskovi = normTroskovi;
    }

    public BigDecimal getOsnovicaPorez() {
        return osnovicaPorez;
    }

    public void setOsnovicaPorez(BigDecimal osnovicaPorez) {
        this.osnovicaPorez = osnovicaPorez;
    }

    public BigDecimal getOsnovicaDoprinosi() {
        return osnovicaDoprinosi;
    }

    public void setOsnovicaDoprinosi(BigDecimal osnovicaDoprinosi) {
        this.osnovicaDoprinosi = osnovicaDoprinosi;
    }

    public BigDecimal getPoreskoOslobodjenje() {
        return poreskoOslobodjenje;
    }

    public void setPoreskoOslobodjenje(BigDecimal poreskoOslobodjenje) {
        this.poreskoOslobodjenje = poreskoOslobodjenje;
    }

    public BigDecimal getPorez() {
        return porez;
    }

    public void setPorez(BigDecimal porez) {
        this.porez = porez;
    }

    public BigDecimal getPio() {
        return pio;
    }

    public void setPio(BigDecimal pio) {
        this.pio = pio;
    }

    public BigDecimal getZdr() {
        return zdr;
    }

    public void setZdr(BigDecimal zdr) {
        this.zdr = zdr;
    }

    public BigDecimal getNez() {
        return nez;
    }

    public void setNez(BigDecimal nez) {
        this.nez = nez;
    }

    public BigDecimal getpPio() {
        return pPio;
    }

    public void setpPio(BigDecimal pPio) {
        this.pPio = pPio;
    }

    public BigDecimal getpZdr() {
        return pZdr;
    }

    public void setpZdr(BigDecimal pZdr) {
        this.pZdr = pZdr;
    }

    public BigDecimal getpNez() {
        return pNez;
    }

    public void setpNez(BigDecimal pNez) {
        this.pNez = pNez;
    }

    public BigDecimal getzPio() {
        return zPio;
    }

    public void setzPio(BigDecimal zPio) {
        this.zPio = zPio;
    }

    public BigDecimal getzZdr() {
        return zZdr;
    }

    public void setzZdr(BigDecimal zZdr) {
        this.zZdr = zZdr;
    }

    public BigDecimal getzNez() {
        return zNez;
    }

    public void setzNez(BigDecimal zNez) {
        this.zNez = zNez;
    }

    public BigDecimal getUkupnoDoprinosiPoslodavac() {
        return ukupnoDoprinosiPoslodavac;
    }

    public void setUkupnoDoprinosiPoslodavac(BigDecimal ukupnoDoprinosiPoslodavac) {
        this.ukupnoDoprinosiPoslodavac = ukupnoDoprinosiPoslodavac;
    }

    public BigDecimal getUkupnoDoprinosiZaposleni() {
        return ukupnoDoprinosiZaposleni;
    }

    public void setUkupnoDoprinosiZaposleni(BigDecimal ukupnoDoprinosiZaposleni) {
        this.ukupnoDoprinosiZaposleni = ukupnoDoprinosiZaposleni;
    }

    public BigDecimal getUkupnoDoprinosi() {
        return ukupnoDoprinosi;
    }

    public void setUkupnoDoprinosi(BigDecimal ukupnoDoprinosi) {
        this.ukupnoDoprinosi = ukupnoDoprinosi;
    }

    public BigDecimal getUkupnoPorezIDoprinosi() {
        return ukupnoPorezIDoprinosi;
    }

    public void setUkupnoPorezIDoprinosi(BigDecimal ukupnoPorezIDoprinosi) {
        this.ukupnoPorezIDoprinosi = ukupnoPorezIDoprinosi;
    }

    public BigDecimal getTroskoviPoslodavca() {
        return troskoviPoslodavca;
    }

    public void setTroskoviPoslodavca(BigDecimal troskoviPoslodavca) {
        this.troskoviPoslodavca = troskoviPoslodavca;
    }

    /**
     * Izračunava izvedene vrednosti (ukupni doprinosi, troškovi poslodavca).
     */
    public void izracunajIzvedene() {
        BigDecimal zero = BigDecimal.ZERO;

        // Ukupno doprinosi na teret poslodavca
        this.ukupnoDoprinosiPoslodavac = safeAdd(pPio, pZdr, pNez);

        // Ukupno doprinosi na teret zaposlenog
        this.ukupnoDoprinosiZaposleni = safeAdd(zPio, zZdr, zNez);

        // Ukupno doprinosi
        this.ukupnoDoprinosi = safeAdd(pio, zdr, nez);

        // Ukupno porez i doprinosi
        this.ukupnoPorezIDoprinosi = safeAdd(porez, ukupnoDoprinosi);

        // Troškovi poslodavca = Bruto + Doprinosi na teret poslodavca
        this.troskoviPoslodavca = safeAdd(bruto, ukupnoDoprinosiPoslodavac);
    }

    private BigDecimal safeAdd(BigDecimal... values) {
        BigDecimal result = BigDecimal.ZERO;
        for (BigDecimal v : values) {
            if (v != null) {
                result = result.add(v);
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("=== OBRAČUN ===\n");
        sb.append(String.format("NETO: %,15.2f RSD\n", neto));
        sb.append(String.format("BRUTO: %,14.2f RSD\n", bruto));
        sb.append("\n--- Osnovice ---\n");
        sb.append(String.format("Osnovica porez: %,10.2f RSD\n", osnovicaPorez));
        sb.append(String.format("Osnovica dopr.: %,10.2f RSD\n", osnovicaDoprinosi));
        if (normTroskovi != null && normTroskovi.compareTo(BigDecimal.ZERO) > 0) {
            sb.append(String.format("Norm. troškovi: %,10.2f RSD\n", normTroskovi));
        }
        sb.append("\n--- Porez ---\n");
        sb.append(String.format("Porez: %,19.2f RSD\n", porez));
        sb.append("\n--- Doprinosi (zaposleni) ---\n");
        sb.append(String.format("PIO: %,21.2f RSD\n", zPio));
        sb.append(String.format("ZDR: %,21.2f RSD\n", zZdr));
        sb.append(String.format("NEZ: %,21.2f RSD\n", zNez));
        sb.append("\n--- Doprinosi (poslodavac) ---\n");
        sb.append(String.format("PIO: %,21.2f RSD\n", pPio));
        sb.append(String.format("ZDR: %,21.2f RSD\n", pZdr));
        sb.append(String.format("NEZ: %,21.2f RSD\n", pNez));
        sb.append("\n--- Ukupno ---\n");
        sb.append(String.format("Troškovi poslodavca: %,.2f RSD\n", troskoviPoslodavca));
        return sb.toString();
    }
}
