package it.uniba.dib.sms222312.modelli.adapterTask;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.dib.sms222312.R;
import it.uniba.dib.sms222312.modelli.TaskTesi;

public class ListaTaskAdapter extends RecyclerView.Adapter<ListaTaskAdapter.ViewHolder> {
    private OnItemClickListener listener;
    private static ArrayList<TaskTesi> taskArrayList;


    public ListaTaskAdapter(ArrayList<TaskTesi> taskArrayList, OnItemClickListener listener) {
        this.taskArrayList = taskArrayList;
        this.listener = listener;
    }



    public interface OnItemClickListener {
        void onItemClick(int pos, ArrayList<TaskTesi> taskArrayList);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nome;
        TextView scadenza;
        TextView stato;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.nomeTesi);
            scadenza = itemView.findViewById(R.id.scadenza);
            stato = itemView.findViewById(R.id.stato);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_task, parent, false);
            return new ViewHolder(v);
        }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TaskTesi task = taskArrayList.get(position);
        holder.nome.setText(task.getNome());
        holder.scadenza.setText(task.getScadenza());
        holder.stato.setText(task.getStato());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(holder.getBindingAdapterPosition(), taskArrayList);
            }
        });
    }


    @Override
    public int getItemCount() {
        if(taskArrayList == null) return 0;
        return taskArrayList.size();
    }

}
/*
public class ListaTaskAdapter extends RecyclerView.Adapter<ListaTaskAdapter.ViewHolder> {

    private final ListaRichiesteInterface recyclerViewInterface;
    private Context context;
    private static ArrayList<TaskTesi> taskArrayList;

    public ListaTaskAdapter(Context context, ArrayList<TaskTesi> taskArrayList, ListaRichiesteInterface listaRichiesteInterface) {
        this.context = context;
        this.taskArrayList = taskArrayList;
        this.recyclerViewInterface = listaRichiesteInterface;
    }

    public interface OnItemClickListener {
        void onItemClick(StorageReference storageRef, String file);
    }

    @NonNull
    @Override
    public ListaTaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.itemtesi, parent, false);

        return new ViewHolder(v, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaTaskAdapter.ViewHolder holder, int position) {

        TaskTesi task = taskArrayList.get(position);

        holder.nome.setText(task.getNome());

    }

    @Override
    public int getItemCount() {
        return taskArrayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView nome;

        public ViewHolder(@NonNull View itemView, ListaRichiesteInterface recyclerViewInterface) {
            super(itemView);
            nome = itemView.findViewById(R.id.nomeTesi);

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
}*/
