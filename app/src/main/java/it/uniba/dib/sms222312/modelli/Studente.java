package it.uniba.dib.sms222312.modelli;

import android.app.Activity;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import it.uniba.dib.sms222312.registrazione.Login;
import it.uniba.dib.sms222312.R;

public class Studente {
    private String id;
    private String matricola;

    private String dipartimento;
    private String email;
    private String nome;
    private String cognome;
    private String corso;


    public  Studente(){}
    public Studente(String id, String matricola, String email, String nome, String cognome,String dipartimento ,String corso) {
        this.id = id;
        this.matricola = matricola;
        this.email = email;
        this.nome = nome;
        this.dipartimento = dipartimento;
        this.cognome = cognome;
        this.corso = corso;
    }
    public Studente(String id, String matricola, String email, String nome, String cognome, String corso) {
        this.id = id;
        this.matricola = matricola;
        this.email = email;
        this.nome = nome;
        this.cognome = cognome;
        this.corso = corso;
    }

    public String getCorso() { return corso;}

    public String getId() {
        return id;
    }

    public String getMatricola() {
        return matricola;
    }

    public String getEmail() {
        return email;
    }

    public String getNome() {
        return nome;
    }

    public String getCognome() {
        return cognome;
    }

    public boolean registraUtente(FirebaseFirestore db, Activity activity) {
        boolean[] flag = {false};
        Map<String, Object> userDb = new HashMap<>();
        userDb.put("email", this.email);
        userDb.put("matricola", this.matricola);
        userDb.put("nome", this.nome);
        userDb.put("cognome", this.cognome);
        userDb.put("dipartimento", this.dipartimento);
        userDb.put("corso", this.corso);
        userDb.put("tipo", "studente");
        db.collection("utente").document(this.id).set(userDb)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(activity, R.string.successRegistrazione, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(activity, Login.class);
                        activity.startActivity(intent);
                        activity.finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity, R.string.errorRegistrazione, Toast.LENGTH_SHORT).show();
                        flag[0] =true;
                    }
                });
        return flag[0];
    }
}
