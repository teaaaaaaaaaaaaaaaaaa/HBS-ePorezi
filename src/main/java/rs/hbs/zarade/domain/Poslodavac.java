package rs.hbs.zarade.domain;

/**
 * Poslodavac / Isplatilac prihoda.
 *
 * Mapira se na tabelu: Poslodavac (12 kolona)
 * Baza: data_zarade.mdb (Main DB)
 */
public class Poslodavac {

    // === PRIMARNI KLJUC ===
    private Integer idPoslodavac;                   // IDPoslodavac [PK]

    // === IDENTIFIKACIJA ===
    private Integer tipIsplatioca;                  // TipIsplatioca
    private String poreskiIdentifikacioniBroj;      // PoreskiIdentifikacioniBroj (PIB), VARCHAR(9)
    private String maticniBrojIsplatioca;           // MaticniBrojisplatioca, VARCHAR(8)

    // === OSNOVNI PODACI ===
    private String nazivPrezimeIme;                 // NazivPrezimeIme
    private Integer brojZaposlenih;                 // BrojZaposlenih

    // === ADRESA ===
    private String sedistePrebivaliste;             // SedistePrebivaliste - sifra opstine, VARCHAR(3)
    private String ulicaIBroj;                      // UlicaIBroj
    private String grad;                            // Grad

    // === KONTAKT ===
    private String telefon;                         // Telefon
    private String email;                           // eMail

    // === ODGOVORNO LICE ===
    private String odgovornoLice;                   // OdgovornoLice

    // === KONSTRUKTORI ===

    public Poslodavac() {
    }

    // === POMOCNE METODE ===

    /**
     * @return Puna adresa u formatu "Ulica i broj, Grad"
     */
    public String getPunaAdresa() {
        StringBuilder sb = new StringBuilder();
        if (ulicaIBroj != null && !ulicaIBroj.isEmpty()) {
            sb.append(ulicaIBroj);
        }
        if (grad != null && !grad.isEmpty()) {
            if (sb.length() > 0) sb.append(", ");
            sb.append(grad);
        }
        return sb.toString();
    }

    // === GETTERI I SETTERI ===

    public Integer getIdPoslodavac() {
        return idPoslodavac;
    }

    public void setIdPoslodavac(Integer idPoslodavac) {
        this.idPoslodavac = idPoslodavac;
    }

    public Integer getTipIsplatioca() {
        return tipIsplatioca;
    }

    public void setTipIsplatioca(Integer tipIsplatioca) {
        this.tipIsplatioca = tipIsplatioca;
    }

    public String getPoreskiIdentifikacioniBroj() {
        return poreskiIdentifikacioniBroj;
    }

    public void setPoreskiIdentifikacioniBroj(String poreskiIdentifikacioniBroj) {
        this.poreskiIdentifikacioniBroj = poreskiIdentifikacioniBroj;
    }

    public String getMaticniBrojIsplatioca() {
        return maticniBrojIsplatioca;
    }

    public void setMaticniBrojIsplatioca(String maticniBrojIsplatioca) {
        this.maticniBrojIsplatioca = maticniBrojIsplatioca;
    }

    public String getNazivPrezimeIme() {
        return nazivPrezimeIme;
    }

    public void setNazivPrezimeIme(String nazivPrezimeIme) {
        this.nazivPrezimeIme = nazivPrezimeIme;
    }

    public Integer getBrojZaposlenih() {
        return brojZaposlenih;
    }

    public void setBrojZaposlenih(Integer brojZaposlenih) {
        this.brojZaposlenih = brojZaposlenih;
    }

    public String getSedistePrebivaliste() {
        return sedistePrebivaliste;
    }

    public void setSedistePrebivaliste(String sedistePrebivaliste) {
        this.sedistePrebivaliste = sedistePrebivaliste;
    }

    public String getUlicaIBroj() {
        return ulicaIBroj;
    }

    public void setUlicaIBroj(String ulicaIBroj) {
        this.ulicaIBroj = ulicaIBroj;
    }

    public String getGrad() {
        return grad;
    }

    public void setGrad(String grad) {
        this.grad = grad;
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

    public String getOdgovornoLice() {
        return odgovornoLice;
    }

    public void setOdgovornoLice(String odgovornoLice) {
        this.odgovornoLice = odgovornoLice;
    }

    @Override
    public String toString() {
        return "Poslodavac{" +
                "idPoslodavac=" + idPoslodavac +
                ", nazivPrezimeIme='" + nazivPrezimeIme + '\'' +
                ", poreskiIdentifikacioniBroj='" + poreskiIdentifikacioniBroj + '\'' +
                '}';
    }
}
