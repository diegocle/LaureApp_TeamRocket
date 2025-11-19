package it.uniba.dib.sms222312.utenti.ospite;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import it.uniba.dib.sms222312.R;
import it.uniba.dib.sms222312.modelli.RecyclerViewInterface;
import it.uniba.dib.sms222312.modelli.Tesi;
import it.uniba.dib.sms222312.modelli.TesiAdapter;

public class Ospite extends AppCompatActivity implements RecyclerViewInterface {
    RecyclerView recyclerView;
    FirebaseFirestore database;
    TesiAdapter myAdapter;
    ArrayList<Tesi> list;
    String corso = null;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_cerca_tesi);


        searchView = findViewById(R.id.searchView);
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

        recyclerView = findViewById(R.id.listaTesi);
        database = FirebaseFirestore.getInstance();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        list = new ArrayList<Tesi>();
        myAdapter = new TesiAdapter<Tesi>(this,list,this);
        recyclerView.setAdapter(myAdapter);
        EventChangeListener();

    }

    private void filterList(String text) {
        ArrayList<Tesi> filteredList = new ArrayList<>();

        for(Tesi tesi : list){
            if(tesi.getNome().toLowerCase().contains(text.toLowerCase())){
                filteredList.add(tesi);
            }
        }

        if(filteredList.isEmpty()){
            Toast.makeText(this, "No data found", Toast.LENGTH_SHORT).show();
        }else {
            myAdapter.setFilteredList(filteredList);
        }
    }

    private void EventChangeListener(){
        DocumentReference docRef = database.collection("utente").document();

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                corso = documentSnapshot.getString("corso");
                database.collection("tesi").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
        Intent intent = new Intent(this, VisualizzaTesiOspite.class);
        intent.putExtra("tesi", tesi);
        startActivity(intent);

    }

    @Override
    public void onItemLongClick(int position) {
        //niente
    }
    public void esci(View view) {
        finish();
    }
}
