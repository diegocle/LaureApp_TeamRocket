package it.uniba.dib.sms222312.modelli;

public class Ricevimento {
    private String tesista;
    private String task;
    private String data;
    private String dettagli;
    private StatoRicevimento stato;

    public Ricevimento(){}

    public Ricevimento(String tesista, String task, String data, String dettagli, StatoRicevimento stato) {
        this.tesista = tesista;
        this.task = task;
        this.data = data;
        this.dettagli = dettagli;
        this.stato= stato;
    }

    public Ricevimento(String tesista, String task, String data, String dettagli) {
        this.tesista = tesista;
        this.task = task;
        this.data = data;
        this.dettagli = dettagli;
    }

    public String getTesista() {
        return tesista;
    }

    public String getTask() {
        return task;
    }

    public String getData() {
        return data;
    }

    public StatoRicevimento getStato(){
        return stato;
    }

    public String getDettagli() {
        return dettagli;
    }
}
