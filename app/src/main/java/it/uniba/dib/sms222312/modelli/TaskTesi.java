package it.uniba.dib.sms222312.modelli;

import java.io.Serializable;
import java.util.List;

public class TaskTesi implements Serializable {
    private String tesista;
    private String nome;
    private String descrizione;
    private String stato;
    private String scadenza;
    private List<String> file;

    public TaskTesi(){}

    @Override
    public String toString() {
        return "TaskTesi{" +
                "tesista='" + tesista + '\'' +
                ", nome='" + nome + '\'' +
                ", descrizione='" + descrizione + '\'' +
                ", stato='" + stato + '\'' +
                ", scadenza='" + scadenza + '\'' +
                ", file=" + file +
                '}';
    }

    public TaskTesi(String nome) {
        this.nome = nome;
    }

    /**
     * Costruttore della classe task
     * @param tesista
     * @param nome
     * @param descrizione
     * @param scadenza
     * @param stato
     * @param file
     */
    public TaskTesi(String tesista, String nome, String descrizione, String scadenza, String stato, List<String> file) {
        this.tesista = tesista;
        this.nome = nome;
        this.descrizione = descrizione;
        this.stato = scadenza;
        this.scadenza = scadenza;
        this.stato = stato;
        this.file = file;
    }

    /**
     * restituisce id del tesista
     * @return String tesista
     */
    public String getTesista() {
        return tesista;
    }

    /**
     * restituisce nome del task
     * @return String nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * restituisce descrizione del task
     * @return String descrizione
     */
    public String getDescrizione() {
        return descrizione;
    }

    /**
     * restituisce lo stato del task
     * @return String stato
     */
    public String getStato() {
        return stato;
    }

    /**
     * restituisce la data della scadenza del task
     * @return String della data scadenza
     */
    public String getScadenza() {
        return scadenza;
    }

    /**
     * restituisce la lista del Url dei file assosciati al task
     * @return List<String>
     */
    public List<String> getFile() {
        return file;
    }
}
