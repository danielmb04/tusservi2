package es.studium.tusservi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class LoginActivity extends AppCompatActivity {

    EditText etEmail, etPassword;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.editTextUsuario);
        etPassword = findViewById(R.id.editTextContrasena);
        btnLogin = findViewById(R.id.buttonEntrar);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            } else {
                iniciarSesion(email, password);
            }
        });
    }

    private void iniciarSesion(String email, String password) {
        new Thread(() -> {
            try {
                URL url = new URL("http://10.0.2.2/TUSSERVI/login.php"); // Cambia esto por la URL real
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                String parametros = "email=" + URLEncoder.encode(email, "UTF-8")
                        + "&password=" + URLEncoder.encode(password, "UTF-8");

                OutputStream os = conn.getOutputStream();
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
                writer.write(parametros);
                writer.flush();
                writer.close();
                os.close();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder resultado = new StringBuilder();
                    String linea;
                    while ((linea = reader.readLine()) != null) {
                        resultado.append(linea);
                    }
                    reader.close();

                    JSONObject respuesta = new JSONObject(resultado.toString());
                    boolean success = respuesta.getBoolean("success");

                    if (success) {
                        JSONObject usuario = respuesta.getJSONObject("usuario");
                        String nombre = usuario.getString("nombre");
                        String tipo = usuario.getString("tipo");
                        String idProfesional = usuario.optString("idProfesional", "");
                        String idUsuario = usuario.optString("idUsuario", "");

                        // 🔐 GUARDAR DATOS EN SharedPreferences
                        SharedPreferences preferences = getSharedPreferences("session", MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("nombre", nombre);
                        editor.putString("tipo_usuario", tipo);
                        editor.putString("idProfesional", idProfesional);
                        editor.putString("idUsuario",idUsuario);

                        editor.apply();
                        runOnUiThread(() -> {
                            Toast.makeText(LoginActivity.this, "Bienvenido " + nombre, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            intent.putExtra("nombreUsuario", nombre);
                            intent.putExtra("tipoUsuario", tipo);
                            intent.putExtra("idProfesional", idProfesional);
                            intent.putExtra("idUsuario", idUsuario);
                            startActivity(intent);
                            finish();

                        });
                    } else {
                        String mensaje = respuesta.getString("message");
                        runOnUiThread(() -> Toast.makeText(LoginActivity.this, mensaje, Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(LoginActivity.this, "Error en el servidor", Toast.LENGTH_SHORT).show());
                }

                conn.disconnect();

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() ->
                        Toast.makeText(LoginActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }
}