package es.studium.tusservi.ui.homeProfesional;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.studium.tusservi.R;
import es.studium.tusservi.servicio.Servicio;

public class RegistroEmpresaActivity extends AppCompatActivity {

    private ImageView imgEmpresa;
    private Bitmap bitmapSeleccionado = null;
    private EditText etNombre, etDescripcion, etUbicacion, etHorario, etWeb;
    private TextView txtServiciosResumen;
    private List<Servicio> listaServicios = new ArrayList<>();
    private int idProfesional; // lo obtendrás del usuario logueado

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_empresa);
        imgEmpresa = findViewById(R.id.imgEmpresa);

        findViewById(R.id.btnSeleccionarImagenEmpresa).setOnClickListener(v -> abrirGaleria());

        etNombre = findViewById(R.id.etNombre);
        etDescripcion = findViewById(R.id.etDescripcion);
        etUbicacion = findViewById(R.id.etUbicacion);
        etHorario = findViewById(R.id.etHorario);
        etWeb = findViewById(R.id.etWeb);
        txtServiciosResumen = findViewById(R.id.txtServiciosResumen);

        idProfesional = getIntent().getIntExtra("idProfesional", -1);

        findViewById(R.id.btnAddServicio).setOnClickListener(v -> abrirDialogServicio());
        findViewById(R.id.btnGuardarEmpresa).setOnClickListener(v -> guardarEmpresa());
    }
    private void abrirGaleria() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, 101); // Usa un requestCode diferente
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                bitmapSeleccionado = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imgEmpresa.setImageBitmap(bitmapSeleccionado);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void abrirDialogServicio() {
        DialogoServicio dialogo = new DialogoServicio(servicio -> {
            listaServicios.add(servicio);
            actualizarResumenServicios();
        });
        dialogo.show(getSupportFragmentManager(), "DialogoServicio");
    }

    private void actualizarResumenServicios() {
        StringBuilder resumen = new StringBuilder();
        for (Servicio s : listaServicios) {
            resumen.append("- ").append(s.getTituloServicio()).append(" (").append(s.getPrecioEstimadoServicio()).append("€)\n");
        }
        txtServiciosResumen.setText(resumen.toString());
    }

    private void guardarEmpresa() {
        // Limpiar errores anteriores
        etNombre.setError(null);
        etDescripcion.setError(null);
        etUbicacion.setError(null);

        String nombre = etNombre.getText().toString().trim();
        String descripcion = etDescripcion.getText().toString().trim();
        String ubicacion = etUbicacion.getText().toString().trim();
        String horario = etHorario.getText().toString().trim();
        String web = etWeb.getText().toString().trim();

        boolean error = false;

        if (nombre.isEmpty()) {
            etNombre.setError("El nombre es obligatorio");
            error = true;
        }
        if (descripcion.isEmpty()) {
            etDescripcion.setError("La descripción es obligatoria");
            error = true;
        }
        if (ubicacion.isEmpty()) {
            etUbicacion.setError("La ubicación es obligatoria");
            error = true;
        }

        if (error) {
            // No continuar con el guardado si hay error
            return;
        }

        String imagenBase64 = "";
        String nombreImagen = "";

        if (bitmapSeleccionado != null) {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmapSeleccionado.compress(Bitmap.CompressFormat.JPEG, 70, baos);
            byte[] imagenBytes = baos.toByteArray();
            imagenBase64 = Base64.encodeToString(imagenBytes, Base64.NO_WRAP);
            nombreImagen = "empresa_" + System.currentTimeMillis() + ".jpg";
        }

        String url = "http://10.0.2.2/TUSSERVI/insertEmpresas.php"; // cambia por tu ruta real

        RequestQueue queue = Volley.newRequestQueue(this);
        String finalImagenBase6 = imagenBase64;
        String finalNombreImagen = nombreImagen;
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            int idEmpresa = json.getInt("idEmpresa");
                            guardarServicios(idEmpresa); // Inserta los servicios ahora
                        } else {
                            Toast.makeText(this, "Error al guardar empresa", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                volleyError -> Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                if (!finalImagenBase6.isEmpty()) {
                    params.put("imagenBase64", finalImagenBase6);
                    params.put("nombreImagen", finalNombreImagen);
                }

                params.put("idProfesional", String.valueOf(idProfesional));
                params.put("nombre", nombre);
                params.put("descripcion", descripcion);
                params.put("ubicacion", ubicacion);
                params.put("horario", horario);
                params.put("web", web);
                return params;
            }
        };

        queue.add(request);
    }

    private void guardarServicios(int idEmpresa) {
        String url = "http://10.0.2.2/TUSSERVI/insertServicios.php"; // Cambia por tu URL real

        JSONArray serviciosArray = new JSONArray();
        for (Servicio s : listaServicios) {
            JSONObject obj = new JSONObject();
            try {
                obj.put("tituloServicio", s.getTituloServicio());
                obj.put("descripcionServicio", s.getDescripcionServicio());
                obj.put("precioEstimadoServicio", s.getPrecioEstimadoServicio());
                serviciosArray.put(obj);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        JSONObject postData = new JSONObject();
        try {
            postData.put("idEmpresa", idEmpresa);
            postData.put("servicios", serviciosArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, postData,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            Toast.makeText(this, "Empresa y servicios guardados", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            Toast.makeText(this, "Error al guardar servicios", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error al enviar servicios", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
        SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
        prefs.edit().putBoolean("empresaRegistrada", true).apply();
    }
}


