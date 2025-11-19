package it.uniba.dib.sms222312.utenti.docenti;

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

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import it.uniba.dib.sms222312.R;
import it.uniba.dib.sms222312.modelli.ListaRichiesteInterface;
import it.uniba.dib.sms222312.modelli.RicevimentiAdapter;
import it.uniba.dib.sms222312.modelli.Ricevimento;
import it.uniba.dib.sms222312.modelli.StatoRicevimento;

public class VisualizzaRicevimentiTesistaFragment extends Fragment implements ListaRichiesteInterface {

    RecyclerView recyclerView;
    ArrayList<Ricevimento> ricevimentoArrayList;
    RicevimentiAdapter myAdapter;
    FirebaseFirestore db;
    private String tesista;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_visualizza_ricevimenti_tesista, container, false);

        //configuro appbarr e navigationbar
        ((HomeDocente) getActivity()).setToolbarTitle(getString(R.string.ricevimenti));
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.clearFocus();

        Bundle bundle = getArguments();
        tesista = bundle.getString("tesista");

        recyclerView = view.findViewById(R.id.recyclerRicevimenti);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        ricevimentoArrayList = new ArrayList<Ricevimento>();
        myAdapter = new RicevimentiAdapter(getContext(), ricevimentoArrayList, this);
        recyclerView.setAdapter(myAdapter);

        EventChangeListener();
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
                        Ricevimento ric = dc.getDocument().toObject(Ricevimento.class);
                        if(ric.getStato() != StatoRicevimento.Inviata)
                        ricevimentoArrayList.add(ric);
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