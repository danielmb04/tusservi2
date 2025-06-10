package es.studium.tusservi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
/**
 * Clase RegistroActivity
 *
 * Esta actividad permite a los nuevos usuarios registrarse en la aplicación TusServi.
 * Ofrece dos tipos de registro:
 *  - Cliente: registra nombre, email, contraseña, teléfono, dirección, ciudad e imagen de perfil.
 *  - Profesional: además de los datos anteriores, se solicitan categoría profesional y experiencia.
 *
 * Permite seleccionar una imagen de perfil desde la galería y subir los datos al servidor mediante Volley.
 *
 * Hereda de AppCompatActivity.
 */

public class RegistroActivity extends AppCompatActivity {

    ImageView imgPerfil;
    Bitmap bitmapSeleccionado = null;
    RadioGroup radioGroupTipo;
    RadioButton radioCliente, radioProfesional;
    View layoutProfesional;
    Button btnRegistrarse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
btnRegistrarse = findViewById(R.id.btnRegistrarse);
        imgPerfil = findViewById(R.id.imgPerfil);
        radioGroupTipo = findViewById(R.id.radioGroupTipo);
        radioCliente = findViewById(R.id.radioCliente);
        radioProfesional = findViewById(R.id.radioProfesional);
        layoutProfesional = findViewById(R.id.layoutProfesional);

        // Listener para cambiar visibilidad al seleccionar tipo usuario
        radioGroupTipo.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioProfesional) {
                layoutProfesional.setVisibility(View.VISIBLE);
            } else {
                layoutProfesional.setVisibility(View.GONE);
            }
        });

        Button btnSeleccionarImagen = findViewById(R.id.btnSeleccionarImagen);
        btnSeleccionarImagen.setOnClickListener(v -> abrirGaleria());

        Button btnRegistrarse = findViewById(R.id.btnRegistrarse);
        btnRegistrarse.setOnClickListener(v -> enviarDatosAlServidor());


    }

    void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                bitmapSeleccionado = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
            } catch (IOException e) {
                e.printStackTrace();
            }
            imgPerfil.setImageBitmap(bitmapSeleccionado);
        }
    }

    // Función de validación de contraseña
    boolean esPasswordValida(String password) {
        // Mínimo 8 caracteres, al menos una mayúscula, una minúscula, un número y un carácter especial
        String regex = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&+=!]).{8,}$";
        return password.matches(regex);
    }

    void enviarDatosAlServidor() {
        String nombre = ((EditText) findViewById(R.id.etNombre)).getText().toString().trim();
        String email = ((EditText) findViewById(R.id.etEmail)).getText().toString().trim();
        String password = ((EditText) findViewById(R.id.etPassword)).getText().toString().trim();
        String telefono = ((EditText) findViewById(R.id.etTelefono)).getText().toString().trim();
        String direccion = ((EditText) findViewById(R.id.etDireccion)).getText().toString().trim();
        String ciudad = ((EditText) findViewById(R.id.etCiudad)).getText().toString().trim();
        String tipoUsuario = radioCliente.isChecked() ? "cliente" : radioProfesional.isChecked() ? "profesional" : "";

        String categoria = "", experiencia = "";
        if (tipoUsuario.equals("profesional")) {
            categoria = ((EditText) findViewById(R.id.etCategoria)).getText().toString().trim();
            experiencia = ((EditText) findViewById(R.id.etExperiencia)).getText().toString().trim();
        }

        boolean hayError = false;
        if (nombre.isEmpty()) {
            ((EditText) findViewById(R.id.etNombre)).setError("Nombre requerido");
            hayError = true;
        }
        if (email.isEmpty()) {
            ((EditText) findViewById(R.id.etEmail)).setError("Email requerido");
            hayError = true;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            ((EditText) findViewById(R.id.etEmail)).setError("Introduce un email válido");
            hayError = true;
        }
        if (password.isEmpty()) {
            ((EditText) findViewById(R.id.etPassword)).setError("Contraseña requerida");
            hayError = true;
        } else if (!esPasswordValida(password)) {
            ((EditText) findViewById(R.id.etPassword)).setError("La contraseña debe tener al menos 8 caracteres, una mayúscula, una minúscula, un número y un carácter especial");
            hayError = true;
        }

        if (tipoUsuario.isEmpty()) {
            Toast.makeText(this, "Selecciona un tipo de usuario", Toast.LENGTH_SHORT).show();
            hayError = true;
        }
        if (tipoUsuario.equals("profesional")) {
            if (categoria.isEmpty()) {
                ((EditText) findViewById(R.id.etCategoria)).setError("Categoría requerida");
                hayError = true;
            }
            if (experiencia.isEmpty()) {
                ((EditText) findViewById(R.id.etExperiencia)).setError("Experiencia requerida");
                hayError = true;
            }
        }

        if (hayError) return;

        String imagenBase64 = "";
        String nombreImagen = "";
        if (bitmapSeleccionado != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapSeleccionado.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] imagenBytes = baos.toByteArray();
            imagenBase64 = Base64.encodeToString(imagenBytes, Base64.NO_WRAP);
            nombreImagen = "perfil_" + System.currentTimeMillis() + ".jpg";
        }

        String url = "http://10.0.2.2/TUSSERVI/registroUsuario.php";
        Map<String, String> parametros = new HashMap<>();
        parametros.put("nombreUsuario", nombre);
        parametros.put("emailUsuario", email);
        parametros.put("contraseñaUsuario", password);
        parametros.put("telefonoUsuario", telefono);
        parametros.put("direccionUsuario", direccion);
        parametros.put("ciudadUsuario", ciudad);
        parametros.put("tipoUsuario", tipoUsuario);

        if (!imagenBase64.isEmpty()) {
            parametros.put("imagenBase64", imagenBase64);
            parametros.put("nombreImagen", nombreImagen);
            parametros.put("fotoPerfilUsuario", imagenBase64);
            parametros.put("fotoPerfilProfesional", imagenBase64);
        }

        if (tipoUsuario.equals("profesional")) {
            parametros.put("categoriaProfesional", categoria);
            parametros.put("experienciaProfesional", experiencia);
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    if (response.trim().equalsIgnoreCase("success")) {
                        Toast.makeText(RegistroActivity.this, "Registro completado con éxito", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(RegistroActivity.this, "" + response, Toast.LENGTH_LONG).show();
                    }
                    finish();
                },
                error -> Toast.makeText(RegistroActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                return parametros;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }

}
