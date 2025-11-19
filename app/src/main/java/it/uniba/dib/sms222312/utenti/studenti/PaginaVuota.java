package it.uniba.dib.sms222312.utenti.studenti;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import it.uniba.dib.sms222312.R;

public class PaginaVuota extends Fragment{


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //configuro appbarr e navigationbar
        ((HomeStudente) getActivity()).setToolbarTitle(getResources().getString(R.string.taskTesi));
        ((HomeStudente) getActivity()).setflag(false);
        BottomNavigationView bottomNavigationView = getActivity().findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.page_2);


        View view = inflater.inflate(R.layout.pagina_vuota, container, false);
        return view;
    }
}
