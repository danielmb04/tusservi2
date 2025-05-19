package es.studium.tusservi;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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

public class RegistroActivity extends AppCompatActivity {

    ImageView imgPerfil;
    Bitmap bitmapSeleccionado = null;
    RadioGroup radioGroupTipo;
    RadioButton radioCliente, radioProfesional;
    View layoutProfesional;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

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

    void enviarDatosAlServidor() {
        String nombre = ((EditText) findViewById(R.id.etNombre)).getText().toString().trim();
        String email = ((EditText) findViewById(R.id.etEmail)).getText().toString().trim();
        String password = ((EditText) findViewById(R.id.etPassword)).getText().toString().trim();
        String telefono = ((EditText) findViewById(R.id.etTelefono)).getText().toString().trim();
        String direccion = ((EditText) findViewById(R.id.etDireccion)).getText().toString().trim();
        String ciudad = ((EditText) findViewById(R.id.etCiudad)).getText().toString().trim();

        // Obtener el tipo de usuario seleccionado
        String tipoUsuario = radioCliente.isChecked() ? "cliente" : radioProfesional.isChecked() ? "profesional" : "";

        // Campos específicos de profesional (si aplica)
        String categoria = "", descripcion = "", experiencia = "", horario = "", ubicacion = "", redesSociales = "";
        if (tipoUsuario.equals("profesional")) {
            categoria = ((EditText) findViewById(R.id.etCategoria)).getText().toString().trim();
            descripcion = ((EditText) findViewById(R.id.etDescripcion)).getText().toString().trim();
            experiencia = ((EditText) findViewById(R.id.etExperiencia)).getText().toString().trim();
            horario = ((EditText) findViewById(R.id.etHorario)).getText().toString().trim();
            ubicacion = ((EditText) findViewById(R.id.etUbicacion)).getText().toString().trim();
            redesSociales = ((EditText) findViewById(R.id.etRedesSociales)).getText().toString().trim();
        }

        String imagenBase64 = "";
        if (bitmapSeleccionado != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapSeleccionado.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] imagenBytes = baos.toByteArray();
            imagenBase64 = Base64.encodeToString(imagenBytes, Base64.DEFAULT);
        }

        String url = "https://tu-servidor.com/api/registro.php";

        String finalImagenBase64 = imagenBase64;
        String finalCategoria = categoria;
        String finalDescripcion = descripcion;
        String finalExperiencia = experiencia;
        String finalHorario = horario;
        String finalUbicacion = ubicacion;
        String finalRedesSociales = redesSociales;
        String finalTipoUsuario = tipoUsuario;

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(getApplicationContext(), "Registro exitoso", Toast.LENGTH_SHORT).show();
                    // Aquí puedes limpiar campos o redirigir
                },
                error -> Toast.makeText(getApplicationContext(), "Error: " + error.toString(), Toast.LENGTH_LONG).show()) {

            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("nombre", nombre);
                params.put("email", email);
                params.put("password", password);
                params.put("telefono", telefono);
                params.put("direccion", direccion);
                params.put("ciudad", ciudad);
                params.put("tipoUsuario", finalTipoUsuario);

                if (finalTipoUsuario.equals("profesional")) {
                    params.put("categoria", finalCategoria);
                    params.put("descripcion", finalDescripcion);
                    params.put("experiencia", finalExperiencia);
                    params.put("horario", finalHorario);
                    params.put("ubicacion", finalUbicacion);
                    params.put("redesSociales", finalRedesSociales);
                }

                params.put("imagen", finalImagenBase64);

                return params;
            }
        };

        Volley.newRequestQueue(this).add(stringRequest);
    }
}
