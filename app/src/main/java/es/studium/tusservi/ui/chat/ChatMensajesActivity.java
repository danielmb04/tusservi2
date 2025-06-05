package es.studium.tusservi.ui.chat;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.studium.tusservi.R;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.Response;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;





public class ChatMensajesActivity extends AppCompatActivity {

    private static final int PICK_MEDIA_REQUEST = 123;
    private static final int PERMISSION_REQUEST_CODE = 456;
    private int idEmisor, idReceptor;
    private RecyclerView recyclerMensajes;
    private EditText edtMensaje;
    private Button btnEnviar;
    private MensajeAdapter adapter;
    private List<Mensaje> listaMensajes = new ArrayList<>();

    private Handler handler = new Handler();
    private Runnable refrescarMensajesRunnable;
    private static final int INTERVALO_REFRESCO = 3000; // cada 3 segundos
    private ImageView imgFotoUsuario;
    private TextView txtNombreUsuario;
    private Button btnAdjuntar;


    private Uri mediaUriSeleccionada;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chat_mensajes);
        btnAdjuntar = findViewById(R.id.btnAdjuntar);


        imgFotoUsuario = findViewById(R.id.imgFotoReceptor);
        txtNombreUsuario = findViewById(R.id.txtNombreReceptor);

        recyclerMensajes = findViewById(R.id.recyclerMensajes);
        edtMensaje = findViewById(R.id.edtMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);

        idEmisor = getIntent().getIntExtra("idEmisor", -1);
        idReceptor = getIntent().getIntExtra("idReceptor", -1);

        recyclerMensajes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MensajeAdapter(ChatMensajesActivity.this, listaMensajes, idEmisor); // ✅

        recyclerMensajes.setAdapter(adapter);
        iniciarRefrescoAutomatico();
        btnEnviar.setOnClickListener(v -> enviarMensaje());
        btnAdjuntar.setOnClickListener(v -> checkPermissionAndAbrirGaleria());

        obtenerMensajes();
        cargarDatosUsuarioReceptor();


    }

    private void checkPermissionAndAbrirGaleria() {
        Log.d("ChatMensajesActivity", "checkPermissionAndAbrirGaleria() llamado");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // API 33
            int permiso = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES);
            Log.d("ChatMensajesActivity", "Permiso READ_MEDIA_IMAGES: " + permiso);
            if (permiso != PackageManager.PERMISSION_GRANTED) {
                Log.d("ChatMensajesActivity", "Permiso NO concedido, solicitando permiso...");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        PERMISSION_REQUEST_CODE);
            } else {
                Log.d("ChatMensajesActivity", "Permiso ya concedido, abriendo galería...");
                abrirGaleria();
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permiso = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
            Log.d("ChatMensajesActivity", "Permiso READ_EXTERNAL_STORAGE: " + permiso);
            if (permiso != PackageManager.PERMISSION_GRANTED) {
                Log.d("ChatMensajesActivity", "Permiso NO concedido, solicitando permiso...");
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_CODE);
            } else {
                Log.d("ChatMensajesActivity", "Permiso ya concedido, abriendo galería...");
                abrirGaleria();
            }
        } else {
            Log.d("ChatMensajesActivity", "SDK < M, abriendo galería sin permiso...");
            abrirGaleria();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d("ChatMensajesActivity", "onRequestPermissionsResult: requestCode=" + requestCode);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("ChatMensajesActivity", "Permiso concedido, abriendo galería...");
                abrirGaleria();
            } else {
                Log.d("ChatMensajesActivity", "Permiso denegado");
                Toast.makeText(this, "Permiso de almacenamiento denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void abrirGaleria() {
        Log.d("ChatMensajesActivity", "abrirGaleria() llamado");
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, PICK_MEDIA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_MEDIA_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();

            // Detectar tipo según mimeType
            String tipo = "imagen";
            String mimeType = getContentResolver().getType(uri);
            if (mimeType != null && mimeType.startsWith("video")) {
                tipo = "video";
            }

            subirArchivoConUploader(uri, tipo);
        }
    }


    // Método para obtener ruta real a partir de Uri
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        if (cursor != null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            String result = cursor.getString(column_index);
            cursor.close();
            return result;
        }
        return null;
    }


    private void subirArchivoConUploader(Uri uri, String tipo) {
        Uploader uploader = new Uploader();

        uploader.subirArchivo(this, uri, tipo, idEmisor, idReceptor, new Uploader.UploadCallback() {
            @Override
            public void onSuccess(String response) {
                runOnUiThread(() -> {
                    Toast.makeText(ChatMensajesActivity.this, "Archivo subido", Toast.LENGTH_SHORT).show();
                    obtenerMensajes();  // refresca mensajes para mostrar el archivo nuevo
                });
            }

            @Override
            public void onFailure(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(ChatMensajesActivity.this, "Error al subir archivo: " + error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }



    private void iniciarRefrescoAutomatico() {
        refrescarMensajesRunnable = new Runnable() {
            @Override
            public void run() {
                obtenerMensajes();
                handler.postDelayed(this, INTERVALO_REFRESCO);
            }
        };
        handler.post(refrescarMensajesRunnable);
    }
    private void obtenerMensajes() {
        String url = "http://10.0.2.2/TUSSERVI/obtenerMensajes.php?usuario1=" + idEmisor + "&usuario2=" + idReceptor;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    listaMensajes.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            int emisor = obj.getInt("emisor_id");
                            String texto = obj.getString("mensaje");
                            String fecha = obj.getString("fecha_envio");
                            String tipo = obj.getString("tipo");
                            listaMensajes.add(new Mensaje(emisor, texto, fecha, tipo));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                    recyclerMensajes.scrollToPosition(listaMensajes.size() - 1);
                },
                error -> Toast.makeText(this, "Error al cargar mensajes", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }


    private void enviarMensaje() {
        String texto = edtMensaje.getText().toString().trim();
        if (texto.isEmpty()) return;

        String url = "http://10.0.2.2/TUSSERVI/enviarMensaje.php";

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    edtMensaje.setText("");
                    obtenerMensajes();
                },
                error -> Toast.makeText(this, "Error al enviar", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("emisor_id", String.valueOf(idEmisor));
                params.put("receptor_id", String.valueOf(idReceptor));
                params.put("mensaje", texto);
                params.put("tipo", "texto");

                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }
    private void cargarDatosUsuarioReceptor() {
        String url = "http://10.0.2.2/TUSSERVI/obtenerUsuarioPorId.php?idUsuario=" + idReceptor;
        Log.d("ChatMensajesActivity", "URL para obtener usuario: " + url);


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String nombre = response.getString("nombreUsuario");
                        String urlFoto = response.getString("fotoPerfilUsuario");

                        txtNombreUsuario.setText(nombre);
                        // Para la foto usa alguna librería como Glide o Picasso:
                        Glide.with(this).load("http://10.0.2.2/TUSSERVI/" + urlFoto).into(imgFotoUsuario);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Log.e("ChatMensajesActivity", "Error al obtener usuario: " + error.toString());
                }
        );

        Volley.newRequestQueue(this).add(request);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(refrescarMensajesRunnable);
    }
}
