package it.uniba.dib.sms222312.modelli.adapterRicevimenti;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import it.uniba.dib.sms222312.R;
import it.uniba.dib.sms222312.modelli.ListaRichiesteInterface;
import it.uniba.dib.sms222312.modelli.Ricevimento;

public class RichiesteRicevimentoAdapter extends RecyclerView.Adapter<RichiesteRicevimentoAdapter.myViewHolder> {

    Context context;
    ArrayList<Ricevimento> ricevimentoArrayList;
    private final ListaRichiesteInterface recyclerViewInterface;

    public RichiesteRicevimentoAdapter(Context context, ArrayList<Ricevimento> ricevimentoArrayList, ListaRichiesteInterface listaRichiesteInterface) {
        this.context = context;
        this.ricevimentoArrayList = ricevimentoArrayList;
        this.recyclerViewInterface = listaRichiesteInterface;
    }

    @NonNull
    @Override
    public RichiesteRicevimentoAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.itemrichiesta, parent, false);

        return new myViewHolder(v, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull RichiesteRicevimentoAdapter.myViewHolder holder, int position) {

        Ricevimento ricevimento = ricevimentoArrayList.get(position);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("tesisti").document(ricevimento.getTesista()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                String studente = documentSnapshot.getString("studente");
                db.collection("utente").document(studente).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        holder.matricola.setText(documentSnapshot.getString("matricola"));
                        holder.nome.setText(documentSnapshot.getString("nome"));
                        holder.cognome.setText(documentSnapshot.getString("cognome"));

                    }
                });

            }
        });


    }

    @Override
    public int getItemCount() {
        return ricevimentoArrayList.size();
    }

    public static class myViewHolder extends RecyclerView.ViewHolder{
        TextView matricola, nome, cognome;

        public myViewHolder(@NonNull View itemView, ListaRichiesteInterface recyclerViewInterface) {
            super(itemView);
            matricola = itemView.findViewById(R.id.matricolaRichiesta);
            nome = itemView.findViewById(R.id.nomeRichiesta);
            cognome = itemView.findViewById(R.id.cognomeRichiesta);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface != null){
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(pos);
                        }
                    }
                }
            });

        }
    }
}
