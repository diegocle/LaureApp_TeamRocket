package it.uniba.dib.sms222312.utenti.docenti;

import static android.content.ContentValues.TAG;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import it.uniba.dib.sms222312.R;

public class ModificaProfiloDocenteFragment extends DialogFragment {

    private FirebaseFirestore db;
    private AlertDialog alertDialog;

    private Context mContext;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_mod_profilo, null);


        Bundle args = getArguments();

        String nome = args.getString("Nome");
        String email = args.getString("email");
        String cognome = args.getString("cognome");


        EditText editNome = view.findViewById(R.id.edt_name);
        EditText editCognome = view.findViewById(R.id.edt_surname);
        EditText editEmail = view.findViewById(R.id.email);
        view.findViewById(R.id.matricolaS).setVisibility(View.GONE);


        editNome.setText(nome);
        editCognome.setText(cognome);
        editEmail.setText(email);


        db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String docente = auth.getCurrentUser().getUid();
        view.findViewById(R.id.btn_modPass)
                .setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            auth.useAppLanguage();
                                            auth.sendPasswordResetEmail(email)
                                                    .addOnCompleteListener(task -> {
                                                        if (task.isSuccessful()) {
                                                            Toast.makeText(getContext(), R.string.email_inviata, Toast.LENGTH_SHORT).show();
                                                        } else {
                                                            Toast.makeText(getContext(), R.string.errore_nel_reimpostare_la_password, Toast.LENGTH_SHORT).show();
                                                            Log.e(TAG, "Error sending password reset email", task.getException());
                                                        }
                                                    });
                                        }
                                    }
                );

        builder.setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        DocumentReference docRef = db.collection("utente").document(docente);
                        String newNome = editNome.getText().toString();
                        String newCognome = editCognome.getText().toString();
                        String newEmail = editEmail.getText().toString();

                        auth.getCurrentUser().updateEmail(email);
                        docRef.update("nome", newNome, "cognome", newCognome, "e-mail", newEmail)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(mContext, "Profilo modificato con successo", Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(mContext, "Impossibile modificare profilo", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }})
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ModificaProfiloDocenteFragment.this.getDialog().cancel();
                    }
                });
        alertDialog = builder.create();
        return alertDialog;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
