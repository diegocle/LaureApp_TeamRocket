package it.uniba.dib.sms222312.utenti.docenti;

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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import it.uniba.dib.sms222312.R;
import it.uniba.dib.sms222312.modelli.Tesi;
import it.uniba.dib.sms222312.utenti.studenti.HomeStudente;

public class VisualizzaTesiFragment extends Fragment {

    Bitmap bitmap;
    String docenta;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((HomeDocente) getActivity()).setToolbarTitle(getString(R.string.visualizzaTesi));


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

        //Gestione pulsanti
        Button classificaButton = view.findViewById(R.id.classifica);
        classificaButton.setVisibility(View.GONE);
        Button richiediButton = view.findViewById(R.id.richiesta);
        richiediButton.setVisibility(View.GONE);
        Button condividiButton = view.findViewById(R.id.condividi);
        Button modificaButton = view.findViewById(R.id.contatta);
        modificaButton.setText(getString(R.string.modifica));

        FirebaseAuth auth = FirebaseAuth.getInstance();
        String user = auth.getCurrentUser().getUid();

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
        textDurata.setText(durata+" ore");

        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection("utente").document(docente).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                docenta = documentSnapshot.getString("nome") + " " + documentSnapshot.getString("cognome");
                textDocente.setText(docenta);

            }
        });
        modificaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ModificaTesiFragment dialog = new ModificaTesiFragment();
                Bundle bundle = new Bundle();
                bundle.putString("Nome", nome);
                bundle.putString("Corso", corso);
                bundle.putString("Descrizione", descrizione);
                bundle.putString("Media", media);
                bundle.putString("Durata", durata);
                bundle.putString("Id", tesi.getIdTesi());

                dialog.setArguments(bundle);
                dialog.show(getActivity().getSupportFragmentManager(), "ModificaTesiFragment");

            }
        });

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
        return view;
    }
}