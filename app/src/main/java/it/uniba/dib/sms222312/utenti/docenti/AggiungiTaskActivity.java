package it.uniba.dib.sms222312.utenti.docenti;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointForward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.datepicker.MaterialPickerOnPositiveButtonClickListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import it.uniba.dib.sms222312.R;
import it.uniba.dib.sms222312.SchermataCaricamento;
import it.uniba.dib.sms222312.modelli.adapterFile.AdapterFileCard;
import it.uniba.dib.sms222312.modelli.TaskTesi;

public class AggiungiTaskActivity extends Fragment implements AdapterFileCard.OnItemClickListener1{

    private EditText edtNome;
    private EditText edtDescrizione;
    private TextInputEditText textViewDataScadenza;
    private Button buttonScegliFile;
    private Button buttonCaricaDati;
    private HashSet<Uri> fileUris = new HashSet<>();
    String dataScadenza = null;
    View view1;
    // Inizializza il ProgressBar
    SchermataCaricamento dialog;
    List<String> listaFile = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((HomeDocente) getActivity()).setToolbarTitle(getString(R.string.visualizzaTask));
        view1 = inflater.inflate(R.layout.fragment_aggiungi_task, container, false);

        Bundle bundle = getArguments();
        String tesista = bundle.getString("Tesista");

        dialog = new SchermataCaricamento(getContext());



        // Inizializza le variabili
        edtNome = view1.findViewById(R.id.nomeTask);
        edtDescrizione = view1.findViewById(R.id.descrizioneTask);
        textViewDataScadenza = view1.findViewById(R.id.date_picker_text_input_edit_text);
        buttonScegliFile = view1.findViewById(R.id.caricaFile);
        buttonCaricaDati = view1.findViewById(R.id.aggiungiTask);

        // Ottieni i dati inseriti dall'utente


        // Aggiungi un listener al pulsante per selezionare i file
        buttonScegliFile.setOnClickListener(view -> scegliFile());

        // Aggiungi un listener al pulsante per caricare i file
        buttonCaricaDati.setOnClickListener(view -> uploadData(dataScadenza, tesista));
        view1.findViewById(R.id.date_picker_text_input_edit_text).setOnClickListener(view -> showDatePickerDialog(view));

        return view1;
    }

    private void uploadData(String dataScadenza, String tesista) {
        String nome = edtNome.getText().toString();
        String descrizione = edtDescrizione.getText().toString();
        Log.d("",dataScadenza+descrizione+nome);
        // Verifica che tutti i campi siano stati compilati
        if (nome.isEmpty() || descrizione.isEmpty() || dataScadenza.isEmpty()) {
            Toast.makeText(getContext(), R.string.compila_tutti_i_campi, Toast.LENGTH_SHORT).show();
            return;
        }

        //codice per inizializzare Firebase
        FirebaseApp.initializeApp(getContext());
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();


        // Mostra il ProgressBar
        dialog.show();

        // Carica il file selezionato su Firebase Storage
        // carica il file
        if(!fileUris.isEmpty()) {
            for (Uri fileUri : fileUris) {
                StorageReference storageRef = storage.getReference().child(tesista + "/" + getFileName(fileUri));
                storageRef.putFile(fileUri)
                        .addOnSuccessListener(taskSnapshot -> {
                            // il file è stato caricato con successo
                            // ottieni l'URL del file appena caricato
                            storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                                // fai qualcosa con l'URL del file, ad esempio salvarlo su Firestore
                                listaFile.add(uri.toString());
                                if (listaFile.size() == fileUris.size()) {
                                    // Crea un oggetto "TaskTesi" con i dati inseriti dall'utente e l'URL del file
                                    TaskTesi task = new TaskTesi(tesista, nome, descrizione, dataScadenza, "Non iniziato", listaFile);
                                    uploadTask(database, task);
                                }
                            });
                        })
                        .addOnFailureListener(e -> {
                            // Se si verifica un errore durante il caricamento su Firebase Storage, mostra un messaggio di errore
                            // Nascondi il ProgressBar

                            Toast.makeText(getContext(), R.string.si_verificato_un_errore, Toast.LENGTH_SHORT).show();

                        });
            }
        }else{
            TaskTesi task = new TaskTesi(tesista, nome, descrizione, dataScadenza, "Non iniziato", listaFile);
            uploadTask(database, task);
        }


    }

    private void uploadTask(FirebaseFirestore database, TaskTesi task) {
        // Carica l'oggetto "Dato" su Firebase Database
        database.collection("task").document().set(task)
                .addOnSuccessListener(aVoid -> {
                    // Nascondi il ProgressBar
                    dialog.dismiss();
                    Toast.makeText(getContext(), R.string.dati_caricati_con_successo, Toast.LENGTH_SHORT).show();
                    getActivity().onBackPressed();
                    //finish();
                })
                .addOnFailureListener(e -> {
                    // Se si verifica un errore durante il caricamento su Firebase Database, mostra un messaggio di errore
                    dialog.dismiss();
                    Toast.makeText(getContext(), R.string.si_verificato_un_errore, Toast.LENGTH_SHORT).show();
                });
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
                dataScadenza = formatter.format(new Date((Long) selection));
                textViewDataScadenza.setText(dataScadenza);
            }
        });

        materialDatePicker.show(getActivity().getSupportFragmentManager(), DATE_PICKER_TAG);
    }

    private ActivityResultLauncher<String[]> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.OpenMultipleDocuments(), uris -> {
                if (uris != null && uris.size() > 0) {
                    if (!fileUris.addAll(uris)) {
                        Toast.makeText(getContext(), "Il file selezionato è già presente", Toast.LENGTH_SHORT).show();
                    }
                    Log.d("a", fileUris.toString());
                    String testo = "File selezionati: " + fileUris.size();
                    buttonScegliFile.setText(testo);
                    updateSelectedFilesList();
                }
            });
    private void scegliFile() {
        filePickerLauncher.launch(new String[] {"*/*"});
    }

    private void updateSelectedFilesList() {

        ArrayList<String> myList = new ArrayList<>();
        ArrayList<Uri> myListUri = new ArrayList<>();
        for (Uri uri : fileUris) {
            myList.add(getFileName(uri));
            myListUri.add(uri);
        }
        RecyclerView recyclerView = view1.findViewById(R.id.recyclerView);
        recyclerView.removeAllViews();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        AdapterFileCard adapter = new AdapterFileCard(myList, myListUri, this);
        recyclerView.setAdapter(adapter);

        buttonScegliFile.setText("Scegli file");
    }

    public String getFileName(Uri uri) {
        String result = "unknown_file_name";
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (columnIndex >= 0) {
                    result = cursor.getString(columnIndex);
                }
            }
            cursor.close();
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    @Override
    public void onItemClick1(Uri file) {
        fileUris.remove((Uri) file);
        updateSelectedFilesList();
    }
}