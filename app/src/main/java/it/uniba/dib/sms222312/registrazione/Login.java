package it.uniba.dib.sms222312.registrazione;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.regex.Pattern;

import it.uniba.dib.sms222312.LoadingDialog;
import it.uniba.dib.sms222312.R;
import it.uniba.dib.sms222312.utenti.docenti.HomeDocente;
import it.uniba.dib.sms222312.utenti.studenti.HomeStudente;

public class Login extends AppCompatActivity {
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button btnLogin = findViewById(R.id.btn_login);



        final LoadingDialog loadingDialog = new LoadingDialog(Login.this);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText edtEmail = findViewById(R.id.edt_email);
                EditText edtPassword = findViewById(R.id.edt_password);
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();
                if (email.isEmpty() || password.isEmpty() ) {
                    Toast.makeText(getApplicationContext(), R.string.tutti_i_campi_sono_obbligatori, Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth = FirebaseAuth.getInstance();
                signIn(email,password,loadingDialog);
                loadingDialog.startLoadingDialog();
            }
        });

        findViewById(R.id.reimposta).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                EditText edtEmail = findViewById(R.id.edt_email);
                String email = edtEmail.getText().toString();
                if (email.isEmpty() || !isEmailValid(email)) {
                    Toast.makeText(getApplicationContext(), R.string.inserisci_email_valida, Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth = FirebaseAuth.getInstance();
                mAuth.useAppLanguage();

                String appName = "LaureApp";


                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), R.string.email_inviata, Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), R.string.errore_nel_reimpostare_la_password, Toast.LENGTH_SHORT).show();
                                Log.e(TAG, "Error sending password reset email", task.getException());
                            }
                        });




            }
        });


    }
    private void signIn(String email, String password, LoadingDialog loadingDialog) {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Controlla se l'utente è uno studente o un docente
                                    checkUserType();
                                } else {
                                    Toast.makeText(Login.this, R.string.Autenticazione_fallita,
                                            Toast.LENGTH_SHORT).show();
                                    loadingDialog.dismissDialog();
                                }
                            }
                        });
    }


    private void checkUserType() {
        FirebaseUser user = mAuth.getCurrentUser();
        String userId = user.getUid();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("utente").document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String role = documentSnapshot.getString("tipo");
                        if (role.equals("studente")) {
                            Toast.makeText(Login.this, R.string.authStudente,
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this, HomeStudente.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finishAffinity();

                        } else if (role.equals("docente")) {
                            Toast.makeText(Login.this, R.string.authDocente,
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Login.this, HomeDocente.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finishAffinity();
                        }

                    }
                });
    }

    public void esci(View view) {
        finish();
    }
    public boolean isEmailValid(String email) {
        // Definiamo una regex per la validazione dell'indirizzo email
        String emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

        // Creiamo un oggetto Pattern utilizzando la regex definita
        Pattern pattern = Pattern.compile(emailRegex);

        // Utilizziamo il metodo matcher per controllare la validità dell'indirizzo email
        return pattern.matcher(email).matches();
    }
}