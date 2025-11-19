package it.uniba.dib.sms222312.utenti.studenti;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import it.uniba.dib.sms222312.R;
import it.uniba.dib.sms222312.SchermataCaricamento;
import it.uniba.dib.sms222312.modelli.Classifica;
import it.uniba.dib.sms222312.modelli.RecyclerViewInterface;
import it.uniba.dib.sms222312.modelli.Tesi;
import it.uniba.dib.sms222312.modelli.TesiAdapter;
import it.uniba.dib.sms222312.modelli.compara.ComparaDurata;
import it.uniba.dib.sms222312.modelli.compara.ComparaMedia;
import it.uniba.dib.sms222312.modelli.compara.ComparaPos;

public class VisualizzaClassificaFragment extends Fragment implements RecyclerViewInterface {

    RecyclerView recyclerView;
    ArrayList<Classifica> classificaArrayList;
    TesiAdapter myAdapter;
    FirebaseFirestore db;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_visualizza_classifica, container, false);
        TextView t = view.findViewById(R.id.textOrder);

        //configuro appbarr e navigationbar
        ((HomeStudente) getActivity()).setToolbarTitle(getResources().getString(R.string.classificaTesi));
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.clearFocus();


        recyclerView = view.findViewById(R.id.classifica);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        db = FirebaseFirestore.getInstance();
        classificaArrayList = new ArrayList<Classifica>();
        myAdapter = new TesiAdapter<Classifica>(getContext(), classificaArrayList, this);
        myAdapter.setType(Classifica.class.getName());
        recyclerView.setAdapter(myAdapter);

        EventChangeListener();

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        Button button = view.findViewById(R.id.popup_button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showPopupMenu(view,t);
            }
        });

        ComparaPos comparaPos = new ComparaPos();
        Collections.sort(classificaArrayList, comparaPos);
        myAdapter.notifyDataSetChanged();
        t.setText(R.string.order_by_pos);

        return view;
    }

    private void EventChangeListener() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String user = auth.getCurrentUser().getUid();

        db.collection("classifica").whereEqualTo("utente",user).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null){
                    Log.e("Firestore error",error.getMessage());
                    return;
                }
                int i=0;
                for(DocumentChange dc :value.getDocumentChanges()){
                    if(dc.getType() == DocumentChange.Type.ADDED){

                        classificaArrayList.add(dc.getDocument().toObject(Classifica.class));
                    }
                    myAdapter.notifyDataSetChanged();
                }
            }
        });


    }


    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.START | ItemTouchHelper.END, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

            FirebaseAuth auth = FirebaseAuth.getInstance();
            String user = auth.getCurrentUser().getUid();

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();
           if(fromPosition!=toPosition){

               documentid(user, fromPosition, toPosition);
           }
            Collections.swap(classificaArrayList,fromPosition,toPosition);

            recyclerView.getAdapter().notifyItemMoved(fromPosition, toPosition);

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
                                    Query query = db.collection("classifica").whereEqualTo("nome", classificaArrayList.get(position).getNome()).whereEqualTo("media", classificaArrayList.get(position).getMedia()).whereEqualTo("durata", classificaArrayList.get(position).getDurata()).whereEqualTo("utente", classificaArrayList.get(position).getUtente());
                                    query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        @Override
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            if (!queryDocumentSnapshots.isEmpty()) {
                                                // Ottieni il primo documento corrispondente
                                                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                                                // Ottieni l'ID del documento
                                                String documentId = documentSnapshot.getId();

                                                // Usa l'ID del documento per fare altre operazioni su di esso
                                                db.collection("classifica").document(documentId)
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
                                    classificaArrayList.remove(position);
                                    myAdapter.notifyItemRemoved(position);
                                }})
                            .create().show();
                    break;
            }

        }
    };

    @Override
    public void onItemClick(int position) {

        Classifica classifica =classificaArrayList.get(position);
        String idTesi = classifica.getIdTesi();
        Log.d("",idTesi);
        DocumentReference docRef = db.collection("tesi").document(idTesi);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {

                    Tesi tesi = documentSnapshot.toObject(Tesi.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("tesi", tesi);
                    VisualizzaTesiStudenteFragment vistaTask = new VisualizzaTesiStudenteFragment();
                    vistaTask.setArguments(bundle);
                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                    transaction.replace(R.id.container, vistaTask)
                            .addToBackStack(null)
                            .commit();
                }

            }
        });
    }


    @Override
    public void onItemLongClick(int position) {

    }

    private void documentid(String user, int fromPosition, int toPosition) {
        Classifica c = classificaArrayList.get(fromPosition);
        Classifica c1 = classificaArrayList.get(toPosition);
        c.setPos(toPosition+1);
        c1.setPos(fromPosition+1);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        CollectionReference tesiRef = database.collection("classifica");
        Query query = tesiRef.whereEqualTo("pos", fromPosition+1).whereEqualTo("utente", user);
        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String documentId = document.getId();
                        DocumentReference docRef = tesiRef.document(documentId);
                        Query query = tesiRef.whereEqualTo("pos", toPosition + 1).whereEqualTo("utente", user);
                        query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        String documentId = document.getId();
                                        DocumentReference docRef1 = tesiRef.document(documentId);
                                        Map<String, Object> updates = new HashMap<>();
                                        updates.put("pos", c1.getPos());
                                        docRef1.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d("c1", c1.toString());
                                                }
                                            }
                                        });
                                    }
                                }
                                Map<String, Object> updates = new HashMap<>();
                                updates.put("pos", c.getPos());
                                docRef.update(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("c", c.toString());
                                        }
                                    }
                                });
                            }
                        });

                    }
                }
            }
        });
    }

    private void showPopupMenu(View view, TextView t) {
        PopupMenu popup = new PopupMenu(getContext(), view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.ordinamento_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.order_by_pos:
                        ComparaPos comparaPos = new ComparaPos();
                        Collections.sort(classificaArrayList, comparaPos);
                        myAdapter.notifyDataSetChanged();
                        t.setText(R.string.order_by_pos);

                        break;
                    case R.id.order_by_media:
                        ComparaMedia comparator = new ComparaMedia();
                        Collections.sort(classificaArrayList, comparator);
                        myAdapter.notifyDataSetChanged();
                        t.setText(R.string.order_by_media);

                        break;
                    case R.id.order_by_durata:
                        ComparaDurata comparatord = new ComparaDurata();
                        Collections.sort(classificaArrayList, comparatord);
                        myAdapter.notifyDataSetChanged();
                        t.setText(R.string.order_by_durata);

                        Log.d("",myAdapter.toString());
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });
        popup.show();
    }


}