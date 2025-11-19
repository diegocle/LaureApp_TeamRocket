package it.uniba.dib.sms222312.utenti.studenti;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import it.uniba.dib.sms222312.R;
import it.uniba.dib.sms222312.modelli.adapterTask.ListaTaskAdapter;
import it.uniba.dib.sms222312.modelli.adapterTask.ListaTaskAdapter1;
import it.uniba.dib.sms222312.modelli.TaskTesi;
import it.uniba.dib.sms222312.modelli.Tesi;
import it.uniba.dib.sms222312.modelli.Tesista;
import it.uniba.dib.sms222312.utenti.VisualizzaTaskFragment;

public class ListaTaskFragment extends Fragment implements ListaTaskAdapter.OnItemClickListener {

    FirebaseFirestore db;
    RecyclerView recyclerView;
    RecyclerView recyclerViewSvolti;
    ArrayList<TaskTesi> taskArrayList;
    ArrayList<TaskTesi> taskArrayListSvolti;
    ListaTaskAdapter myAdapter;
    ListaTaskAdapter1 myAdapterSvolti;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    String userId = fAuth.getCurrentUser().getUid();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //configuro appbarr e navigationbar
        ((HomeStudente) getActivity()).setToolbarTitle(getResources().getString(R.string.taskTesi));
        ((HomeStudente) getActivity()).setflag(false);
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.page_2);


        View view = inflater.inflate(R.layout.fragment_lista_task, container, false);
        visualizzaTask(view, userId);
        return view;
    }

    private void visualizzaTask(View view, String userId) {
        db = FirebaseFirestore.getInstance();

        taskArrayList = new ArrayList<TaskTesi>();
        taskArrayListSvolti = new ArrayList<TaskTesi>();

        recyclerView = view.findViewById(R.id.recyclerTask);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        myAdapter = new ListaTaskAdapter(taskArrayList, ListaTaskFragment.this);
        recyclerView.setAdapter(myAdapter);


        recyclerViewSvolti = view.findViewById(R.id.recyclerTaskSvolti);
        recyclerViewSvolti.setLayoutManager(new LinearLayoutManager(getActivity()));
        myAdapterSvolti = new ListaTaskAdapter1(taskArrayListSvolti, ListaTaskFragment.this);
        recyclerViewSvolti.setAdapter(myAdapterSvolti);

        Query query = db.collection("tesisti").whereEqualTo("studente", userId);


        caricaDatitesi(view, query);


        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    // Ottieni il primo documento corrispondente
                    DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                    // Ottieni l'ID del documento
                    String tesista = documentSnapshot.getId();

                    EventChangeListener(tesista);

                } else {
                    // Nessun documento corrispondente trovato
                }

            }

        });
    }

    private void caricaDatitesi(View view, Query query) {
        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                               if (task.isSuccessful()) {
                                                   for (QueryDocumentSnapshot document : task.getResult()) {
                                                       String idTesi = document.toObject(Tesista.class).getTesi();
                                                       DocumentReference docRef = db.collection("tesi").document(idTesi);
                                                       docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                           @Override
                                                           public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                               if (task.isSuccessful()) {
                                                                   DocumentSnapshot document = task.getResult();
                                                                   if (document.exists()) {
                                                                       Tesi tesi = document.toObject(Tesi.class);
                                                                       TextView t1 = view.findViewById(R.id.nome);
                                                                       t1.setText(tesi.getNome());
                                                                       t1 = view.findViewById(R.id.durata);
                                                                       t1.setText(tesi.getDurata() + " ore");
                                                                       t1 = view.findViewById(R.id.descrizione);
                                                                       t1.setText(tesi.getDescrizione());
                                                                       Log.d("TAG", tesi.toString());
                                                                   } else {
                                                                       // il documento non esiste
                                                                       Log.d("TAG", "No such document");
                                                                   }
                                                               } else {
                                                                   // errore nel recupero del documento
                                                                   Log.d("TAG", "get failed with ", task.getException());
                                                               }
                                                           }
                                                       });
                                                   }
                                               } else {

                                               }
                                           }
                                       });
    }

    private void EventChangeListener(String tesista) {
        db.collection("task").whereEqualTo("tesista",tesista).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Log.e("Firestore error", error.getMessage());
                    return;
                }
                for(DocumentChange dc : value.getDocumentChanges()){
                    if(dc.getType() == DocumentChange.Type.MODIFIED || dc.getType() == DocumentChange.Type.ADDED){
                        TaskTesi tt = dc.getDocument().toObject(TaskTesi.class);
                        Log.d("",tt.toString());
                        if(tt.getStato().equals("Completato")){
                            taskArrayListSvolti.add(tt);
                            myAdapterSvolti.notifyDataSetChanged();
                        }else {
                            taskArrayList.add(tt);
                            myAdapter.notifyDataSetChanged();
                        }

                    }



                }
            }
        });

    }

    @Override
    public void onItemClick(int pos, ArrayList<TaskTesi> taskArrayList) {

        TaskTesi task =taskArrayList.get(pos);
        Bundle bundle = new Bundle();
        bundle.putSerializable("task", task);
        ((HomeStudente) getActivity()).setToolbarTitle(getString(R.string.visualizzaTask));
        VisualizzaTaskFragment vistaTask = new VisualizzaTaskFragment();
        vistaTask.setArguments(bundle);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.container, vistaTask)
                .addToBackStack(null)
                .commit();

    }
}