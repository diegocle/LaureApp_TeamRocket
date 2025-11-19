package it.uniba.dib.sms222312.utenti;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class UpdateSpinner {
    private AutoCompleteTextView spinnerD;
    private AutoCompleteTextView spinnerC;

    public UpdateSpinner(AutoCompleteTextView spinnerD, AutoCompleteTextView spinnerC) {
        this.spinnerD = spinnerD;
        this.spinnerC = spinnerC;
    }

     public void updateSpinnerD(FirebaseFirestore db) {
        db = FirebaseFirestore.getInstance();
        db.collection("corsi")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Recupera tutti i documenti dalla query
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            // Crea una lista di stringhe per contenere i nomi dei corsi
                            Set<String> uniqueValues = new LinkedHashSet<>();
                            // Per ogni documento, recupera il nome del corso e lo aggiunge alla lista
                            for (DocumentSnapshot document : documents) {
                                uniqueValues.add(document.getString("Dipartimento"));
                            }
                            // Popola lo Spinner con la lista di corsi
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(spinnerD.getContext(), android.R.layout.simple_spinner_item, new ArrayList<>(uniqueValues));
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerD.setAdapter(adapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    public void updateSpinnerC(AdapterView<?> parent, int position,FirebaseFirestore db) {
        // Recupero l'elemento selezionato
        String selectedItem = parent.getItemAtPosition(position).toString();
        Set<String> uniqueValues = new LinkedHashSet<>();

        db.collection("corsi")
                .whereEqualTo("Dipartimento", selectedItem)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> documents = queryDocumentSnapshots.getDocuments();
                        // Per ogni documento, recupera il nome del corso e lo aggiunge alla lista
                        for (DocumentSnapshot document : documents) {
                            uniqueValues.add(document.getString("Corso"));
                        }
                        // Popola lo Spinner con la lista di corsi
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(spinnerC.getContext(), android.R.layout.simple_spinner_item, new ArrayList<>(uniqueValues));
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerC.setAdapter(adapter);


                    }
                });
    }
}
