package it.uniba.dib.sms222312.utenti;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.StorageReference;

import java.io.File;

import it.uniba.dib.sms222312.R;
import it.uniba.dib.sms222312.SchermataCaricamento;
import it.uniba.dib.sms222312.modelli.adapterFile.AdapterFileCard;
import it.uniba.dib.sms222312.modelli.TaskTesi;

public class VisualizzaTaskFragment extends Fragment implements AdapterFileCard.OnItemClickListener{

    private static final int REQUEST_WRITE_STORAGE = 1;
    SchermataCaricamento dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_visualizza_task, container, false);
        dialog = new SchermataCaricamento(getActivity());

        Bundle bundle = getArguments();
        TaskTesi task = (TaskTesi) bundle.getSerializable("task");
        String tesista = task.getTesista();
        String nome = task.getNome();
        String descrizione = task.getDescrizione();
        String stato = task.getStato();
        String scadenza = task.getScadenza();


        TextView txtNome = view.findViewById(R.id.nome);
        TextView txtDescrizione = view.findViewById(R.id.descrizione);
        TextView txtScadenza = view.findViewById(R.id.scadenza);
        TextView txtStato = view.findViewById(R.id.stato);
        Button btnIniziato = view.findViewById(R.id.impostaIniziato);
        Button btnCompletato = view.findViewById(R.id.impostaCompletato);

        txtNome.setText(nome);
        txtDescrizione.setText(descrizione);
        txtScadenza.setText(scadenza);
        txtStato.setText(stato);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        AdapterFileCard adapter = new AdapterFileCard(task.getFile(),this);
        recyclerView.setAdapter(adapter);

        if(stato.equals("Completato")){
            btnIniziato.setVisibility(View.GONE);
            btnCompletato.setVisibility(View.GONE);
        }
        if(stato.equals("Iniziato")){
            btnIniziato.setVisibility(View.GONE);
        }

        btnIniziato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Query query = db.collection("task").whereEqualTo("nome", nome).whereEqualTo("descrizione", descrizione).whereEqualTo("scadenza", scadenza).whereEqualTo("stato", txtStato.getText().toString());
                query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Ottieni il primo documento corrispondente
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                            // Ottieni l'ID del documento
                            String documentId = documentSnapshot.getId();
                            DocumentReference taskRef = db.collection("task").document(documentId);

                            taskRef.update("stato", "Iniziato")
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Modifica del campo "stato" completata con successo
                                            txtStato.setText("Iniziato");
                                            Toast.makeText(getActivity(),"TaskTesi iniziato",Toast.LENGTH_SHORT).show();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Gestione dell'errore
                                            Toast.makeText(getActivity(),"Impossibile impostare TaskTesi Iniziato",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // Nessun documento corrispondente trovato
                        }

                    }
                });

            }
        });

        btnCompletato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Query query = db.collection("task").whereEqualTo("nome", nome).whereEqualTo("descrizione", descrizione).whereEqualTo("scadenza", scadenza).whereEqualTo("stato", txtStato.getText().toString());
                query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // Ottieni il primo documento corrispondente
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);

                            // Ottieni l'ID del documento
                            String documentId = documentSnapshot.getId();
                            DocumentReference taskRef = db.collection("task").document(documentId);

                            taskRef.update("stato", "Completato")
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            // Modifica del campo "stato" completata con successo
                                            txtStato.setText("Completato");
                                            Toast.makeText(getActivity(),"TaskTesi completato",Toast.LENGTH_SHORT).show();

                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Gestione dell'errore
                                            Toast.makeText(getActivity(),"Impossibile impostare TaskTesi Completato",Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            // Nessun documento corrispondente trovato
                        }

                    }
                });

            }
        });

        return view;
    }

    private StorageReference mStorageRef;
    private String mFile;

    @Override
    public void onItemClick(StorageReference storageRef, String file) {

        // controlla se il permesso è già stato concesso
        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // il permesso è già stato concesso
            downloadfile(storageRef, file);
        } else {
            // il permesso non è stato ancora concesso, chiedilo all'utente
            mStorageRef = storageRef;
            mFile = file;
            ActivityCompat.requestPermissions(getActivity(), new String[] {android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_WRITE_STORAGE);
        }

    }

    // Gestisci la risposta dell'utente ai permessi
    @SuppressWarnings("deprecation")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Il permesso è stato concesso, puoi fare qualcosa con il file
                downloadfile(mStorageRef, mFile);
            } else {
                // Il permesso è stato negato, mostra un messaggio all'utente
                Toast.makeText(getActivity(), "Permesso negato", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void downloadfile(StorageReference storageRef, String file) {
        dialog.show();
        storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                File publicDownloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                String localFilePath = publicDownloadsDir.getAbsolutePath() + "/" + file;
                File localFile = new File(localFilePath);
                Log.d("path",localFilePath);
                storageRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        dialog.dismiss();
                        // il file è stato scaricato con successo nella posizione locale
                        Toast.makeText(getActivity(), "file scaricato con successo", Toast.LENGTH_SHORT).show();

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        dialog.dismiss();
                        // si è verificato un errore durante lo scaricamento del file
                        Toast.makeText(getActivity(), "si è verificato un errore", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
    }
}