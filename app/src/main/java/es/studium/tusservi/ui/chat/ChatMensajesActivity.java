package es.studium.tusservi.ui.chat;

import android.os.Bundle;
import android.os.Handler;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.studium.tusservi.R;

public class ChatMensajesActivity extends AppCompatActivity {

    private int idEmisor, idReceptor;
    private RecyclerView recyclerMensajes;
    private EditText edtMensaje;
    private Button btnEnviar;
    private MensajeAdapter adapter;
    private List<Mensaje> listaMensajes = new ArrayList<>();

    private Handler handler = new Handler();
    private Runnable refrescarMensajesRunnable;
    private static final int INTERVALO_REFRESCO = 3000; // cada 3 segundos


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_mensajes);

        recyclerMensajes = findViewById(R.id.recyclerMensajes);
        edtMensaje = findViewById(R.id.edtMensaje);
        btnEnviar = findViewById(R.id.btnEnviar);

        idEmisor = getIntent().getIntExtra("idEmisor", -1);
        idReceptor = getIntent().getIntExtra("idReceptor", -1);

        recyclerMensajes.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MensajeAdapter(listaMensajes, idEmisor);
        recyclerMensajes.setAdapter(adapter);
        iniciarRefrescoAutomatico();
        btnEnviar.setOnClickListener(v -> enviarMensaje());

        obtenerMensajes();


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
                            listaMensajes.add(new Mensaje(emisor, texto, fecha));
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
                return params;
            }
        };

        Volley.newRequestQueue(this).add(request);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(refrescarMensajesRunnable);
    }
}
