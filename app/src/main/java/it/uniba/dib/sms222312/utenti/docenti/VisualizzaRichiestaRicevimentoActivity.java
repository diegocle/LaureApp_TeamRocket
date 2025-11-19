package it.uniba.dib.sms222312.utenti.docenti;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import it.uniba.dib.sms222312.R;
import it.uniba.dib.sms222312.modelli.Ricevimento;
import it.uniba.dib.sms222312.modelli.StatoRicevimento;

public class VisualizzaRichiestaRicevimentoActivity extends DialogFragment {
    private FirebaseFirestore db;
    private AlertDialog alertDialog;

    private Context mContext;
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_visualizza_richiesta_ricevimento, null);


        Bundle args = getArguments();

        String tesista = args.getString("Tesista");
        String taskT = args.getString("TaskTesi");
        String data = args.getString("Data");
        String dettagli = args.getString("Dettagli");

        TextView txtTask = view.findViewById(R.id.taskTesi);
        TextView txtDettagli = view.findViewById(R.id.dettagli);
        TextView txtData = view.findViewById(R.id.data);
        Button btnAccetta = view.findViewById(R.id.accetta);
        Button btnRifiuta = view.findViewById(R.id.rifiuta);

        txtTask.setText(taskT);
        txtDettagli.setText(dettagli);
        txtData.setText(data);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        btnAccetta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection("ricevimenti")
                        .whereEqualTo("tesista", tesista).
                        whereEqualTo("task", taskT)
                        .whereEqualTo("data", data)
                        .whereEqualTo("dettagli", dettagli)
                         .get()
                         .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                             @Override
                             public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                 if (task.isSuccessful()) {
                                     QuerySnapshot querySnapshot = task.getResult();
                                     if (querySnapshot != null && !querySnapshot.isEmpty()) {
                                         DocumentSnapshot document = querySnapshot.getDocuments().get(0);
                                         String documentId = document.getId();
                                         db.collection("ricevimenti").document(documentId)
                                                 .update(
                                                         "tesista", tesista,
                                                         "task" , taskT,
                                                         "data", data,
                                                         "dettagli", dettagli,
                                                         "stato", StatoRicevimento.Approvata
                                                 ).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                     @Override
                                                     public void onSuccess(Void aVoid) {
                                                         // L'aggiornamento è stato eseguito con successo
                                                         Toast.makeText(mContext,"Richiesta accettata", Toast.LENGTH_SHORT).show();
                                                         alertDialog.dismiss();                                                     }
                                                 });

                                     } else {
                                         // Nessun documento trovato che soddisfa i criteri
                                     }
                                 } else {
                                     // Errore durante l'esecuzione della query
                                 }
                             }
                         });
            }
        });

        btnRifiuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Query query = db.collection("ricevimenti").whereEqualTo("tesista", tesista).whereEqualTo("task", taskT).whereEqualTo("data", data).whereEqualTo("dettagli", dettagli);
                query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Ottieni il primo documento corrispondente
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                            // Ottieni l'ID del documento
                            String documentId = documentSnapshot.getId();

                            // Usa l'ID del documento per fare altre operazioni su di esso
                            db.collection("ricevimenti").document(documentId)
                                    .delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(mContext,"Richiesta rifiutata", Toast.LENGTH_SHORT).show();
                                            alertDialog.dismiss();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(mContext, R.string.impossibile_eliminare_richiesta, Toast.LENGTH_SHORT).show();
                                            alertDialog.dismiss();

                                        }
                                    });
                        } else {
                            // Nessun documento corrispondente trovato
                        }

                    }
                });

            }
        });
        builder.setView(view);
        alertDialog = builder.create();
        return alertDialog;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

}
