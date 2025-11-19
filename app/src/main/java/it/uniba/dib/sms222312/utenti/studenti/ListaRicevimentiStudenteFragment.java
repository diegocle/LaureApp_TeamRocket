package it.uniba.dib.sms222312.utenti.studenti;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
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
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import it.uniba.dib.sms222312.R;
import it.uniba.dib.sms222312.modelli.ListaRichiesteInterface;
import it.uniba.dib.sms222312.modelli.RicevimentiAdapter;
import it.uniba.dib.sms222312.modelli.Ricevimento;

public class ListaRicevimentiStudenteFragment extends Fragment implements ListaRichiesteInterface {

    FirebaseFirestore db;
    String tesista;
    RecyclerView recyclerView;
    ArrayList<Ricevimento> ricevimentoArrayList;
    RicevimentiAdapter myAdapter;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_lista_ricevimenti, container, false);

        //configuro appbarr e navigationbar
        ((HomeStudente) getActivity()).setToolbarTitle(getResources().getString(R.string.ricevimenti));
        ((HomeStudente) getActivity()).setflag(false);
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.page_3);


        Button btnRicevimento = view.findViewById(R.id.btnRicevimento);

        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        String userId = fAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        Query query = db.collection("tesisti").whereEqualTo("studente", userId);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    // Ottieni il primo documento corrispondente
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                    // Ottieni l'ID del documento
                    tesista = documentSnapshot.getId();

                    recyclerView = view.findViewById(R.id.listaRicevimenti);
                    recyclerView.setHasFixedSize(true);
                    recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

                    db = FirebaseFirestore.getInstance();
                    ricevimentoArrayList = new ArrayList<Ricevimento>();
                    myAdapter = new RicevimentiAdapter(getContext(), ricevimentoArrayList, ListaRicevimentiStudenteFragment.this);
                    recyclerView.setAdapter(myAdapter);

                    EventChangeListener();

                    btnRicevimento.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            RichiestaRicevimentoFragment dialog = new RichiestaRicevimentoFragment();
                            Bundle bundle = new Bundle();
                            bundle.putString("Tesista", tesista);
                            dialog.setArguments(bundle);
                            dialog.show(getActivity().getSupportFragmentManager(), "RichiestaRicevimentoFragment");

                        }
                    });
                } else {
                    btnRicevimento.setVisibility(View.GONE);
                    view.findViewById(R.id.nessuna).setVisibility(View.VISIBLE);
                }

            }
        });
        return view;
    }

    private void EventChangeListener() {
        db.collection("ricevimenti").whereEqualTo("tesista", tesista).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Log.e("Firestore error", error.getMessage());
                    return;
                }

                for(DocumentChange dc : value.getDocumentChanges()){
                    if(dc.getType() == DocumentChange.Type.ADDED){
                        ricevimentoArrayList.add(dc.getDocument().toObject(Ricevimento.class));
                    }
                    myAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onItemClick(int position) {

    }
}