package it.uniba.dib.sms222312.utenti.studenti;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Stack;

import it.uniba.dib.sms222312.registrazione.Login;
import it.uniba.dib.sms222312.R;

public class HomeStudente extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;
    private Stack<String> titleStack;
    private MaterialToolbar toolbar;
    private HomeStudenteFragment homeFragment = new HomeStudenteFragment();
    private ProfiloStudenteFragment profiloFragment = new ProfiloStudenteFragment();
    private ListaRicevimentiStudenteFragment ricevimentiStudente = new ListaRicevimentiStudenteFragment();

    private ListaTaskFragment tesiFragment = new ListaTaskFragment();
    private boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Set the toolbar as the action bar
        toolbar = findViewById(R.id.topAppBar);
        toolbar.setNavigationIcon(R.drawable.icona_home);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        onCreateOptionsMenu(toolbar.getMenu());

        // Crea una nuova pila per i titoli della toolbar
        titleStack = new Stack<>();
        getSupportActionBar().setTitle("LaureApp");



        bottomNavigationView=findViewById(R.id.bottom_navigation);
        bottomNavigationView.inflateMenu(R.menu.bottonmnav_studente);
        getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.page_1:
                        flag = true;
                        getSupportFragmentManager().beginTransaction().replace(R.id.container,homeFragment).commit();
                        return true;
                    case R.id.page_2:
                        if(bottomNavigationView.getSelectedItemId()==R.id.page_2) return true;
                        if(flag)
                            getSupportFragmentManager().beginTransaction().replace(R.id.container,tesiFragment).addToBackStack(null).commit();
                        flag=true;
                        return true;
                    case R.id.page_3:
                        if(bottomNavigationView.getSelectedItemId()==R.id.page_3) return true;
                        if(flag)
                            getSupportFragmentManager().beginTransaction().replace(R.id.container,ricevimentiStudente).addToBackStack(null).commit();
                        flag=true;
                        return true;
                }
                return false;
            }
        });
    }

    public void logout(View view){
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(HomeStudente.this, Login.class));
        finish();
    }


    @Override
    public void onBackPressed() {

        // Ripristina il titolo precedente dalla pila
        if (!titleStack.isEmpty()) {
            if(titleStack.peek().equals("LaureApp")){
                toolbar.setTitle(titleStack.peek());
                toolbar.setNavigationIcon(R.drawable.icona_home);
            }
            if(titleStack.peek().equals("LaureApp")){
                toolbar.setTitle(titleStack.pop());
            }
        }
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.container);
        if (currentFragment != null && currentFragment.getClass().getName().equals(HomeStudenteFragment.class.getName())) {
            finish();
        }else{
            super.onBackPressed();
        }

    }
    // Imposta il titolo della toolbar
    public void setToolbarTitle(String title) {
        Log.d("setToolbarTitle",titleStack.toString());
        // Aggiungi il titolo precedente alla pila
        titleStack.push(toolbar.getTitle().toString());
        if(!title.equals("LaureApp")) {
            toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);

        }
        // Imposta il nuovo titolo
        toolbar.setTitle(title);

    }
    public void setToolBarEmpty(){
        titleStack.removeAllElements();
        getSupportActionBar().setTitle("LaureApp");
        toolbar.setNavigationIcon(R.drawable.icona_home);
    }

    // Gestisci il click del pulsante back nella toolbar
    @Override
    public boolean onSupportNavigateUp() {
        Log.d("onSupportNavigateUp ",titleStack.toString());
        if(titleStack.isEmpty()) return false;

        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void setflag(boolean b) {
        flag= b;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        if(toolbar.getTitle().equals(getResources().getString(R.string.profilo)))  return super.onOptionsItemSelected(item);
        switch (itemId) {
            case R.id.profilo:
                getSupportFragmentManager().beginTransaction().replace(R.id.container,profiloFragment).addToBackStack(null).commit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}