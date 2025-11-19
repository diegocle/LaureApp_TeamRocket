package it.uniba.dib.sms222312.modelli.menuHome;

import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import it.uniba.dib.sms222312.R;

public class MyAdapterMenu extends RecyclerView.Adapter<MyAdapterMenu.MenuViewHolder> {

    private List<cardHome> menuItems;
    private Context context;
    private OnItemClickListener listener;

    public MyAdapterMenu(List<cardHome> menuItems, Context context, OnItemClickListener listener) {
        this.menuItems = menuItems;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_home, parent, false);
        return new MenuViewHolder(view);
    }

    public interface OnItemClickListener {
        void onItemClick(cardHome menuItem);
    }


    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int screenHeight = displayMetrics.heightPixels;
        cardHome menuItem = menuItems.get(position);
        holder.imageView.setImageResource(menuItem.getImage());
        holder.titleTextView.setText(menuItem.getTitle());
        if(screenWidth<screenHeight){
            int textSize = (int) (screenWidth * 0.01f); // Imposta la dimensione del testo al 4% della larghezza dello schermo
            holder.titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        }else{
            int textSize = (int) (screenHeight * 0.015f); // Imposta la dimensione del testo al 4% della larghezza dello schermo
            holder.titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);

        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(menuItem);
            }
        });
    }

    @Override
    public int getItemCount() {
        return menuItems.size();
    }

    public static class MenuViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageView;
        public TextView titleTextView;

        public MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.icon);
            titleTextView = itemView.findViewById(R.id.title);
        }
    }
}
