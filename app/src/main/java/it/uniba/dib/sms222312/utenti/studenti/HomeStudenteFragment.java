package it.uniba.dib.sms222312.utenti.studenti;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;


import it.uniba.dib.sms222312.R;
import it.uniba.dib.sms222312.modelli.RichiestaTesi;
import it.uniba.dib.sms222312.utenti.docenti.HomeDocente;
import it.uniba.dib.sms222312.modelli.menuHome.MyAdapterMenu;
import it.uniba.dib.sms222312.modelli.menuHome.cardHome;

public class HomeStudenteFragment extends Fragment implements MyAdapterMenu.OnItemClickListener{

    private RecyclerView recyclerView;
    private MyAdapterMenu adapter;
    private BottomNavigationView bottomNavigationView;
    private List<cardHome> menuItems = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.page_1);
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);

        ((HomeStudente) getActivity()).setToolBarEmpty();


        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int numColumns = (int) (dpWidth / 120);
        getMenuItems(); // Recupera la lista degli elementi del menu
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), numColumns));

        // Imposta l'adattatore per il RecyclerView
        MyAdapterMenu adapter = new MyAdapterMenu(menuItems, getContext(), this);
        recyclerView.setAdapter(adapter);
        return view;

    }



    private void getMenuItems() {
        if(menuItems.size()==0) {
            // Aggiungi gli elementi del menu
            //menuItems.add(new cardHome(R.drawable.icona_chat, "Chat" , HomeDocente.class.getName()));
            //menuItems.add(new cardHome(R.drawable.icona_tesi, "Info Tesi", HomeDocente.class.getName()));
            menuItems.add(new cardHome(R.drawable.icona_classifica, getString(R.string.classifica_tesi), VisualizzaClassificaFragment.class.getName()));
            menuItems.add(new cardHome(R.drawable.icona_ricerca, getString(R.string.ricercaTesi), CercaTesiFragment.class.getName()));
            menuItems.add(new cardHome(R.drawable.icona_ricevimenti, getString(R.string.ricevimenti), ListaRicevimentiStudenteFragment.class.getName()));
            menuItems.add(new cardHome(R.drawable.icona_mie_tesi, getString(R.string.miaTesi), ListaTaskFragment.class.getName()));
            menuItems.add(new cardHome(R.drawable.icona_richieste_tesi, getString(R.string.richieste_tesi), RichiestaTesiFragment.class.getName()));
        }
    }

    @Override
    public void onItemClick(cardHome menuItem) {
        try{
            Class<?> fragmentClass = Class.forName(menuItem.getactivityName());
            if(fragmentClass.getName() ==ListaTaskFragment.class.getName()){
                FirebaseAuth fAuth = FirebaseAuth.getInstance();
                String userId = fAuth.getCurrentUser().getUid();
                FirebaseFirestore database = FirebaseFirestore.getInstance();
                Query query = database.collection("tesisti").whereEqualTo("studente", userId);
                query.get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            Fragment fragment = null;
                            if (!queryDocumentSnapshots.isEmpty()) {
                                try {
                                    fragment = (Fragment) fragmentClass.newInstance();
                                } catch (IllegalAccessException ex) {
                                    throw new RuntimeException(ex);
                                } catch (java.lang.InstantiationException ex) {
                                    throw new RuntimeException(ex);
                                }
                            }else{
                                 fragment = new PaginaVuota();
                            }
                            // Inizia una nuova transazione del Fragment
                            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                            transaction.replace(R.id.container, fragment)
                                    .addToBackStack(null)
                                    .commit();
                        });

            }else{
            Fragment fragment = (Fragment) fragmentClass.newInstance();
            // Inizia una nuova transazione del Fragment
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.container, fragment)
                    .addToBackStack(null)
                    .commit();
            }
            /*Intent intent = new Intent(getActivity(), Class.forName(menuItem.getactivityName())); // Replace with the name of your activity
            // Add any extras or data to the intent if needed
            startActivity(intent);*/
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        }
    }


    /*// Aggiungi gli elementi del menu
        menuItems.add(new cardHome(R.drawable.icona_chat, "Chat"));
        menuItems.add(new cardHome(R.drawable.icona_classifica, "Classifica Tesi"));
        menuItems.add(new cardHome(R.drawable.icona_ricerca, "Ricerca Tesi"));
        menuItems.add(new cardHome(R.drawable.icona_ricevimenti, "Ricevimenti"));
        menuItems.add(new cardHome(R.drawable.icona_tesi, "Info Tesi"));
        menuItems.add(new cardHome(R.drawable.icona_mie_tesi, "Mie Tesi"));
        menuItems.add(new cardHome(R.drawable.icona_richieste_tesi, "Richiesta Tesi"));*/
}
