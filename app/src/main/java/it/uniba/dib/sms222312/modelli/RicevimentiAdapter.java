package it.uniba.dib.sms222312.modelli;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.dib.sms222312.R;

public class RicevimentiAdapter extends RecyclerView.Adapter<RicevimentiAdapter.myViewHolder> {

    private final ListaRichiesteInterface recyclerViewInterface;
    Context context;
    ArrayList<Ricevimento> ricevimentoArrayList;

    public RicevimentiAdapter(Context context, ArrayList<Ricevimento> ricevimentoArrayList, ListaRichiesteInterface listaRichiesteInterface) {
        this.context = context;
        this.ricevimentoArrayList = ricevimentoArrayList;
        this.recyclerViewInterface = listaRichiesteInterface;
    }

    @NonNull
    @Override
    public RicevimentiAdapter.myViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.card_ricevimenti,parent,false);

        return new myViewHolder(v, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull RicevimentiAdapter.myViewHolder holder, int position) {

        Ricevimento ricevimento = ricevimentoArrayList.get(position);

        if(ricevimento.getData() != null)
        holder.data.setText(ricevimento.getData());

        if(ricevimento.getTask() != null)
        holder.task.setText(ricevimento.getTask());

        if(ricevimento.getDettagli() != null)
        holder.dettagli.setText(ricevimento.getDettagli());

        if(ricevimento.getStato() != null)
        holder.stato.setText(ricevimento.getStato().toString());


    }

    @Override
    public int getItemCount() {
        return ricevimentoArrayList.size();
    }

    public static class myViewHolder extends RecyclerView.ViewHolder{

        TextView data;
        TextView task;

        TextView stato;
        TextView dettagli;

        public myViewHolder(@NonNull View itemView, ListaRichiesteInterface recyclerViewInterface) {
            super(itemView);
            data = itemView.findViewById(R.id.dataRicevimento);
            task = itemView.findViewById(R.id.taskRicevimento);
            dettagli = itemView.findViewById(R.id.dettagli);
            stato = itemView.findViewById(R.id.statoRicevimenti);

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
