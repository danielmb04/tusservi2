package es.studium.tusservi.ui.profile;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import es.studium.tusservi.R;

public class EditarPerfilProfesionalActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    EditText editNombreUsuario, editEmailUsuario, editContrasenaUsuario, editTelefonoUsuario,
            editDireccionUsuario, editCiudadUsuario, editCategoriaProfesional, editExperienciaProfesional;
    ImageView imgFotoPerfil;
    Button btnCambiarFoto, btnGuardarCambios;
    Uri imagenSeleccionadaUri;
    private Bitmap bitmapSeleccionado = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil_profesional);

        // Enlazar vistas
        editNombreUsuario = findViewById(R.id.editNombreUsuario);
        editEmailUsuario = findViewById(R.id.editEmailUsuario);
        editContrasenaUsuario = findViewById(R.id.editContrasenaUsuario);
        editTelefonoUsuario = findViewById(R.id.editTelefonoUsuario);
        editDireccionUsuario = findViewById(R.id.editDireccionUsuario);
        editCiudadUsuario = findViewById(R.id.editCiudadUsuario);
        editCategoriaProfesional = findViewById(R.id.editCategoriaProfesional);
        editExperienciaProfesional = findViewById(R.id.editExperienciaProfesional);
        imgFotoPerfil = findViewById(R.id.imgFotoPerfil);
        btnCambiarFoto = findViewById(R.id.btnCambiarFoto);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);

        int idProfesional = Integer.parseInt(obtenerIdUsuarioDesdePreferencias());
        cargarDatosPerfil(idProfesional);

        btnGuardarCambios.setOnClickListener(v -> guardarCambios(idProfesional, bitmapSeleccionado));

        btnCambiarFoto.setOnClickListener(v -> abrirGaleria());
    }

    private String obtenerIdUsuarioDesdePreferencias() {
        SharedPreferences prefs = getSharedPreferences("session", Context.MODE_PRIVATE);
        String tipo = prefs.getString("tipo_usuario", "");
        String id = "";

        if ("profesional".equals(tipo)) {
            id = prefs.getString("idProfesional", "");
        } else if ("cliente".equals(tipo)) {
            id = prefs.getString("idUsuario", "");
        }

        Log.d("DEBUG", "Tipo usuario: " + tipo + ", ID: " + id);
        return id;
    }

    private void cargarDatosPerfil(int idProfesional) {
        String url = "http://10.0.2.2/TUSSERVI/obtenerDatosProfesional.php?idProfesional=" + idProfesional;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        editNombreUsuario.setText(response.getString("nombreUsuario"));
                        editEmailUsuario.setText(response.getString("emailUsuario"));
                        editContrasenaUsuario.setText(response.getString("contraseñaUsuario"));
                        editTelefonoUsuario.setText(response.getString("telefonoUsuario"));
                        editDireccionUsuario.setText(response.getString("direccionUsuario"));
                        editCiudadUsuario.setText(response.getString("ciudadUsuario"));
                        editCategoriaProfesional.setText(response.getString("categoriaProfesional"));
                        editExperienciaProfesional.setText(String.valueOf(response.getInt("experienciaProfesional")));

                        String nombreFoto = response.getString("fotoPerfilProfesional");
                        if (nombreFoto != null && !nombreFoto.isEmpty()) {
                            String fotoUrl = "http://10.0.2.2/TUSSERVI/uploads/" + nombreFoto + "?t=" + System.currentTimeMillis();
                            Log.d("EditarPerfil", "Cargando imagen desde URL: " + fotoUrl);

                            Glide.with(EditarPerfilProfesionalActivity.this)
                                    .load(fotoUrl)
                                    .placeholder(R.drawable.ic_launcher_background)
                                    .error(R.drawable.ic_launcher_foreground)
                                    .into(imgFotoPerfil);
                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error al obtener datos del perfil", Toast.LENGTH_SHORT).show();
                    Log.e("VolleyError", error.toString());
                });

        queue.add(request);
    }

    private String convertirBitmapABase64(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] bytes = baos.toByteArray();
        return "data:image/jpeg;base64," + Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    private void guardarCambios(int idProfesional, Bitmap imagenPerfilBitmap) {
        String url = "http://10.0.2.2/TUSSERVI/actualizarDatosProfesional.php";

        int experiencia;
        try {
            experiencia = Integer.parseInt(editExperienciaProfesional.getText().toString());
        } catch (NumberFormatException e) {
            Toast.makeText(this, "La experiencia debe ser un número", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject datos = new JSONObject();
        try {
            datos.put("idProfesional", idProfesional);
            datos.put("nombreUsuario", editNombreUsuario.getText().toString());
            datos.put("emailUsuario", editEmailUsuario.getText().toString());
            datos.put("contraseñaUsuario", editContrasenaUsuario.getText().toString());
            datos.put("telefonoUsuario", editTelefonoUsuario.getText().toString());
            datos.put("direccionUsuario", editDireccionUsuario.getText().toString());
            datos.put("ciudadUsuario", editCiudadUsuario.getText().toString());
            if(imagenPerfilBitmap != null) {
                String base64Imagen = convertirBitmapABase64(imagenPerfilBitmap);
                datos.put("fotoPerfilProfesional", base64Imagen);
            } else {
                datos.put("fotoPerfilProfesional", JSONObject.NULL);
            }
            datos.put("categoriaProfesional", editCategoriaProfesional.getText().toString());
            datos.put("experienciaProfesional", experiencia);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al preparar los datos", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, datos,
                response -> {
                    Toast.makeText(this, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show();
                    finish();
                },
                error -> {
                    Toast.makeText(this, "Error al actualizar perfil", Toast.LENGTH_SHORT).show();
                    Log.e("VolleyError", error.toString());
                });

        queue.add(request);
    }

    private void abrirGaleria() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imagenSeleccionadaUri = data.getData();
            try {
                bitmapSeleccionado = MediaStore.Images.Media.getBitmap(getContentResolver(), imagenSeleccionadaUri);
                imgFotoPerfil.setImageBitmap(bitmapSeleccionado);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al cargar imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
