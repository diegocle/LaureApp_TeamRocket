package it.uniba.dib.sms222312.utenti.ospite;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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


public class VisualizzaTesiOspite extends AppCompatActivity {

    Bitmap bitmap;
    String docenta;
    private Classifica classifica;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_visualizza_tesi);
        //configuro il layout del fragment


        //recupero i dati

        Tesi tesi = (Tesi)  getIntent().getSerializableExtra("tesi");

        String nome = tesi.getNome();
        String corso = tesi.getCorso();
        String descrizione = tesi.getDescrizione();
        String media = tesi.getMedia();
        String durata = tesi.getDurata();
        String docente = tesi.getDocente();
        String idtesi = tesi.getIdTesi();


        //imposto i dati
        Log.d("",tesi.toString());
        TextView textNome = findViewById(R.id.nome);
        TextView textCorso = findViewById(R.id.corso);
        TextView textDescrizione = findViewById(R.id.descrizione);
        TextView textMedia = findViewById(R.id.media);
        TextView textDurata = findViewById(R.id.durata);
        TextView textDocente = findViewById(R.id.docente);
        ImageView imageQr = findViewById(R.id.qr);
        Button classificaButton = findViewById(R.id.classifica);
        Button richiediButton = findViewById(R.id.richiesta);
        Button condividiButton = findViewById(R.id.condividi);
        Button contattaButton = findViewById(R.id.contatta);



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
        classificaButton.setVisibility(View.GONE);
        //pulsante richiesta
        richiediButton.setVisibility(View.GONE);

        //pulsante condividi
        condividiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(VisualizzaTesiOspite.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(VisualizzaTesiOspite.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }else{
                    // Codice per condividere un Bitmap su WhatsApp
                    String bitmapPath = MediaStore.Images.Media.insertImage(VisualizzaTesiOspite.this.getContentResolver(), bitmap, "Shared image", null);

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

                        if(intent.resolveActivity(VisualizzaTesiOspite.this.getPackageManager()) != null){
                            startActivity(intent);
                        }else{
                            Toast.makeText(VisualizzaTesiOspite.this, "Nessuna app email installata", Toast.LENGTH_SHORT).show();
                        }


                    }
                });
            }
        });

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
                                        Toast.makeText(VisualizzaTesiOspite.this,"Aggiunto alla classifica", Toast.LENGTH_SHORT).show();

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(VisualizzaTesiOspite.this,"Impossibile aggiungere alla classifica", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                });
    }
    public void esci(View view) {
        finish();
    }
}
