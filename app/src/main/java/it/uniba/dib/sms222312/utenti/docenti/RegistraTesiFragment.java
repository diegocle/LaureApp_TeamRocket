package it.uniba.dib.sms222312.utenti.docenti;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import it.uniba.dib.sms222312.R;
import it.uniba.dib.sms222312.modelli.Tesi;
import it.uniba.dib.sms222312.utenti.UpdateSpinner;

public class RegistraTesiFragment extends DialogFragment {
    private Tesi tesi;
    private FirebaseFirestore db;
    private AlertDialog alertDialog;
    private Context mContext;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.fragment_registra_tesi, null);

        db = FirebaseFirestore.getInstance();
        AutoCompleteTextView spinnerCourses = view.findViewById(R.id.spinner_dipartimento);
        UpdateSpinner updateSpinner = new UpdateSpinner(spinnerCourses,view.findViewById(R.id.spinner_corso));
        updateSpinner.updateSpinnerD(db);
        spinnerCourses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                updateSpinner.updateSpinnerC(parent,position,db);
            }
        });

        builder.setView(view)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        EditText edtNome = view.findViewById(R.id.nome);
                        String nome = edtNome.getText().toString();
                        EditText edtOre = view.findViewById(R.id.durata);
                        String ore = edtOre.getText().toString();
                        EditText edtMedia = view.findViewById(R.id.media);
                        int media = Integer.parseInt(edtMedia.getText().toString());
                        EditText edtDesc = view.findViewById(R.id.descrizione);
                        String descrizione = edtDesc.getText().toString();
                        AutoCompleteTextView coursesSpinner = view.findViewById(R.id.spinner_dipartimento);
                        Object selectedItem = coursesSpinner.getText();
                        String dib = selectedItem.toString();
                        coursesSpinner = view.findViewById(R.id.spinner_corso);
                        selectedItem = coursesSpinner.getText();
                        String corso = selectedItem.toString();
                        Log.d("aa", ""+media);
                        // Verifica che tutti i campi siano stati compilati
                        if (nome.isEmpty() || ore.isEmpty()  || descrizione.isEmpty()) {
                            Toast.makeText(getActivity(), R.string.errorCampiObbligatori, Toast.LENGTH_SHORT).show();
                            alertDialog.setCancelable(false);
                        } else if(dib.isEmpty()){
                            Toast.makeText(getActivity(), R.string.errorDip, Toast.LENGTH_SHORT).show();
                            alertDialog.setCancelable(false);
                        }else if(media<18 || media>30) {
                            Toast.makeText(getActivity(), R.string.mediaErr, Toast.LENGTH_SHORT).show();
                            alertDialog.setCancelable(false);
                        }else{
                                FirebaseAuth auth = FirebaseAuth.getInstance();
                                String user = auth.getCurrentUser().getUid();
                                tesi = new Tesi(user, nome, dib,corso, ore, String.valueOf(media), descrizione);
                                registraTesi(tesi);
                                dialog.dismiss();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        RegistraTesiFragment.this.getDialog().cancel();
                    }
                });

        alertDialog = builder.create();
        return alertDialog;
    }



    private void registraTesi(Tesi tesi) {
        db.collection("tesi").add(tesi)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            String message = mContext.getString(R.string.registrazione_avvenuta_con_successo);
                            Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
                            // Recupera l'ID del documento appena creato
                            String documentId = task.getResult().getId();

                            // Aggiorna il documento con l'ID appena recuperato
                            tesi.setIdTesi(documentId); // Supponendo che ci sia un metodo setId_documento() nella classe Tesi
                            db.collection("tesi").document(documentId).set(tesi).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Aggiornamento riuscito, fai qualcosa se necessario
                                    } else {
                                        // Errore durante l'aggiornamento del documento
                                        // Gestisci l'errore qui, se necessario
                                    }
                                }
                            });
                        } else {
                            // Errore durante l'inserimento del documento
                            // Gestisci l'errore qui, se necessario
                        }
                    }

                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), mContext.getString(R.string.errore_durante_la_registrazione), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

}
