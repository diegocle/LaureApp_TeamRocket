package it.uniba.dib.sms222312.utenti.docenti;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import it.uniba.dib.sms222312.R;
import it.uniba.dib.sms222312.SchermataCaricamento;
import it.uniba.dib.sms222312.modelli.RecyclerViewInterface;
import it.uniba.dib.sms222312.modelli.Tesi;
import it.uniba.dib.sms222312.modelli.TesiAdapter;


public class ListaTesiFragment extends Fragment implements RecyclerViewInterface {

    RecyclerView recyclerView;
    FirebaseFirestore database;
    TesiAdapter myAdapter;
    ArrayList<Tesi> list;
    RegistraTesiFragment registraTesiFragment = new RegistraTesiFragment();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_lista_tesi, container, false);

        //configuro appbarr e navigationbar
        ((HomeDocente) getActivity()).setToolbarTitle(getString(R.string.mie_tesi));
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.clearFocus();

        recyclerView = view.findViewById(R.id.listaTesi);
        database = FirebaseFirestore.getInstance();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        list = new ArrayList<Tesi>();
        myAdapter = new TesiAdapter(getActivity(),list,this);
        recyclerView.setAdapter(myAdapter);
        EventChangeListener();
        Button btn = view.findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegistraTesiFragment dialog = new RegistraTesiFragment();
                dialog.show(getActivity().getSupportFragmentManager(), "RegistraTesiFragment");
                //getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.container, registraTesiFragment).commit();
            }
        });




        return view;
    }

    private void EventChangeListener(){
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String user = auth.getCurrentUser().getUid();
        database.collection("tesi").whereEqualTo("docente",user).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Log.e("Firestore error", error.getMessage());
                    return;
                }
                int i=0;
                for(DocumentChange dc : value.getDocumentChanges()){
                    if(dc.getType() == DocumentChange.Type.ADDED){
                        list.add(dc.getDocument().toObject(Tesi.class));
                        String s = value.getDocuments().get(i).getId();
                        list.get(i).setIdTesi(s);

                        Log.d("TAG","ss"+list.get(i).getDurata());
                        i++;
                    }
                    myAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    @Override
    public void onItemClick(int position) {

        Tesi tesi =list.get(position);
        Bundle bundle = new Bundle();
        bundle.putSerializable("tesi", tesi);
        VisualizzaTesiFragment vistaTask = new VisualizzaTesiFragment();
        vistaTask.setArguments(bundle);
        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
        transaction.replace(R.id.container, vistaTask)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public void onItemLongClick(int position) {


        Log.d("TAG", "ss" + list.get(position).getIdTesi());

        new SchermataCaricamento().conferma(getContext(), getString(R.string.titoloConferma), getString(R.string.msgConferma))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        Query query = database.collection("tesisti").whereEqualTo("tesi", list.get(position).getIdTesi());

                        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
                            if (!queryDocumentSnapshots.isEmpty()) {

                            } else {
                                // Usa l'ID del documento per fare altre operazioni su di esso
                                database.collection("tesi").document(list.get(position).getIdTesi())
                                        .delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.d("msg", "DocumentSnapshot successfully deleted!");
                                                list.remove(position);
                                                myAdapter.notifyItemRemoved(position);
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("msg", "Error deleting document", e);
                                            }
                                        });
                            }
                        });
                        myAdapter.notifyItemRemoved(position);
                    }
                })
                .create()
                .show();
    }
}
