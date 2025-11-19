package it.uniba.dib.sms222312;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import it.uniba.dib.sms222312.registrazione.Login;
import it.uniba.dib.sms222312.registrazione.Registry;
import it.uniba.dib.sms222312.registrazione.RegistryDocente;
import it.uniba.dib.sms222312.utenti.ospite.Ospite;
import it.uniba.dib.sms222312.utenti.docenti.HomeDocente;
import it.uniba.dib.sms222312.utenti.studenti.HomeStudente;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new GestioneTema(this).setThema();
        setContentView(R.layout.activity_main);
        Button login = findViewById(R.id.home_login);
        Button registra = findViewById(R.id.home_registra);
        Button registraDoc = findViewById(R.id.home_registraDoc);
        View ospite = findViewById(R.id.ospite);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth != null) {
            String userId = mAuth.getUid();
           if(userId != null) {
               final LoadingDialog loadingDialog = new LoadingDialog(this);
               loadingDialog.startLoadingDialog();
               checkUserType(mAuth,userId);
           }
        }

        ospite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Ospite.class));
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        });

        registra.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, Registry.class));
            }
        });

        registraDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegistryDocente.class));
            }
        });
    }

    private void checkUserType(FirebaseAuth mAuth, String userId) {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("utente").document(userId)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String role = documentSnapshot.getString("tipo");
                        if (role.equals("studente")) {
                            Toast.makeText(MainActivity.this, R.string.authStudente,
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, HomeStudente.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finishAffinity();

                        } else if (role.equals("docente")) {
                            Toast.makeText(MainActivity.this, R.string.authDocente,
                                    Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(MainActivity.this, HomeDocente.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finishAffinity();
                        }

                    }
                });
    }
}