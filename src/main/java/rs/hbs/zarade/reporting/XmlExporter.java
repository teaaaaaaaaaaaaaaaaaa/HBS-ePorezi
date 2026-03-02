package rs.hbs.zarade.reporting;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import rs.hbs.zarade.domain.Obracun;
import rs.hbs.zarade.domain.ObracunDefinicija;
import rs.hbs.zarade.domain.Poslodavac;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Generisanje PPP-PO XML za Poresku upravu Srbije.
 *
 * Struktura zasnovana na: stari_access/vba/Zarade/mod_GenXML.txt
 *
 * NAPOMENA: Ovaj XML se direktno koristi za prijavljivanje Poreskoj upravi.
 * Struktura mora biti 100% kompatibilna sa propisanim formatom.
 */
public class XmlExporter {

    private static final String TNS_NS = "http://pid.purs.gov.rs";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Generiše PPP-PO XML string za pregled.
     *
     * @param definicija zaglavlje obračuna
     * @param stavke     lista pojedinačnih obračuna
     * @param poslodavac podaci o isplatiocu
     * @return XML kao String
     */
    public static String generateXmlString(ObracunDefinicija definicija,
                                           List<Obracun> stavke,
                                           Poslodavac poslodavac) throws Exception {
        Document doc = buildDocument(definicija, stavke, poslodavac);
        return docToString(doc);
    }

    /**
     * Upisuje PPP-PO XML u fajl.
     *
     * @param outputFile odredišni fajl
     * @param definicija zaglavlje obračuna
     * @param stavke     lista pojedinačnih obračuna
     * @param poslodavac podaci o isplatiocu
     */
    public static void exportToFile(File outputFile,
                                    ObracunDefinicija definicija,
                                    List<Obracun> stavke,
                                    Poslodavac poslodavac) throws Exception {
        Document doc = buildDocument(definicija, stavke, poslodavac);
        writeDocToFile(doc, outputFile);
    }

    // -----------------------------------------------------------------------
    // Private implementation
    // -----------------------------------------------------------------------

    private static Document buildDocument(ObracunDefinicija definicija,
                                          List<Obracun> stavke,
                                          Poslodavac poslodavac) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.newDocument();

        // Root: PPP_PO
        Element root = doc.createElementNS(TNS_NS, "tns:PPP_PO");
        root.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:tns", TNS_NS);

        String vrstaPrijave = "1"; // 1 = originalna
        String obracunskiPeriod = definicija.getMesec() != null ? definicija.getMesec() : "";
        String pib = poslodavac != null && poslodavac.getPoreskiIdentifikacioniBroj() != null
                ? poslodavac.getPoreskiIdentifikacioniBroj() : "";

        root.setAttribute("VrstaPrijave", vrstaPrijave);
        root.setAttribute("ObracunskiPeriod", obracunskiPeriod);
        root.setAttribute("PoreskiIdentBrPodnosioca", pib);

        // Podnosilac
        Element podnosilac = doc.createElementNS(TNS_NS, "tns:Podnosilac");
        if (poslodavac != null) {
            podnosilac.setAttribute("PIB", nvl(poslodavac.getPoreskiIdentifikacioniBroj()));
            podnosilac.setAttribute("MB", nvl(poslodavac.getMaticniBrojIsplatioca()));
            podnosilac.setAttribute("NazivPodnosioca", nvl(poslodavac.getNazivPrezimeIme()));
            podnosilac.setAttribute("BrojZaposlenih",
                    poslodavac.getBrojZaposlenih() != null ?
                            String.valueOf(poslodavac.getBrojZaposlenih()) : "0");
        }
        root.appendChild(podnosilac);

        // ObracunskiPodaci
        Element obracunskiPodaci = doc.createElementNS(TNS_NS, "tns:ObracunskiPodaci");
        obracunskiPodaci.setAttribute("NazivObracuna", nvl(definicija.getNazivObracuna()));
        obracunskiPodaci.setAttribute("DatumObracuna",
                definicija.getDatumObracuna() != null ?
                        definicija.getDatumObracuna().format(DATE_FMT) : "");
        obracunskiPodaci.setAttribute("OznakaKonacne", nvl(definicija.getOznakaZaKonacnu()));
        root.appendChild(obracunskiPodaci);

        // PojPrijava — jedna po primaocu
        Element pojPrijava = doc.createElementNS(TNS_NS, "tns:PojPrijava");

        for (Obracun o : stavke) {
            if (o.getNeto() == null || o.getNeto().compareTo(BigDecimal.ZERO) <= 0) {
                continue; // preskoči nulte stavke
            }

            Element primalac = doc.createElementNS(TNS_NS, "tns:Primalac");
            primalac.setAttribute("VrstaIdentifikatora",
                    nvl(o.getVrstaIdentifikatoraPrimaoca()));
            primalac.setAttribute("Identifikator",
                    nvl(o.getIdentifikatorPrimaoca()));
            primalac.setAttribute("Prezime", nvl(o.getPrezime()));
            primalac.setAttribute("Ime", nvl(o.getIme()));
            primalac.setAttribute("OznakaPrebivalista", nvl(o.getOznakaPrebivalista()));

            // SVP kodovi
            primalac.setAttribute("SVP1", nvl(o.getSvp1()));
            primalac.setAttribute("SVP2", nvl(o.getSvp2()));
            primalac.setAttribute("SVP3", nvl(o.getSvp3()));

            // Prihodi
            Element prihod = doc.createElementNS(TNS_NS, "tns:Prihod");
            prihod.setAttribute("VPZ", nvl(o.getSvp3()));
            prihod.setAttribute("Bruto", bd(o.getBruto()));
            prihod.setAttribute("Neto", bd(o.getNeto()));
            prihod.setAttribute("Porez", bd(o.getPorez()));
            prihod.setAttribute("PIO", bd(o.getPio()));
            prihod.setAttribute("ZDR", bd(o.getZdr()));
            prihod.setAttribute("NEZ", bd(o.getNez()));

            primalac.appendChild(prihod);
            pojPrijava.appendChild(primalac);
        }

        root.appendChild(pojPrijava);
        doc.appendChild(root);
        return doc;
    }

    private static String docToString(Document doc) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        StringWriter sw = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(sw));
        return sw.toString();
    }

    private static void writeDocToFile(Document doc, File file) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");

        transformer.transform(new DOMSource(doc), new StreamResult(file));
    }

    private static String nvl(String value) {
        return value != null ? value : "";
    }

    private static String bd(BigDecimal value) {
        return value != null ? value.toPlainString() : "0";
    }
}
