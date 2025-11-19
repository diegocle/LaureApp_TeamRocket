package it.uniba.dib.sms222312;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.widget.CompoundButton;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.android.material.switchmaterial.SwitchMaterial;

public class GestioneTema {
    private Context context;
    private static final String PREF_THEME_MODE = "pref_theme_mode";
    private static final int THEME_LIGHT = 1;
    private static final int THEME_DARK = 2;
    public GestioneTema(Context context){
        this.context = context;
    }


    public void setSwitch( SwitchMaterial btnChangeTheme, String tn, String tc) {
        if(AppCompatDelegate.getDefaultNightMode()==AppCompatDelegate.MODE_NIGHT_YES) {
            btnChangeTheme.setText(tn);
            btnChangeTheme.setChecked(true);
        }else{
            btnChangeTheme.setText(tc);
            btnChangeTheme.setChecked(false);
        }

        btnChangeTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                    buttonView.setText(tn);
                    saveThemeMode(THEME_DARK);
                }else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                    buttonView.setText(tc);
                    saveThemeMode(THEME_LIGHT);
                }
            }
        });

    }
    public void setThema(){
        if(getSavedThemeMode() == THEME_DARK){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
    private int getSavedThemeMode() {
        return context.getSharedPreferences("theme_prefs", MODE_PRIVATE).getInt(PREF_THEME_MODE, THEME_LIGHT);
    }

    private void saveThemeMode(int themeMode) {
        context.getSharedPreferences("theme_prefs", MODE_PRIVATE)
                .edit()
                .putInt(PREF_THEME_MODE, themeMode)
                .apply();
    }
}
