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

public class TesiAdapter<T> extends RecyclerView.Adapter<TesiAdapter.TesiViewHolder> {
    private RecyclerViewInterface recyclerViewInterface;
    private String type = Tesi.class.getName();
    Context context;
    ArrayList<T> list;

    public TesiAdapter(Context context, ArrayList<T> list, RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.list = list;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    public void setFilteredList(ArrayList<T> filteredList){
        this.list = filteredList;
        notifyDataSetChanged();
    }

    public void setType(String type) {
        this.type = type;
    }

    @NonNull
    @Override
    public TesiViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.card_tesi,parent,false);
        return new TesiViewHolder(v, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull TesiViewHolder holder, int position) {

        if(type.equals(Tesi.class.getName())){
            Tesi tesi = (Tesi) list.get(position);
            holder.nomeTesi.setText(tesi.getNome());
        }else{
            Classifica tesi = (Classifica) list.get(position);
            holder.nomeTesi.setText(tesi.getNome());
        }
        //
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class TesiViewHolder extends RecyclerView.ViewHolder{

        TextView nomeTesi;

        public TesiViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);

            nomeTesi = itemView.findViewById(R.id.nomeTesi);
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

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    if(recyclerViewInterface != null){
                        int pos = getAdapterPosition();

                        if(pos != RecyclerView.NO_POSITION){
                            recyclerViewInterface.onItemLongClick(pos);
                        }
                    }
                    return true;
                }
            });

        }
    }
}
