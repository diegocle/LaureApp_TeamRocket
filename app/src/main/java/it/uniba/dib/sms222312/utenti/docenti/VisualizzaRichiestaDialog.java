package it.uniba.dib.sms222312.utenti.docenti;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms222312.R;
import it.uniba.dib.sms222312.modelli.Docente;
import it.uniba.dib.sms222312.modelli.Tesista;

public class VisualizzaRichiestaDialog extends DialogFragment {



    private FirebaseFirestore db;
    private AlertDialog alertDialog;

    private Context mContext;
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_visualizza_richiesta, null);


        Bundle args = getArguments();


        String tesi = args.getString("Tesi");
        String studente = args.getString("Studente");
        String docente = args.getString("Docente");
        String descrizione = args.getString("Descrizione");

        TextView txtTesi = view.findViewById(R.id.tesi);
        TextView txtDescrizione = view.findViewById(R.id.descrizione);
        TextView txtstudente = view.findViewById(R.id.studente);
        AutoCompleteTextView spinnerCorelatori = view.findViewById(R.id.spinner_corelatore);
        Button btnInvia = view.findViewById(R.id.accetta);
        Button btnRifiuta = view.findViewById(R.id.rifiuta);

        FirebaseAuth mAuth=FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser().getUid().equals(studente)){
            btnInvia.setVisibility(View.GONE);
            btnRifiuta.setVisibility(View.GONE);
            view.findViewById(R.id.spinner).setVisibility(View.GONE);
            TextView text = view.findViewById(R.id.stato);
            text.setText("In approvazione....");
        }

        txtDescrizione.setText(descrizione);
        db = FirebaseFirestore.getInstance();
        db.collection("utente").document(studente).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String studente = documentSnapshot.getString("nome");
                studente+=" "+documentSnapshot.getString("cognome");
                txtstudente.setText(studente);

            }
        });


        db.collection("tesi").document(tesi).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String nomeTesi = documentSnapshot.getString("nome");
                txtTesi.setText(nomeTesi);

            }
        });

        List<Docente> docenti = new ArrayList<>();

        db.collection("utente").whereNotEqualTo(FieldPath.documentId(), docente).whereEqualTo("tipo", "docente")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Recupera tutti i documenti dalla query
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            // Per ogni documento, recupera il nome del corso e lo aggiunge alla lista
                            for (DocumentSnapshot document : documents) {
                                String id = document.getId();
                                String name = document.getString("nome");
                                String surname = document.getString("cognome");

                                Docente person = new Docente(id, name, surname);
                                docenti.add(person);
                            }
                            // Popola lo Spinner con la lista di corsi
                            ArrayAdapter<Docente> spinnerAdapter = new ArrayAdapter<Docente>(mContext, android.R.layout.simple_spinner_item, docenti) {
                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    View view = super.getView(position, convertView, parent);
                                    TextView textView = (TextView) view.findViewById(android.R.id.text1);
                                    textView.setText(docenti.get(position).getNome() + " " + docenti.get(position).getCognome());
                                    return view;
                                }
                            };
                            spinnerCorelatori.setAdapter(spinnerAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        btnInvia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String selectedText = spinnerCorelatori.getText().toString();
                Docente selectedObject = null;
                for (Docente obj : docenti) {
                    if (obj.toString().equals(selectedText)) {
                        selectedObject = obj;
                        break;
                    }
                }
                String selectedPersonId = null;
                if (selectedObject != null) {
                    selectedPersonId = selectedObject.getId();
                }


                Tesista tesista = new Tesista(studente, tesi, docente, selectedPersonId);
                db.collection("tesisti").document().set(tesista).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(mContext,"Richiesta accettata", Toast.LENGTH_SHORT).show();
                                Query query = db.collection("richiestatesi").whereEqualTo("descrizione", descrizione).whereEqualTo("docente", docente).whereEqualTo("studente", studente).whereEqualTo("tesi", tesi);
                                query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if (!queryDocumentSnapshots.isEmpty()) {
                                            // Ottieni il primo documento corrispondente
                                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                                            // Ottieni l'ID del documento
                                            String documentId = documentSnapshot.getId();

                                            // Usa l'ID del documento per fare altre operazioni su di esso
                                            db.collection("richiestatesi").document(documentId)
                                                    .delete()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {
                                                            Log.d("msg", "DocumentSnapshot successfully deleted!");

                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {
                                                            Log.w("msg", "Error deleting document", e);
                                                        }
                                                    });
                                        } else {
                                            // Nessun documento corrispondente trovato
                                        }

                                    }
                                });
                                    alertDialog.dismiss();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(mContext,"Impossibile accettare richiesta", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

        btnRifiuta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Query query = db.collection("richiestatesi").whereEqualTo("descrizione", descrizione).whereEqualTo("docente", docente).whereEqualTo("studente", studente).whereEqualTo("tesi", tesi);
                query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Ottieni il primo documento corrispondente
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                            // Ottieni l'ID del documento
                            String documentId = documentSnapshot.getId();

                            // Usa l'ID del documento per fare altre operazioni su di esso
                            db.collection("richiestatesi").document(documentId)
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
                                            Toast.makeText(mContext,"Impossibile eliminare richiesta", Toast.LENGTH_SHORT).show();
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


