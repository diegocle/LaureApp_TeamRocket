package it.uniba.dib.sms222312.modelli;

import com.google.firebase.firestore.FirebaseFirestore;

import java.io.Serializable;

public class Tesi implements Serializable {
    private String dipartimento;
    private String idTesi;
    private String docente;
    private String nome;
    private String corso;
    private String durata;
    private String media;
    private String descrizione;

    public Tesi(){}
    public Tesi(String docente, String nome, String corso, String oreDurata, String mediaVoti, String descrizione) {
        this.docente = docente;
        this.nome = nome;
        this.corso = corso;
        this.durata = oreDurata;
        this.media = mediaVoti;
        this.descrizione = descrizione;
    }



    public Tesi(String docente, String nome, String dib, String corso, String oreDurata, String mediaVoti, String descrizione) {
        this.docente = docente;
        this.nome = nome;
        this.dipartimento = dib;
        this.corso = corso;
        this.durata = oreDurata;
        this.media = mediaVoti;
        this.descrizione = descrizione;
    }

    public String getIdTesi() {
        return idTesi;
    }

    public void setIdTesi(String idTesi) {
        this.idTesi = idTesi;
    }

    public String getDipartimento() {
        return dipartimento;
    }

    public void setDipartimento(String dipartimento) {
        this.dipartimento = dipartimento;
    }

    public String getDocente() {
        return docente;
    }

    public String getNome() {
        return nome;
    }

    public String getCorso() {
        return corso;
    }

    public String getDurata() {
        return durata;
    }

    @Override
    public String toString() {
        return "Tesi{" +
                "docente='" + docente + '\'' +
                ", id='" + idTesi + '\'' +
                ", nome='" + nome + '\'' +
                ", corso='" + corso + '\'' +
                ", ore='" + durata + '\'' +
                ", media='" + media + '\'' +
                ", descrizione='" + descrizione + '\'' +
                '}';
    }

    public String getMedia() {
        return media;
    }

    public String getDescrizione() {
        return descrizione;
    }
}
