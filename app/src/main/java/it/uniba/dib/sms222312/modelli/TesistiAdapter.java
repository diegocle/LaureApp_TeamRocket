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

public class TesistiAdapter extends RecyclerView.Adapter<TesistiAdapter.tesistiViewHolder> {

    private final ListaRichiesteInterface recyclerViewInterface;

    Context context;
    ArrayList<Tesista> tesistaArrayList;

    public TesistiAdapter(Context context, ArrayList<Tesista> tesistaArrayList, ListaRichiesteInterface listaRichiesteInterface) {
        this.context = context;
        this.tesistaArrayList = tesistaArrayList;
        this.recyclerViewInterface = listaRichiesteInterface;
    }

    @NonNull
    @Override
    public TesistiAdapter.tesistiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.itemtesista,parent,false);

        return new tesistiViewHolder(v, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull TesistiAdapter.tesistiViewHolder holder, int position) {

        Tesista tesista = tesistaArrayList.get(position);

        // Ottiene il riferimento al documento da caricare
        DocumentReference docRef = FirebaseFirestore.getInstance().collection("utente").document(tesista.getStudente());

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

        // Ottiene il riferimento al documento da caricare
        DocumentReference docRef2 = FirebaseFirestore.getInstance().collection("tesi").document(tesista.getTesi());
        if(docRef2!=null)
        // Legge i dati del documento
        docRef2.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                // Converte i dati del documento in un oggetto MyObject
                Tesi tesi = documentSnapshot.toObject(Tesi.class);
                holder.tesi.setText(tesi.getNome());

            }
        });

    }

    @Override
    public int getItemCount() {
        return tesistaArrayList.size();
    }

    public static class tesistiViewHolder extends RecyclerView.ViewHolder{

        TextView matricola, nome, cognome, tesi;

        public tesistiViewHolder(@NonNull View itemView, ListaRichiesteInterface recyclerViewInterface) {
            super(itemView);
            matricola = itemView.findViewById(R.id.matricolaTesista);
            nome = itemView.findViewById(R.id.nomeTesista);
            cognome = itemView.findViewById(R.id.cognomeTesista);
            tesi = itemView.findViewById(R.id.nomeTesi);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(recyclerViewInterface != null){
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemClick(position);
                        }
                    }
                }
            });
        }
    }
}
