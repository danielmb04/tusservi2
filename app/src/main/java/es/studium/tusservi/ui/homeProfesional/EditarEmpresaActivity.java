package es.studium.tusservi.ui.homeProfesional;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import es.studium.tusservi.R;
import es.studium.tusservi.empresa.Empresa;
import es.studium.tusservi.servicio.Servicio;

public class EditarEmpresaActivity extends AppCompatActivity {

    private static final int SELECT_IMAGE_REQUEST = 1;

    EditText etNombre, etDescripcion, etUbicacion, etHorario, etWeb;
    Button btnGuardar, btnSeleccionarLogo;
    ImageView ivLogo;
    Empresa empresa;

    String logoBase64 = null; // Aquí guardamos la imagen codificada en base64

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_empresa);

        Button btnEditarServicios = findViewById(R.id.btnEditarServicios);
        btnEditarServicios.setOnClickListener(v -> abrirDialogEditarServicios());

        etNombre = findViewById(R.id.etNombre);
        etDescripcion = findViewById(R.id.etDescripcion);
        etUbicacion = findViewById(R.id.etUbicacion);
        etHorario = findViewById(R.id.etHorario);
        etWeb = findViewById(R.id.etWeb);
        ivLogo = findViewById(R.id.ivLogo);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnSeleccionarLogo = findViewById(R.id.btnSeleccionarLogo);

        empresa = (Empresa) getIntent().getSerializableExtra("empresa");

        if (empresa != null) {
            etNombre.setText(empresa.getNombre());
            etDescripcion.setText(empresa.getDescripcion());
            etUbicacion.setText(empresa.getUbicacion());
            etHorario.setText(empresa.getHorario());
            etWeb.setText(empresa.getWeb());

            String logoUrl = empresa.getLogo();
            if (logoUrl != null && !logoUrl.isEmpty()) {
                if (!logoUrl.startsWith("http")) {
                    logoUrl = "http://10.0.2.2/TUSSERVI/uploads/" + logoUrl;
                }
                logoUrl += "?t=" + System.currentTimeMillis(); // evitar caché

                Glide.with(this)
                        .load(logoUrl)
                        .placeholder(R.drawable.ic_launcher_background)
                        .error(R.drawable.ic_launcher_background)

                        .into(ivLogo);
            } else {
                ivLogo.setImageResource(R.drawable.ic_launcher_background);
            }
}

            // Aquí deberías cargar la imagen del logo si existe, por ejemplo con Glide o similar
        // Para ejemplo rápido, si tienes la URL:
        // Glide.with(this).load(empresa.getLogo()).into(ivLogo);


        btnSeleccionarLogo.setOnClickListener(v -> seleccionarImagen());

        btnGuardar.setOnClickListener(v -> guardarCambios());
    }
    private void abrirDialogEditarServicios() {
        // Abre una nueva Activity o DialogFragment donde el usuario pueda ver, editar o eliminar servicios
        Intent intent = new Intent(this, EditarServiciosActivity.class);
        intent.putExtra("idEmpresa", empresa.getId());
        startActivity(intent);
    }

    private void seleccionarImagen() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, SELECT_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SELECT_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            Uri imageUri = data.getData();

            try {
                InputStream is = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                ivLogo.setImageBitmap(bitmap);
                logoBase64 = encodeImageToBase64(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al seleccionar imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String encodeImageToBase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // Comprime en PNG, puedes cambiar a JPEG si prefieres
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();
        return "data:image/png;base64," + Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void guardarCambios() {
        String url = "http://10.0.2.2/TUSSERVI/editarEmpresa.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonResponse = new JSONObject(response);
                        boolean success = jsonResponse.getBoolean("success");
                        String message = jsonResponse.getString("message");

                        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                        if (success) {
                            // Actualiza los campos de la empresa
                            empresa.setNombre(etNombre.getText().toString());
                            empresa.setDescripcion(etDescripcion.getText().toString());
                            empresa.setUbicacion(etUbicacion.getText().toString());
                            empresa.setHorario(etHorario.getText().toString());
                            empresa.setWeb(etWeb.getText().toString());

                            if (jsonResponse.has("logoPath")) {
                                String logoPath = jsonResponse.isNull("logoPath") ? null : jsonResponse.getString("logoPath");

                                if (logoPath != null && !logoPath.isEmpty()) {
                                    empresa.setLogo(logoPath);
                                    String urlCompletaLogo = "http://10.0.2.2/TUSSERVI/uploads/" + logoPath + "?t=" + System.currentTimeMillis();
                                    Log.d("btnGuardar", "URL logo: " + urlCompletaLogo);

                                    Glide.with(this)
                                            .load(urlCompletaLogo)
                                            .placeholder(R.drawable.ic_launcher_background)
                                            .error(R.drawable.ic_launcher_background)
                                            .into(ivLogo);
                                }
                            }

                            // 🔁 Devolver la empresa modificada
                            Intent returnIntent = new Intent();
                            returnIntent.putExtra("empresaActualizada", empresa);
                            setResult(RESULT_OK, returnIntent);
                            finish();
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error en respuesta del servidor", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("idEmpresa", String.valueOf(empresa.getId()));
                params.put("nombreEmpresa", etNombre.getText().toString());
                params.put("descripcionEmpresa", etDescripcion.getText().toString());
                params.put("ubicacionEmpresa", etUbicacion.getText().toString());
                params.put("horarioEmpresa", etHorario.getText().toString());
                params.put("webEmpresa", etWeb.getText().toString());

                if (logoBase64 != null) {
                    params.put("logoEmpresa", logoBase64);
                }

                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

}
