package it.uniba.dib.sms222312.modelli;

public class RichiestaTesi {
    private String studente;
    private String docente;
    private String tesi;
    private String descrizione;

    public RichiestaTesi(){}

    public RichiestaTesi(String studente, String docente, String tesi, String descrizione) {
        this.studente = studente;
        this.docente = docente;
        this.tesi = tesi;
        this.descrizione = descrizione;
    }

    public String getStudente() {
        return studente;
    }

    public String getDocente() {
        return docente;
    }

    public String getTesi() {
        return tesi;
    }

    public String getDescrizione() {
        return descrizione;
    }
}
