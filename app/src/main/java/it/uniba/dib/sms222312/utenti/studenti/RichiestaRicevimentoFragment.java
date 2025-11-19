package it.uniba.dib.sms222312.utenti.studenti;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import it.uniba.dib.sms222312.R;
import it.uniba.dib.sms222312.modelli.Ricevimento;
import it.uniba.dib.sms222312.modelli.StatoRicevimento;
import it.uniba.dib.sms222312.modelli.TaskTesi;

public class RichiestaRicevimentoFragment extends DialogFragment {

    private TextInputEditText textViewData;
    private EditText edtDettagli;
    private AutoCompleteTextView spinnerArgomento;
    private Button btnRichiedi;
    String dataRicevimento = null;
    private FirebaseFirestore db;
    private AlertDialog alertDialog;

    private Context mContext;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.activity_richiesta_ricevimento, null);

        Bundle args = getArguments();

        String tesista = args.getString("Tesista");

        textViewData = view.findViewById(R.id.date_picker_text_input_edit_text);
        edtDettagli = view.findViewById(R.id.dettagliRicevimento);
        spinnerArgomento = view.findViewById(R.id.spinner_argomento);
        btnRichiedi = view.findViewById(R.id.aggiungiRichiesta);

        db = FirebaseFirestore.getInstance();
        List<String> taska = new ArrayList<>();

        db.collection("task").whereEqualTo("tesista", tesista)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Recupera tutti i documenti dalla query
                            List<DocumentSnapshot> documents = task.getResult().getDocuments();
                            // Per ogni documento, recupera il nome del corso e lo aggiunge alla lista
                            for (DocumentSnapshot document : documents) {
                                String name = document.getString("nome");

                                TaskTesi tasko = new TaskTesi(name);
                                taska.add(tasko.getNome());
                            }
                            // Popola lo Spinner con la lista di corsi
                            ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, taska);
                            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerArgomento.setAdapter(spinnerAdapter);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        btnRichiedi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dettagli = edtDettagli.getText().toString();
                String selectedText = spinnerArgomento.getText().toString();
                String selectedObject = null;
                for (String obj : taska) {
                    if (obj.toString().equals(selectedText)) {
                        selectedObject = obj;
                        break;
                    }
                }
                String selectedPersonId = null;
                if (selectedObject != null) {
                    selectedPersonId = selectedObject;
                }

                Ricevimento ricevimento = new Ricevimento(tesista, selectedPersonId, dataRicevimento, dettagli, StatoRicevimento.Inviata);

                db.collection("ricevimenti").document().set(ricevimento).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(mContext,"Richiesta inviata", Toast.LENGTH_SHORT).show();
                        RichiestaRicevimentoFragment.this.getDialog().dismiss();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext,"Impossibile inviare richiesta", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        View btnDP = view.findViewById(R.id.date_picker_text_input_edit_text);
        btnDP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickerDialog(view);
            }
        });
        builder.setView(view);
        alertDialog = builder.create();
        return alertDialog;
    }

    private static final String DATE_PICKER_TAG = "datePicker";

    public void showDatePickerDialog(View v) {
        MaterialDatePicker.Builder builder = MaterialDatePicker.Builder.datePicker();
        builder.setTitleText("Select date");
        builder.setTheme(com.google.android.material.R.style.ThemeOverlay_MaterialComponents_MaterialCalendar);
        builder.setCalendarConstraints(new CalendarConstraints.Builder()
                .setStart(System.currentTimeMillis() - 1000)
                .setValidator(DateValidatorPointForward.now())
                .build());

        MaterialDatePicker<Long> materialDatePicker = builder.build();
        materialDatePicker.addOnPositiveButtonClickListener(new MaterialPickerOnPositiveButtonClickListener<Long>() {
            @Override
            public void onPositiveButtonClick(Long selection) {
                // Converti la data selezionata in una stringa formattata
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                dataRicevimento = formatter.format(new Date((Long) selection));
                textViewData.setText(dataRicevimento);
            }
        });

        materialDatePicker.show(getActivity().getSupportFragmentManager(), DATE_PICKER_TAG);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}