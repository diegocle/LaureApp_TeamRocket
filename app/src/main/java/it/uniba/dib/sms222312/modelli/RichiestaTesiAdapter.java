package it.uniba.dib.sms222312.modelli;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import it.uniba.dib.sms222312.R;

public class RichiestaTesiAdapter extends RecyclerView.Adapter<RichiestaTesiAdapter.RichiestaTesiViewHolder> {
    private final ListaRichiesteInterface listaRichiesteInterface;
    Context context;
    ArrayList<RichiestaTesi> richiestaTesiArrayList;

    public RichiestaTesiAdapter(Context context, ArrayList<RichiestaTesi> richiestaTesiArrayList, ListaRichiesteInterface listaRichiesteInterface) {
        this.context = context;
        this.richiestaTesiArrayList = richiestaTesiArrayList;
        this.listaRichiesteInterface = listaRichiesteInterface;
    }

    @NonNull
    @Override
    public RichiestaTesiAdapter.RichiestaTesiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.itemrichiesta,parent,false);

        return new RichiestaTesiViewHolder(v, listaRichiesteInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull RichiestaTesiAdapter.RichiestaTesiViewHolder holder, int position) {

        RichiestaTesi richiestaTesi = richiestaTesiArrayList.get(position);

        // Ottiene il riferimento al documento da caricare
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("utente").document(richiestaTesi.getStudente());

        // Legge i dati del documento
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // Converte i dati del documento in un oggetto MyObject
                Studente studente = documentSnapshot.toObject(Studente.class);
                holder.matricola.setText(studente.getMatricola());
                holder.nome.setText(studente.getNome());
                holder.cognome.setText(studente.getCognome());

            }
        });

    }

    @Override
    public int getItemCount() {
        return richiestaTesiArrayList.size();
    }

    public static class RichiestaTesiViewHolder extends RecyclerView.ViewHolder{

        TextView matricola, nome, cognome;

        public RichiestaTesiViewHolder(@NonNull View itemView, ListaRichiesteInterface listaRichiesteInterface) {
            super(itemView);
            matricola = itemView.findViewById(R.id.matricolaRichiesta);
            nome = itemView.findViewById(R.id.nomeRichiesta);
            cognome = itemView.findViewById(R.id.cognomeRichiesta);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(listaRichiesteInterface != null){
                        int position = getAdapterPosition();

                        if(position != RecyclerView.NO_POSITION){
                            listaRichiesteInterface.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
