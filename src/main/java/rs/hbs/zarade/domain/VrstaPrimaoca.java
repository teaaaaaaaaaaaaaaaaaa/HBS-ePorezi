package rs.hbs.zarade.domain;

/**
 * Vrsta primaoca prihoda (šifarnik).
 * Mapira se na tabelu: SFR_VRSTAPRIMAOCA
 * Baza: data_zarade.mdb (Main DB)
 */
public class VrstaPrimaoca {

    private Integer id;           // ID
    private String sifra;         // Sifra (01, 02, 03, 09...)
    private String naziv;         // Naziv (Zaposleni, Osnivač, itd.)

    public VrstaPrimaoca() {
    }

    public VrstaPrimaoca(Integer id, String sifra, String naziv) {
        this.id = id;
        this.sifra = sifra;
        this.naziv = naziv;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getSifra() {
        return sifra;
    }

    public void setSifra(String sifra) {
        this.sifra = sifra;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    /**
     * @return Prikaz u formatu "XX - Naziv"
     */
    public String getDisplayName() {
        return (sifra != null ? sifra : "") + " - " + (naziv != null ? naziv : "");
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
