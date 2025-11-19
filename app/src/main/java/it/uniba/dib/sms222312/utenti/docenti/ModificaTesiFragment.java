package it.uniba.dib.sms222312.utenti.docenti;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import it.uniba.dib.sms222312.R;

public class ModificaTesiFragment extends DialogFragment {

    private FirebaseFirestore db;
    private AlertDialog alertDialog;

    private Context mContext;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_modifica_tesi, null);


        Bundle args = getArguments();

        String nome = args.getString("Nome");
        String corso = args.getString("Corso");
        String descrizione = args.getString("Descrizione");
        String media = args.getString("Media");
        String durata = args.getString("Durata");
        String idTesi = args.getString("Id");


        EditText editNome = view.findViewById(R.id.nome);
        EditText editDurata = view.findViewById(R.id.durata);
        EditText editMedia = view.findViewById(R.id.media);
        EditText editDescrizione = view.findViewById(R.id.descrizione);


        editNome.setText(nome);
        editDurata.setText(durata);
        editMedia.setText(media);
        editDescrizione.setText(descrizione);

        db = FirebaseFirestore.getInstance();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String docente = auth.getCurrentUser().getUid();


        builder.setView(view)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                DocumentReference docRef = db.collection("tesi").document(idTesi);
                String newNome = editNome.getText().toString();
                String newDurata = editDurata.getText().toString();
                String newMedia = editMedia.getText().toString();
                String newDescrizione = editDescrizione.getText().toString();

                docRef.update("nome", newNome, "durata", newDurata, "media", newMedia, "descrizione", newDescrizione)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(mContext, "Tesi modificata con successo", Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(mContext, "Impossibile modificare la tesi", Toast.LENGTH_SHORT).show();
                            }
                        });
            }})
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ModificaTesiFragment.this.getDialog().cancel();
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