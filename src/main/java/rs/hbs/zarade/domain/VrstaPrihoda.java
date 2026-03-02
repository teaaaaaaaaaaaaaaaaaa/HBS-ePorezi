package rs.hbs.zarade.domain;

/**
 * Vrsta prihoda (šifarnik).
 * Mapira se na tabelu: SFR_VRSTAPRIHODA
 * Baza: data_zarade.mdb (Main DB)
 *
 * Stvarne kolone: VPZ (PK, šifra npr. "303"), NAZIV, OPIS, GrupaSVR
 */
public class VrstaPrihoda {

    private String vpz;       // VPZ - šifra (PK), npr. "303", "304"
    private String naziv;     // NAZIV
    private String opis;      // OPIS
    private String grupaSvr;  // GrupaSVR

    public VrstaPrihoda() {
    }

    public String getVpz() {
        return vpz;
    }

    public void setVpz(String vpz) {
        this.vpz = vpz;
    }

    public String getNaziv() {
        return naziv;
    }

    public void setNaziv(String naziv) {
        this.naziv = naziv;
    }

    public String getOpis() {
        return opis;
    }

    public void setOpis(String opis) {
        this.opis = opis;
    }

    public String getGrupaSvr() {
        return grupaSvr;
    }

    public void setGrupaSvr(String grupaSvr) {
        this.grupaSvr = grupaSvr;
    }

    public String getDisplayName() {
        return (vpz != null ? vpz : "") + " - " + (naziv != null ? naziv : "");
    }

    @Override
    public String toString() {
        return getDisplayName();
    }
}
