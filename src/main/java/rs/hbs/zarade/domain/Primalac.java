package rs.hbs.zarade.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Primalac prihoda (zaposleni/honorarac).
 *
 * Mapira se na tabelu: Primaoci (33 kolone)
 * Baza: data_zarade.mdb (Main DB)
 */
public class Primalac {

    // === PRIMARNI KLJUC ===
    private Integer idZaposleni;                    // IDZaposleni [PK]

    // === IDENTIFIKACIJA ===
    private Integer vrstaIdentifikatoraPrimaoca;    // VrstaIdentifikatoraPrimaoca
    private Integer vrstaPrimaocaId;                // VrstaPrimaocaID
    private String mb;                              // MB - Maticni broj (JMBG), VARCHAR(13)
    private String oznakaPrebivalista;              // OznakaPrebivalista, VARCHAR(3)

    // === LICNI PODACI ===
    private String prezime;                         // Prezime
    private String ime;                             // Ime
    private String prezimeCyr;                      // PrezimeCyr - cirilica
    private String imeCyr;                          // ImeCyr - cirilica
    private String prezimeLat;                      // PrezimeLAT - latinica
    private String imeLat;                          // ImeLAT - latinica
    private LocalDate datumR;                       // DatumR - datum rodjenja
    private String mestoR;                          // MestoR - mesto rodjenja

    // === ADRESA ===
    private String adresaP;                         // AdresaP - adresa prebivalista
    private String pbGradP;                         // PBGradP - postanski broj i grad
    private String opstina;                         // Opstina - opština

    // === KONTAKT ===
    private String telefon;                         // Telefon
    private String email;                           // Email
    private String mobilni;                         // Mobilni

    // === ZAPOSLENJE ===
    private String datumZaposlenja;                 // DatumZaposlenja
    private LocalDate datumPrestanka;               // DatumPrestanka
    private Integer status;                         // Status
    private String radnaPozicija;                   // RadnaPozicija
    private Boolean zap;                            // Zap - da li je zaposlen
    private Boolean penz;                           // Penz - da li je penzioner
    private Integer procenatZaposlenja;             // ProcenatZaposlenja (npr. 100 za puno radno vreme)
    private String prethodniStaz;                   // PrethodniStaz
    private Integer strucnaSprema;                  // StrucnaSprema
    private Double koeficijent;                     // Koeficijent

    // === SINDIKAT ===
    private BigDecimal sindikatNomIznos;            // SindikatNomIznos
    private Double sindikatProcenat;                // SindikatProcenat

    // === META PODACI ===
    private String napomena;                        // Napomena
    private LocalDateTime datumAzuriranja;          // DatumAzuriranja
    private Integer lnkAzurirao;                    // lnkAzurirao - ko je azurirao

    // === KONSTRUKTORI ===

    public Primalac() {
    }

    // === POMOCNE METODE ===

    /**
     * @return Puno ime u formatu "Prezime Ime"
     */
    public String getPunoIme() {
        return (prezime != null ? prezime : "") + " " + (ime != null ? ime : "");
    }

    /**
     * @return Puna adresa u formatu "Adresa, PB Grad"
     */
    public String getPunaAdresa() {
        return (adresaP != null ? adresaP : "") + ", " + (pbGradP != null ? pbGradP : "");
    }

    /**
     * @return Procenat zaposlenja kao decimalni broj (0.0 - 1.0)
     */
    public BigDecimal getProcenatZaposlenjaDecimal() {
        if (procenatZaposlenja == null) return BigDecimal.ONE;
        return new BigDecimal(procenatZaposlenja).divide(new BigDecimal(100));
    }

    // === GETTERI I SETTERI ===

    public Integer getIdZaposleni() {
        return idZaposleni;
    }

    public void setIdZaposleni(Integer idZaposleni) {
        this.idZaposleni = idZaposleni;
    }

    public Integer getVrstaIdentifikatoraPrimaoca() {
        return vrstaIdentifikatoraPrimaoca;
    }

    public void setVrstaIdentifikatoraPrimaoca(Integer vrstaIdentifikatoraPrimaoca) {
        this.vrstaIdentifikatoraPrimaoca = vrstaIdentifikatoraPrimaoca;
    }

    public Integer getVrstaPrimaocaId() {
        return vrstaPrimaocaId;
    }

    public void setVrstaPrimaocaId(Integer vrstaPrimaocaId) {
        this.vrstaPrimaocaId = vrstaPrimaocaId;
    }

    public String getMb() {
        return mb;
    }

    public void setMb(String mb) {
        this.mb = mb;
    }

    public String getOznakaPrebivalista() {
        return oznakaPrebivalista;
    }

    public void setOznakaPrebivalista(String oznakaPrebivalista) {
        this.oznakaPrebivalista = oznakaPrebivalista;
    }

