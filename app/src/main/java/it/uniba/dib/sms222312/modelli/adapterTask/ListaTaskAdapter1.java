package it.uniba.dib.sms222312.modelli.adapterTask;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import it.uniba.dib.sms222312.R;
import it.uniba.dib.sms222312.modelli.TaskTesi;

public class ListaTaskAdapter1 extends RecyclerView.Adapter<ListaTaskAdapter.ViewHolder> {
    private ListaTaskAdapter.OnItemClickListener listener;
    private static ArrayList<TaskTesi> taskArrayList;

    public ListaTaskAdapter1(Context context, ArrayList<TaskTesi> taskArrayList, ListaTaskAdapter.OnItemClickListener listener) {
        this.taskArrayList = taskArrayList;
        this.listener = listener;
    }

    public ListaTaskAdapter1(ArrayList<TaskTesi> taskArrayList, ListaTaskAdapter.OnItemClickListener listener) {
        this.taskArrayList = taskArrayList;
        this.listener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(int pos, ArrayList<TaskTesi> taskArrayList);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView nome;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.nomeTesi);
        }
    }

    @NonNull
    @Override
    public ListaTaskAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_task, parent, false);
        return new ListaTaskAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ListaTaskAdapter.ViewHolder holder, int position) {
        TaskTesi task = taskArrayList.get(position);
        holder.nome.setText(task.getNome());
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