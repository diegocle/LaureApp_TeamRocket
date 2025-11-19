package it.uniba.dib.sms222312.utenti.studenti;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
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
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import org.checkerframework.common.subtyping.qual.Bottom;

import java.util.ArrayList;
import java.util.Arrays;

import it.uniba.dib.sms222312.R;
import it.uniba.dib.sms222312.modelli.RecyclerViewInterface;
import it.uniba.dib.sms222312.modelli.Tesi;
import it.uniba.dib.sms222312.modelli.TesiAdapter;

public class CercaTesiFragment extends Fragment implements RecyclerViewInterface {

    RecyclerView recyclerView;
    FirebaseFirestore database;
    TesiAdapter myAdapter;
    ArrayList<Tesi> list;
    private SearchView searchView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_cerca_tesi, container, false);

        //configuro appbarr e navigationbar
        ((HomeStudente) getActivity()).setToolbarTitle(getResources().getString(R.string.ricercaTesi));
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.clearFocus();

        Button indietro = view.findViewById(R.id.my_icon_button);
        indietro.setVisibility(View.GONE);

        searchView = view.findViewById(R.id.searchView);
        searchView.clearFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });

        recyclerView = view.findViewById(R.id.listaTesi);
        database = FirebaseFirestore.getInstance();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        list = new ArrayList<Tesi>();
        myAdapter = new TesiAdapter<Tesi>(getContext(),list,this);
        recyclerView.setAdapter(myAdapter);
        EventChangeListener();
        return view;
    }

    private void filterList(String text) {
        ArrayList<Tesi> filteredList = new ArrayList<>();

        for(Tesi tesi : list){
            if(tesi.getNome().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(tesi);
            }
        }

        if(filteredList.isEmpty()){
            Toast.makeText(getContext(), "No data found", Toast.LENGTH_SHORT).show();
        }else {
            myAdapter.setFilteredList(filteredList);
        }
    }

    private void EventChangeListener(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String user = auth.getCurrentUser().getUid();
        DocumentReference docRef = database.collection("utente").document(user);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String dipartimento = documentSnapshot.getString("dipartimento");
                database.collection("tesi").whereIn("dipartimento", Arrays.asList(dipartimento, "")).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot valua, @Nullable FirebaseFirestoreException error) {
                        if(error != null){
                            Log.e("Firestore error", error.getMessage());
                            return;
                        }

                        int i=0;
                        for(DocumentChange dc : valua.getDocumentChanges()){
                            if(dc.getType() == DocumentChange.Type.ADDED){
                                list.add(dc.getDocument().toObject(Tesi.class));
                                String s = valua.getDocuments().get(i).getId();
                                list.get(i).setIdTesi(s);
                                i++;
                            }
                            myAdapter.notifyDataSetChanged();
                        }


                    }
                });

            }
        });

    }


    @Override
    public void onItemClick(int position) {
        Tesi tesi =list.get(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable("tesi", tesi);
        VisualizzaTesiStudenteFragment vistaTask = new VisualizzaTesiStudenteFragment();
        vistaTask.setArguments(bundle);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.container, vistaTask)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onItemLongClick(int position) {
        //niente
    }
}