// SplashActivity.java
package es.studium.tusservi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = getSharedPreferences("session", MODE_PRIVATE);
        String tipoUsuario = preferences.getString("tipo_usuario", null);

        if (tipoUsuario != null) {
            // Sesión iniciada, redirige a MainActivity
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            // No hay sesión, redirige a LoginActivity
            Intent intent = new Intent(this, WelcomeActivity.class);
            startActivity(intent);
        }

        finish(); // Finaliza esta actividad para que no quede en el back stack
    }
}