    public String getPrezime() {
        return prezime;
    }

    public void setPrezime(String prezime) {
        this.prezime = prezime;
    }

    public String getIme() {
        return ime;
    }

    public void setIme(String ime) {
        this.ime = ime;
    }

    public String getPrezimeCyr() {
        return prezimeCyr;
    }

    public void setPrezimeCyr(String prezimeCyr) {
        this.prezimeCyr = prezimeCyr;
    }

    public String getImeCyr() {
        return imeCyr;
    }

    public void setImeCyr(String imeCyr) {
        this.imeCyr = imeCyr;
    }

    public String getPrezimeLat() {
        return prezimeLat;
    }

    public void setPrezimeLat(String prezimeLat) {
        this.prezimeLat = prezimeLat;
    }

    public String getImeLat() {
        return imeLat;
    }

    public void setImeLat(String imeLat) {
        this.imeLat = imeLat;
    }

    public LocalDate getDatumR() {
        return datumR;
    }

    public void setDatumR(LocalDate datumR) {
        this.datumR = datumR;
    }

    public String getMestoR() {
        return mestoR;
    }

    public void setMestoR(String mestoR) {
        this.mestoR = mestoR;
    }

    public String getAdresaP() {
        return adresaP;
    }

    public void setAdresaP(String adresaP) {
        this.adresaP = adresaP;
    }

    public String getPbGradP() {
        return pbGradP;
    }

    public void setPbGradP(String pbGradP) {
        this.pbGradP = pbGradP;
    }

    public String getOpstina() {
        return opstina;
    }

    public void setOpstina(String opstina) {
        this.opstina = opstina;
    }

    public String getTelefon() {
        return telefon;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobilni() {
        return mobilni;
    }

    public void setMobilni(String mobilni) {
        this.mobilni = mobilni;
    }

    public String getDatumZaposlenja() {
        return datumZaposlenja;
    }

    public void setDatumZaposlenja(String datumZaposlenja) {
        this.datumZaposlenja = datumZaposlenja;
    }

    public LocalDate getDatumPrestanka() {
        return datumPrestanka;
    }

    public void setDatumPrestanka(LocalDate datumPrestanka) {
        this.datumPrestanka = datumPrestanka;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRadnaPozicija() {
        return radnaPozicija;
    }

    public void setRadnaPozicija(String radnaPozicija) {
        this.radnaPozicija = radnaPozicija;
    }

    public Boolean getZap() {
        return zap;
    }

    public void setZap(Boolean zap) {
        this.zap = zap;
    }

    public Boolean getPenz() {
        return penz;
    }

    public void setPenz(Boolean penz) {
        this.penz = penz;
    }

    public Integer getProcenatZaposlenja() {
        return procenatZaposlenja;
    }

    public void setProcenatZaposlenja(Integer procenatZaposlenja) {
        this.procenatZaposlenja = procenatZaposlenja;
    }

    public String getPrethodniStaz() {
        return prethodniStaz;
    }

    public void setPrethodniStaz(String prethodniStaz) {
        this.prethodniStaz = prethodniStaz;
    }

    public Integer getStrucnaSprema() {
        return strucnaSprema;
    }

    public void setStrucnaSprema(Integer strucnaSprema) {
        this.strucnaSprema = strucnaSprema;
    }

    public Double getKoeficijent() {
        return koeficijent;
    }

    public void setKoeficijent(Double koeficijent) {
        this.koeficijent = koeficijent;
    }

    public BigDecimal getSindikatNomIznos() {
        return sindikatNomIznos;
    }

    public void setSindikatNomIznos(BigDecimal sindikatNomIznos) {
        this.sindikatNomIznos = sindikatNomIznos;
    }

    public Double getSindikatProcenat() {
        return sindikatProcenat;
    }

    public void setSindikatProcenat(Double sindikatProcenat) {
        this.sindikatProcenat = sindikatProcenat;
    }

    public String getNapomena() {
        return napomena;
    }

    public void setNapomena(String napomena) {
        this.napomena = napomena;
    }

    public LocalDateTime getDatumAzuriranja() {
        return datumAzuriranja;
    }

    public void setDatumAzuriranja(LocalDateTime datumAzuriranja) {
        this.datumAzuriranja = datumAzuriranja;
    }

    public Integer getLnkAzurirao() {
        return lnkAzurirao;
    }

    public void setLnkAzurirao(Integer lnkAzurirao) {
        this.lnkAzurirao = lnkAzurirao;
    }

    @Override
    public String toString() {
        return "Primalac{" +
                "idZaposleni=" + idZaposleni +
                ", prezime='" + prezime + '\'' +
                ", ime='" + ime + '\'' +
                ", mb='" + mb + '\'' +
                '}';
    }
}
