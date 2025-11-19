package it.uniba.dib.sms222312.modelli;

public class Classifica {
    private String utente;
    private String nome;
    private String durata;
    private String media;
    private int pos;
    private String idTesi;

    /**
     * Costruttore della classe task
     * @param pos
     * @param idTesi
     * @param utente
     * @param nome
     * @param durata
     * @param media
     */
    public Classifica(int pos, String idTesi, String utente, String nome, String durata, String media) {
        this.utente = utente;
        this.pos=pos;
        this.idTesi=idTesi;
        this.nome = nome;
        this.durata = durata;
        this. media = media;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getDurata() {
        return durata;
    }

    public String getMedia() {
        return media;
    }

    public Classifica() {
    }

    public String getIdTesi() {
        return idTesi;
    }

    public void setIdTesi(String idTesi) {
        this.idTesi = idTesi;
    }

    @Override
    public String toString() {
        return "Classifica{" +
                "utente='" + utente + '\'' +
                ", nome='" + nome + '\'' +
                ", durata='" + durata + '\'' +
                ", media='" + media + '\'' +
                ", pos='" + pos + '\'' +
                '}';
    }

    public String getUtente() {
        return utente;
    }

    public String getNome() {
        return nome;
    }
}
