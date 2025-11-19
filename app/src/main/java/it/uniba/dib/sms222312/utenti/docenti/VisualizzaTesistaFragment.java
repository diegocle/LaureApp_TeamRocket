package it.uniba.dib.sms222312.utenti.docenti;

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
import android.widget.Button;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import it.uniba.dib.sms222312.R;
import it.uniba.dib.sms222312.modelli.adapterTask.ListaTaskAdapter;
import it.uniba.dib.sms222312.modelli.TaskTesi;
import it.uniba.dib.sms222312.utenti.VisualizzaTaskFragment;

public class VisualizzaTesistaFragment extends Fragment implements ListaTaskAdapter.OnItemClickListener {

    RecyclerView recyclerView;
    ArrayList<TaskTesi> taskArrayList;
    ListaTaskAdapter myAdapter;
    FirebaseFirestore db;
    String tesista;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((HomeDocente) getActivity()).setToolbarTitle(getString(R.string.visualizzaTask));


        View view = inflater.inflate(R.layout.activity_visualizza_tesista, container, false);

        Bundle bundle = getArguments();
        tesista = bundle.getString("Tesista");

        recyclerView = view.findViewById(R.id.listaTask);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        taskArrayList = new ArrayList<TaskTesi>();
        myAdapter = new ListaTaskAdapter( taskArrayList, this);
        recyclerView.setAdapter(myAdapter);

        EventChangeListener();

        Button btnTask = view.findViewById(R.id.btnTask);

        btnTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle bundle = new Bundle();
                bundle.putString("Tesista", tesista);
                AggiungiTaskActivity vistaTask = new AggiungiTaskActivity();
                vistaTask.setArguments(bundle);
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.container, vistaTask)
                        .addToBackStack(null)
                        .commit();

            }
        });
        return view;
    }

    private void EventChangeListener() {

        db.collection("task").whereEqualTo("tesista",tesista).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Log.e("Firestore error", error.getMessage());
                    return;
                }
                for(DocumentChange dc : value.getDocumentChanges()){
                    if(dc.getType() == DocumentChange.Type.ADDED){
                        taskArrayList.add(dc.getDocument().toObject(TaskTesi.class));
                    }
                    myAdapter.notifyDataSetChanged();
                }
            }
        });

    }

    @Override
    public void onItemClick(int pos, ArrayList<TaskTesi> taskArrayList) {

        TaskTesi task =taskArrayList.get(pos);
        Bundle bundle = new Bundle();
        bundle.putSerializable("task", task);
        ((HomeDocente) getActivity()).setToolbarTitle(getString(R.string.visualizzaTask));
        VisualizzaTaskFragment vistaTask = new VisualizzaTaskFragment();
        vistaTask.setArguments(bundle);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.container, vistaTask)
                .addToBackStack(null)
                .commit();

    }
}