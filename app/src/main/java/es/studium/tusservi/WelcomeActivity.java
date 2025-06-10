
package es.studium.tusservi;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
/**
 * Clase WelcomeActivity
 *
 * Esta actividad representa la pantalla de bienvenida de la aplicación TusServi.
 * Se muestra al iniciar la app por primera vez (antes del login) y permite al usuario
 * elegir entre iniciar sesión o registrarse como nuevo usuario.
 *
 * Proporciona dos botones:
 * - Iniciar sesión: Dirige a LoginActivity.
 * - Crear cuenta: Dirige a RegistroActivity.
 *
 * Hereda de AppCompatActivity para compatibilidad con versiones antiguas de Android.
 */
public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button btnLogin = findViewById(R.id.buttonIniciarSesion);
        Button btnRegistro = findViewById(R.id.buttonCrearCuenta);

        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(WelcomeActivity.this, LoginActivity.class));
        });

        btnRegistro.setOnClickListener(v -> {
            startActivity(new Intent(WelcomeActivity.this, RegistroActivity.class));
        });
    }
}

