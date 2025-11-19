package it.uniba.dib.sms222312.registrazione;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import it.uniba.dib.sms222312.LoadingDialog;
import it.uniba.dib.sms222312.R;
import it.uniba.dib.sms222312.modelli.Docente;
import it.uniba.dib.sms222312.modelli.Studente;

public class RegistryDocente extends AppCompatActivity {
    private final LoadingDialog loadingDialog = new LoadingDialog(RegistryDocente.this);
    private Studente studente;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry_docente);
        Button btnRegistry = findViewById(R.id.btn_register);
        db = FirebaseFirestore.getInstance();
        btnRegistry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadingDialog.startLoadingDialog();
                if (registra()){
                    loadingDialog.dismissDialog();
                    return;
                }
            }
        });

    }

    private boolean registra() {
        EditText edtEmail = findViewById(R.id.email);
        String email = edtEmail.getText().toString();
        EditText edtPassword = findViewById(R.id.password);
        String password = edtPassword.getText().toString();
        EditText edtNome = findViewById(R.id.edt_name);
        String nome = edtNome.getText().toString();
        EditText edtCognome = findViewById(R.id.edt_surname);
        String cognome = edtCognome.getText().toString();

        // Verifica che tutti i campi siano stati compilati
        if (email.isEmpty() || password.isEmpty()  || nome.isEmpty() || cognome.isEmpty()) {
            Toast.makeText(RegistryDocente.this, R.string.errorCampiObbligatori, Toast.LENGTH_SHORT).show();
            return true;
        }

        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegistryDocente.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String user = auth.getCurrentUser().getUid();
                            Docente docente = new Docente(user, email, nome, cognome);
                            if(docente.registraDocente(db,RegistryDocente.this))
                                loadingDialog.dismissDialog();
                        } else {
                            // Creazione di un nuovo utente non r                            // iuscita, mostra un messaggio di errore.
                            Toast.makeText(RegistryDocente.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            loadingDialog.dismissDialog();
                        }
                    }
                }); return false;
    }
    public void esci(View view) {
        finish();
    }
}