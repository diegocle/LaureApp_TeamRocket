package it.uniba.dib.sms222312.modelli.adapterFile;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

import it.uniba.dib.sms222312.R;

public class AdapterFileCard  extends RecyclerView.Adapter<AdapterFileCard.ViewHolder> {
    private List<Uri> fileUris = new ArrayList<>();
    private List<String> mData;
    private Boolean flag = false;
    private Boolean close = false;
    private OnItemClickListener listener;
    private OnItemClickListener1 listener1;

    public AdapterFileCard(List<String> mData, OnItemClickListener listener) {
        this.mData = mData;
        this.listener = listener;
    }

    public AdapterFileCard(List<String> mData) {
        this.mData = mData;
        this.flag = true;
    }

    public AdapterFileCard(ArrayList<String> mData, List fileUris, OnItemClickListener1 listener) {
        this.mData = mData;
        this.fileUris = fileUris;
        this.listener1 = listener;
    }

    public interface OnItemClickListener1 {
        void onItemClick1(Uri text);
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.title);
            imageView = itemView.findViewById(R.id.im_dw);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_file, parent, false);
        return new ViewHolder(view);
    }

    public interface OnItemClickListener {
        void onItemClick(StorageReference storageRef, String file);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String text = mData.get(position);
        if(!fileUris.isEmpty()){
            Uri uri = fileUris.get(position);
            holder.imageView.setImageResource(R.drawable.ic_baseline_close_24);
            holder.textView.setText(text);
            holder.textView.setTag(uri);
            holder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener1.onItemClick1(uri);
                }
            });
            return;
        }
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReferenceFromUrl(text);
        holder.textView.setText(storageRef.getName());
        //Controllo se bisogna far visualizzare il pulsante di download
        if (flag) {
            holder.imageView.setVisibility(View.GONE);
            return;
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onItemClick(storageRef,storageRef.getName());
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }
}
