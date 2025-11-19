package it.uniba.dib.sms222312.modelli;

public class Tesista {

    private String studente;
    private String tesi;
    private String relatore;
    private String corelatore = null;

    public Tesista(){}

    public Tesista(String studente, String tesi, String relatore, String corelatore) {
        this.studente = studente;
        this.tesi = tesi;
        this.relatore = relatore;
        this.corelatore = corelatore;
    }

    public Tesista(String studente, String tesi, String relatore) {
        this.studente = studente;
        this.tesi = tesi;
        this.relatore = relatore;
    }

    public String getStudente() {
        return studente;
    }

    public String getTesi() {
        return tesi;
    }

    public String getRelatore() {
        return relatore;
    }

    public String getCorelatore() {
        return corelatore;
    }
}
