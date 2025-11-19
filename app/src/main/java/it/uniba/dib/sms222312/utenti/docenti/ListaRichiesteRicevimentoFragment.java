package it.uniba.dib.sms222312.utenti.docenti;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
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
import java.util.List;

import it.uniba.dib.sms222312.R;
import it.uniba.dib.sms222312.modelli.ListaRichiesteInterface;
import it.uniba.dib.sms222312.modelli.Ricevimento;
import it.uniba.dib.sms222312.modelli.StatoRicevimento;
import it.uniba.dib.sms222312.modelli.adapterRicevimenti.RichiesteRicevimentoAdapter;

public class ListaRichiesteRicevimentoFragment extends Fragment implements ListaRichiesteInterface {

    private List<String> tesisti;
    FirebaseFirestore db;
    RecyclerView recyclerView;
    ArrayList<Ricevimento> ricevimentoArrayList;
    RichiesteRicevimentoAdapter myAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_lista_richieste_ricevimento, container, false);

        super.onCreate(savedInstanceState);

        //configuro appbarr e navigationbar
        ((HomeDocente) getActivity()).setToolbarTitle(getString(R.string.richiesta_ric));
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.clearFocus();

        Bundle bundle = getArguments();
        tesisti = bundle.getStringArrayList("tesi");

        recyclerView = view.findViewById(R.id.recyclerRichieste);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        ricevimentoArrayList = new ArrayList<Ricevimento>();
        myAdapter = new RichiesteRicevimentoAdapter(getContext(), ricevimentoArrayList, this);
        recyclerView.setAdapter(myAdapter);

        EventChangeListener();

        return view;

    }

    private void EventChangeListener() {

        db.collection("ricevimenti").whereIn("tesista", tesisti).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Log.e("Firestore error", error.getMessage());
                    return;
                }

                for(DocumentChange dc : value.getDocumentChanges()){
                    if(dc.getType() == DocumentChange.Type.ADDED){
                        Ricevimento ric = dc.getDocument().toObject(Ricevimento.class);
                        if(ric.getStato() == StatoRicevimento.Inviata)
                            ricevimentoArrayList.add(ric);
                    }
                    myAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        VisualizzaRichiestaRicevimentoActivity dialog = new VisualizzaRichiestaRicevimentoActivity();
        Bundle bundle = new Bundle();
        bundle.putString("Tesista", ricevimentoArrayList.get(position).getTesista());
        bundle.putString("TaskTesi", ricevimentoArrayList.get(position).getTask());
        bundle.putString("Data", ricevimentoArrayList.get(position).getData());
        bundle.putString("Dettagli", ricevimentoArrayList.get(position).getDettagli());
        dialog.setArguments(bundle);
        dialog.show(getActivity().getSupportFragmentManager(), "VisualizzaRichiestaRicevimentoActivity");
        ricevimentoArrayList.remove(position);
        myAdapter.notifyDataSetChanged();
    }

    /*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            // Codice per "restartare" l'activity
            Log.d("qualcosa","si");
            ricevimentoArrayList.remove(requestCode);
            myAdapter.notifyDataSetChanged();
        }
    }*/
}