package it.uniba.dib.sms222312.utenti.docenti;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import it.uniba.dib.sms222312.R;

public class ClickRicevimentoTesistaActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_click_ricevimento_tesista);

        String task = getIntent().getStringExtra("TaskTesi");
        String data = getIntent().getStringExtra("Data");
        String dettagli = getIntent().getStringExtra("Dettagli");

        TextView txtTask = findViewById(R.id.taskTesi);
        TextView txtDettagli = findViewById(R.id.dettagli);
        TextView txtData = findViewById(R.id.data);

        txtTask.setText(task);
        txtDettagli.setText(dettagli);
        txtData.setText(data);
    }
}