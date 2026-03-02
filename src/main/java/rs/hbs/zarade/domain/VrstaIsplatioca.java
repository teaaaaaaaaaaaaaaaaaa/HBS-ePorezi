package rs.hbs.zarade.domain;

/**
 * Vrsta isplatioca (šifarnik).
 * Mapira se na tabelu: SFR_VRSTAISPLATIOCA
 * Baza: data_zarade.mdb (Main DB)
 *
 * Stvarne kolone: IDvi (PK, integer 1-7), VI (naziv)
 */
public class VrstaIsplatioca {

    private Integer id;     // IDvi (1-7)
    private String naziv;   // VI

    public VrstaIsplatioca() {
    }

    public VrstaIsplatioca(Integer id, String naziv) {
        this.id = id;
        this.naziv = naziv;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getDisplayName() {
        return (id != null ? id : "") + " - " + (naziv != null ? naziv : "");
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
