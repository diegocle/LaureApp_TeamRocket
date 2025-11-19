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

public class Docente {
    private String id;
    private String email;
    private String nome;
    private String cognome;

    public Docente(String id, String nome, String cognome) {
        this.id = id;
        this.nome = nome;
        this.cognome = cognome;
    }

    public Docente(String id, String email, String nome, String cognome) {
        this.id = id;
        this.email = email;
        this.nome = nome;
        this.cognome = cognome;
    }

    public String getId() {
        return id;
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

    @Override
    public String toString() {
        return nome + " " + cognome;
    }

    public boolean registraDocente(FirebaseFirestore db, Activity activity) {
        // Creazione di un nuovo utente riuscita
        boolean[] flag = {false};
        Map<String, Object> userDb = new HashMap<>();
        userDb.put("email", this.email);
        userDb.put("nome", this.nome);
        userDb.put("cognome", this.cognome);
        userDb.put("tipo", "docente");
        db.collection("utente").document(this.id).set(userDb)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(activity , R.string.successRegistrazione, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(activity , Login.class);
                        activity.startActivity(intent);
                        activity.finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(activity , R.string.errorRegistrazione, Toast.LENGTH_SHORT).show();
                        flag[0] =true;
                    }
                });
        return flag[0];
    }

}
