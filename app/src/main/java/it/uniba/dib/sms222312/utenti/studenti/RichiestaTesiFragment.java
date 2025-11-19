package it.uniba.dib.sms222312.utenti.studenti;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
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
import it.uniba.dib.sms222312.SchermataCaricamento;
import it.uniba.dib.sms222312.modelli.ListaRichiesteInterface;
import it.uniba.dib.sms222312.modelli.RichiestaTesi;
import it.uniba.dib.sms222312.modelli.RichiestaTesiAdapter;
import it.uniba.dib.sms222312.utenti.docenti.HomeDocente;
import it.uniba.dib.sms222312.utenti.docenti.VisualizzaRichiestaDialog;

public class RichiestaTesiFragment extends Fragment implements ListaRichiesteInterface {

    RecyclerView recyclerView;
    ArrayList<RichiestaTesi> richiestaTesiArrayList;
    RichiestaTesiAdapter myAdapter;
    FirebaseFirestore db;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_lista_richieste, container, false);

        //configuro appbarr e navigationbar
        ((HomeStudente) getActivity()).setToolbarTitle(getString(R.string.richieste_tesi));
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.clearFocus();


        recyclerView = view.findViewById(R.id.recyclerRichieste);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        richiestaTesiArrayList = new ArrayList<RichiestaTesi>();
        myAdapter = new RichiestaTesiAdapter(getContext(), richiestaTesiArrayList, this);
        recyclerView.setAdapter(myAdapter);
        EventChangeListener();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        return view;
    }

    String deletedRichiesta = null;

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            int position = viewHolder.getAdapterPosition();

            switch (direction){
                case ItemTouchHelper.LEFT:
                case ItemTouchHelper.RIGHT:
                    new SchermataCaricamento().conferma(getContext(), getString(R.string.titoloConferma), getString(R.string.msgConferma))
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    Query query = db.collection("richiestatesi").whereEqualTo("descrizione", richiestaTesiArrayList.get(position).getDescrizione()).whereEqualTo("docente", richiestaTesiArrayList.get(position).getDocente()).whereEqualTo("studente", richiestaTesiArrayList.get(position).getStudente()).whereEqualTo("tesi", richiestaTesiArrayList.get(position).getTesi());
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
                                    richiestaTesiArrayList.remove(position);
                                    myAdapter.notifyItemRemoved(position);
                                }}).create().show();
                    break;
            }
        }
    };

    private void EventChangeListener() {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String user = auth.getCurrentUser().getUid();
        db.collection("richiestatesi").whereEqualTo("studente", user).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Log.e("Firestore error", error.getMessage());
                    return;
                }

                for(DocumentChange dc : value.getDocumentChanges()){
                    if(dc.getType() == DocumentChange.Type.ADDED){
                        richiestaTesiArrayList.add(dc.getDocument().toObject(RichiestaTesi.class));
                    }
                    myAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public void onItemClick(int position) {
        VisualizzaRichiestaDialog dialog = new VisualizzaRichiestaDialog();
        Bundle bundle = new Bundle();
        bundle.putString("Tesi", richiestaTesiArrayList.get(position).getTesi());
        bundle.putString("Studente", richiestaTesiArrayList.get(position).getStudente());
        bundle.putString("Docente", richiestaTesiArrayList.get(position).getDocente());
        bundle.putString("Descrizione", richiestaTesiArrayList.get(position).getDescrizione());
        dialog.setArguments(bundle);
        dialog.show(getActivity().getSupportFragmentManager(), "VisualizzaRichiestaFragment");
        richiestaTesiArrayList.remove(position);
        myAdapter.notifyDataSetChanged();

    }

}
