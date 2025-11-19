package it.uniba.dib.sms222312.utenti.studenti;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import it.uniba.dib.sms222312.R;
import it.uniba.dib.sms222312.modelli.Classifica;
import it.uniba.dib.sms222312.modelli.Tesi;

public class VisualizzaTesiStudenteFragment extends Fragment {

    Bitmap bitmap;
    String docenta;
    private Classifica classifica;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth auth = FirebaseAuth.getInstance();

        String user = auth.getCurrentUser().getUid();
            //configuro appbarr
        ((HomeStudente) getActivity()).setToolbarTitle(getResources().getString(R.string.visualizzaTesi));


        //configuro il layout del fragment
        View view = inflater.inflate(R.layout.fragment_visualizza_tesi, container, false);

        Button indietro = view.findViewById(R.id.my_icon_button);
        indietro.setVisibility(View.GONE);
        //recupero i dati
        Bundle bundle = getArguments();
        Tesi tesi = (Tesi) bundle.getSerializable("tesi");

        String nome = tesi.getNome();
        String corso = tesi.getCorso();
        String descrizione = tesi.getDescrizione();
        String media = tesi.getMedia();
        String durata = tesi.getDurata();
        String docente = tesi.getDocente();
        String idtesi = tesi.getIdTesi();


        //imposto i dati
        Log.d("",tesi.toString());
        TextView textNome = view.findViewById(R.id.nome);
        TextView textCorso = view.findViewById(R.id.corso);
        TextView textDescrizione = view.findViewById(R.id.descrizione);
        TextView textMedia = view.findViewById(R.id.media);
        TextView textDurata = view.findViewById(R.id.durata);
        TextView textDocente = view.findViewById(R.id.docente);
        ImageView imageQr = view.findViewById(R.id.qr);
        Button classificaButton = view.findViewById(R.id.classifica);
        Button richiediButton = view.findViewById(R.id.richiesta);
        Button condividiButton = view.findViewById(R.id.condividi);
        Button contattaButton = view.findViewById(R.id.contatta);



        String text =getString(R.string.nome)+": "+ nome + " "+getString(R.string.corso)+": "+ corso + " "+getString(R.string.media)+": " + media +" "+getString(R.string.durata)+": " + durata +  " "+getString(R.string.durata)+": " + descrizione;
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix matrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 400, 400);
            BarcodeEncoder encoder = new BarcodeEncoder();
            bitmap = encoder.createBitmap(matrix);
            imageQr.setImageBitmap(bitmap);
        } catch (WriterException e) {
            throw new RuntimeException(e);
        }

        textNome.setText(nome);
        textCorso.setText(corso);
        textDescrizione.setText(descrizione);
        textMedia.setText(media);
        textDurata.setText(durata);

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection("utente").document(docente).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                docenta = documentSnapshot.getString("nome") + " " + documentSnapshot.getString("cognome");
                textDocente.setText(docenta);

            }
        });

        //pulante aggiungi alla classifica
        classificaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseFirestore database = FirebaseFirestore.getInstance();


                database.getInstance()
                        .collection("classifica")
                        .whereEqualTo("utente", user)
                        .whereEqualTo("idTesi", idtesi)
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot documentSnapshots) {
                                Classifica oggetto = null;
                                if (documentSnapshots.isEmpty()) {
                                    uploadClassifica(database, user, idtesi, nome, durata, media);

                                }else {
                                    Toast.makeText(getContext(), "Tesi gia presente in classifica", Toast.LENGTH_SHORT).show();
                                }

                            }
                        });
            }
        });

        Query query = database.collection("tesisti").whereEqualTo("studente", user).whereEqualTo("tesi", idtesi);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                richiediButton.setVisibility(View.GONE);
            }
        });
        query = database.collection("richiestatesi").whereEqualTo("studente", user);
        query.get().addOnSuccessListener(queryDocumentSnapshots -> {
            if (!queryDocumentSnapshots.isEmpty()) {
                richiediButton.setVisibility(View.GONE);
            }
        });

        //pulsante richiesta
        richiediButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                InvioRichiestaFragment dialog = new InvioRichiestaFragment();
                Bundle bundle = new Bundle();
                bundle.putString("Studente", user);
                bundle.putString("Docente", docente);
                bundle.putString("Tesi", idtesi);

                dialog.setArguments(bundle);
                dialog.show(getActivity().getSupportFragmentManager(), "ModificaTesiFragment");

            }
        });

        //pulsante condividi
        condividiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }else{
                    // Codice per condividere un Bitmap su WhatsApp
                    String bitmapPath = MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, "Shared image", null);

                    Uri bitmapUri = Uri.parse(bitmapPath);

                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("image/*");
                    shareIntent.putExtra(Intent.EXTRA_STREAM, bitmapUri);
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "Nome tesi: "+ nome +
                            "\nDocente: " + docenta+
                            "\nDescrizione: " + descrizione);
                    //shareIntent.setPackage("com.whatsapp");

                    startActivity(Intent.createChooser(shareIntent, "Condividi immagine"));
                }

            }
        });

        //pulsante contatta
        contattaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                database.collection("utente").document(docente).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String docentemail = documentSnapshot.getString("email");

                        Intent intent = new Intent(Intent.ACTION_SENDTO);
                        intent.setData(Uri.parse("mailto:"));
                        Log.d("email", docentemail);
                        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{docentemail});
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Informazioni riguardo tesi: "+nome);

                        if(intent.resolveActivity(getActivity().getPackageManager()) != null){
                            startActivity(intent);
                        }else{
                            Toast.makeText(getContext(), "Nessuna app email installata", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
            }
        });
        return view;
    }

    private void uploadClassifica(FirebaseFirestore database, String user, String idtesi, String nome, String durata, String media) {
        FirebaseFirestore.getInstance()
                .collection("classifica")
                .whereEqualTo("utente", user)
                .orderBy("pos", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot documentSnapshots) {
                        Classifica oggetto = null;
                        if (!documentSnapshots.isEmpty()) {
                            // Recupera l'oggetto con il punteggio più alto
                            DocumentSnapshot snapshot = documentSnapshots.getDocuments().get(0);
                            oggetto = snapshot.toObject(Classifica.class);
                            classifica = new Classifica(oggetto.getPos()+1, idtesi, user, nome, durata, media);
                        }else {
                            classifica = new Classifica(1, idtesi, user, nome, durata, media);
                        }
                        database.collection("classifica").document().set(classifica).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Toast.makeText(getContext(),"Aggiunto alla classifica", Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getContext(),"Impossibile aggiungere alla classifica", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                });
    }
}