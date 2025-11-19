package it.uniba.dib.sms222312.registrazione;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
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
import it.uniba.dib.sms222312.utenti.UpdateSpinner;
import it.uniba.dib.sms222312.modelli.Studente;

public class Registry extends AppCompatActivity {
private Studente studente;
private FirebaseFirestore db;
private final LoadingDialog loadingDialog = new LoadingDialog(Registry.this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registry);
        db = FirebaseFirestore.getInstance();
        AutoCompleteTextView spinnerCourses = findViewById(R.id.spinner_dipartimento);
        UpdateSpinner updateSpinner = new UpdateSpinner(spinnerCourses,findViewById(R.id.spinner_corso));
        updateSpinner.updateSpinnerD(db);
        spinnerCourses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateSpinner.updateSpinnerC(parent,position,db);
            }
        });

        Button btnRegistry = findViewById(R.id.btn_register);
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
        EditText edtMatricola = findViewById(R.id.matricola);
        String matricola = edtMatricola.getText().toString();
        EditText edtNome = findViewById(R.id.edt_name);
        String nome = edtNome.getText().toString();
        EditText edtCognome = findViewById(R.id.edt_surname);
        String cognome = edtCognome.getText().toString();
        AutoCompleteTextView coursesSpinner = findViewById(R.id.spinner_corso);
        Object selectedItem = coursesSpinner.getText();
        String corso = selectedItem.toString();
        AutoCompleteTextView coursesSpinner1 = findViewById(R.id.spinner_dipartimento);
        Object selectedItem1 = coursesSpinner1.getText();
        String dipartimento = selectedItem1.toString();
        // Verifica che tutti i campi siano stati compilati
        if (email.isEmpty() || password.isEmpty() || matricola.isEmpty() || nome.isEmpty() || cognome.isEmpty() ) {
            Toast.makeText(Registry.this, R.string.errorCampiObbligatori, Toast.LENGTH_SHORT).show();
            return true;
        }
        if(matricola.length()!=6){
            Toast.makeText(Registry.this, R.string.errorMatricola, Toast.LENGTH_SHORT).show();
            return true;
        }
        if(corso.equals(getString(R.string.Seleziona_corso)) || dipartimento.isEmpty()){
            Toast.makeText(Registry.this, R.string.errorDip, Toast.LENGTH_SHORT).show();
            return true;
        }
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(Registry.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Creazione di un nuovo utente riuscita
                            String user = auth.getCurrentUser().getUid();
                            studente = new Studente(user,matricola,email,nome,cognome,dipartimento,corso);
                            if(studente.registraUtente(db,Registry.this))
                                loadingDialog.dismissDialog();
                        } else {
                            // Creazione di un nuovo utente non riuscita, mostra un messaggio di errore.
                            Toast.makeText(Registry.this, task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                            loadingDialog.dismissDialog();
                        }
                    }
                });
        return false;
    }


    public void esci(View view) {
        finish();
    }
}