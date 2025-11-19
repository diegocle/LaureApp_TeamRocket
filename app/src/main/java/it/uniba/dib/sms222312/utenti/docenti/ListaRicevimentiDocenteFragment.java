package it.uniba.dib.sms222312.utenti.docenti;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms222312.R;
import it.uniba.dib.sms222312.modelli.ListaRichiesteInterface;
import it.uniba.dib.sms222312.modelli.Tesi;
import it.uniba.dib.sms222312.modelli.Tesista;
import it.uniba.dib.sms222312.modelli.TesistiAdapter;

public class ListaRicevimentiDocenteFragment extends Fragment implements ListaRichiesteInterface {

    FirebaseFirestore db;
    RecyclerView recyclerView;
    ArrayList<Tesista> tesistaArrayList;
    TesistiAdapter myAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_lista_ricevimenti_docente, container, false);

        //configuro appbarr e navigationbar
        ((HomeDocente) getActivity()).setToolbarTitle(getString(R.string.ricevimenti));
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.clearFocus();

        recyclerView = view.findViewById(R.id.listaRicevimenti);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        tesistaArrayList = new ArrayList<Tesista>();
        myAdapter = new TesistiAdapter(getContext(), tesistaArrayList, this);
        recyclerView.setAdapter(myAdapter);

        EventChangeListener();

        Button btnRicevimento = view.findViewById(R.id.btnRichieste);

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        String userId = fAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        List<String> documentIds = new ArrayList<>();
        Query query = db.collection("tesisti").whereEqualTo("relatore", userId);
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        String documentId = document.getId();
                        documentIds.add(documentId);
                    }

                    btnRicevimento.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            Bundle bundle = new Bundle();
                            bundle.putStringArrayList("tesi", (ArrayList<String>) documentIds);
                            ListaRichiesteRicevimentoFragment vistaTask = new ListaRichiesteRicevimentoFragment();
                            vistaTask.setArguments(bundle);
                            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                            transaction.replace(R.id.container, vistaTask)
                                    .addToBackStack(null)
                                    .commit();



                        }
                    });
                } else {
                    // Nessun documento corrispondente trovato
                }

            }
        });
        return view;
    }

    private void EventChangeListener() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String user = auth.getCurrentUser().getUid();


        db.collection("tesisti").whereEqualTo("relatore", user).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Log.e("Firestore error", error.getMessage());
                    return;
                }
                for(DocumentChange dc : value.getDocumentChanges()){
                    if(dc.getType() == DocumentChange.Type.ADDED){
                        tesistaArrayList.add(dc.getDocument().toObject(Tesista.class));
                    }
                    myAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        Query query = db.collection("tesisti").whereEqualTo("corelatore", tesistaArrayList.get(position).getCorelatore()).whereEqualTo("relatore", tesistaArrayList.get(position).getRelatore()).whereEqualTo("studente", tesistaArrayList.get(position).getStudente()).whereEqualTo("tesi", tesistaArrayList.get(position).getTesi());
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    // Ottieni il primo documento corrispondente
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                    // Ottieni l'ID del documento
                    String documentId = documentSnapshot.getId();
                    ;
                    Bundle bundle = new Bundle();
                    bundle.putString("tesista", documentId);
                    VisualizzaRicevimentiTesistaFragment vistaTask = new VisualizzaRicevimentiTesistaFragment();
                    vistaTask.setArguments(bundle);
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, vistaTask)
                            .addToBackStack(null)
                            .commit();
                } else {
                    // Nessun documento corrispondente trovato
                }

            }
        });

    }

}