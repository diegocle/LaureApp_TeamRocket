package it.uniba.dib.sms222312.utenti.studenti;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import it.uniba.dib.sms222312.R;
import it.uniba.dib.sms222312.modelli.RichiestaTesi;

public class InvioRichiestaFragment extends DialogFragment {

    private AlertDialog alertDialog;

    private Context mContext;
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.activity_invio_richiesta, null);

        Bundle args = getArguments();
        String studente = args.getString("Studente");
        String docente = args.getString("Docente");
        String tesi = args.getString("Tesi");

        EditText edtDescrizione = view.findViewById(R.id.edit_text);
        Button btnInvia = view.findViewById(R.id.invia);


        btnInvia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String descrizione = edtDescrizione.getText().toString();
                FirebaseFirestore database = FirebaseFirestore.getInstance();
                RichiestaTesi richiestaTesi = new RichiestaTesi(studente, docente, tesi, descrizione);
                database.collection("richiestatesi").document().set(richiestaTesi).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Toast.makeText(mContext,"Richiesta inviata", Toast.LENGTH_SHORT).show();
                                InvioRichiestaFragment.this.getDialog().dismiss();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(mContext,"Impossibile inviare richiesta", Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });
        builder.setView(view);
        alertDialog = builder.create();
        return alertDialog;
    }
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}