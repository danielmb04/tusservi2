package es.studium.tusservi.empresa;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.studium.tusservi.R;
import es.studium.tusservi.servicio.Servicio;
import es.studium.tusservi.servicio.ServicioAdapter;
import es.studium.tusservi.ui.chat.ChatFragment;
import es.studium.tusservi.ui.chat.ChatMensajesActivity;
import es.studium.tusservi.empresa.Profesional;
import es.studium.tusservi.empresa.ProfesionalAdapter;

public class DetalleEmpresaActivity extends AppCompatActivity {

    TextView txtNombre, txtDescripcion, txtUbicacion, txtHorario, txtWeb, txtCategoria, txtExperiencia;
    ImageView imgLogo;
    RecyclerView recyclerServicios;
    ServicioAdapter servicioAdapter;
    List<Servicio> listaServicios = new ArrayList<>();
    int idEmpresaFK;
    Button btnContactar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_empresa);
        RecyclerView recyclerProfesionales = findViewById(R.id.recyclerProfesionales);
        recyclerProfesionales.setLayoutManager(new LinearLayoutManager(this));
        List<Profesional> listaProfesionales = new ArrayList<>();
        ProfesionalAdapter profesionalAdapter = new ProfesionalAdapter(listaProfesionales, profesional -> {
            SharedPreferences prefs = getSharedPreferences("session", MODE_PRIVATE);
            String idClienteStr = prefs.getString("idUsuario", "-1");
            int idCliente = Integer.parseInt(idClienteStr);


            // o como tengas guardado el ID del usuario actual
            int idProfesionalUsuario = profesional.getIdUsuario(); // este debe ser el ID de la tabla Usuarios
            Log.d("DEBUG", "idCliente: " + idCliente + ", idUsuarioProfesional: " + idProfesionalUsuario);

            if (idCliente == -1 || idProfesionalUsuario == -1) {
                Toast.makeText(this, "IDs inválidos", Toast.LENGTH_SHORT).show();
                return;
            }

            Intent intentChat = new Intent(DetalleEmpresaActivity.this, ChatMensajesActivity.class);
            intentChat.putExtra("idEmisor", idCliente);
            intentChat.putExtra("idReceptor", idProfesionalUsuario);
            startActivity(intentChat);
        });



        recyclerProfesionales.setAdapter(profesionalAdapter);




        txtNombre = findViewById(R.id.txtNombreEmpresa);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        txtUbicacion = findViewById(R.id.txtUbicacion);
        txtHorario = findViewById(R.id.txtHorario);
        txtWeb = findViewById(R.id.txtWeb);
        txtCategoria = findViewById(R.id.txtCategoria);
        txtExperiencia = findViewById(R.id.txtExperiencia);
        imgLogo = findViewById(R.id.imgLogo);
        recyclerServicios = findViewById(R.id.recyclerServicios);
        recyclerServicios.setLayoutManager(new LinearLayoutManager(this));

        // Obtener los datos de la empresa del Intent
        Intent intent = getIntent();
        idEmpresaFK = intent.getIntExtra("idEmpresaFK", 0);
        txtNombre.setText(intent.getStringExtra("nombre"));
        txtDescripcion.setText(intent.getStringExtra("descripcion"));
        txtUbicacion.setText(intent.getStringExtra("ubicacion"));
        txtHorario.setText(intent.getStringExtra("horario"));
        txtWeb.setText(intent.getStringExtra("web"));
        txtCategoria.setText(intent.getStringExtra("categoria"));
        txtExperiencia.setText(String.valueOf(intent.getIntExtra("experiencia", 0)));



        // Mostrar el logo con Glide
        String logo = intent.getStringExtra("logo");
        if (logo != null && !logo.isEmpty()) {
            String urlLogo = "http://10.0.2.2/TUSSERVI/uploads/" + logo + "?t=" + System.currentTimeMillis();
            Log.d("DetalleEmpresa", "Cargando logo desde URL: " + urlLogo);
            Glide.with(this)
                    .load(urlLogo)
                    .placeholder(R.drawable.ic_launcher_background) // Cambia si tienes un drawable de carga
                    .error(R.drawable.ic_launcher_foreground)       // Cambia si tienes un drawable de error
                    .into(imgLogo);
        }

        Toast.makeText(this, "ID empresa: " + idEmpresaFK, Toast.LENGTH_SHORT).show();

        cargarProfesionales(idEmpresaFK, listaProfesionales, profesionalAdapter);
        cargarServiciosEmpresa();




    }

    private void cargarServiciosEmpresa() {
        String url = "http://10.0.2.2/TUSSERVI/obtenerServiciosPorEmpresa.php?idEmpresa=" + idEmpresaFK;
        listaServicios.clear();

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            JSONArray array = response.getJSONArray("servicios");
                            if (array.length() == 0) {
                                Toast.makeText(this, "Esta empresa no tiene servicios disponibles", Toast.LENGTH_SHORT).show();
                            }
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj = array.getJSONObject(i);
                                Servicio servicio = new Servicio(
                                        obj.getInt("idServicio"),
                                        obj.getString("tituloServicio"),
                                        obj.getString("descripcionServicio"),
                                        obj.getDouble("precioEstimadoServicio")
                                );
                                listaServicios.add(servicio);
                            }
                            servicioAdapter = new ServicioAdapter(listaServicios);

                            recyclerServicios.setAdapter(servicioAdapter);
                        } else {
                            Toast.makeText(this, "Error al obtener los servicios", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al procesar los datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Error de conexión", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(this).add(request);
    }
    private void cargarProfesionales(int idEmpresa, List<Profesional> ListaProfesionales, ProfesionalAdapter profesionalAdapter) {
        String url = "http://10.0.2.2/TUSSERVI/obtenerProfesionalesPorEmpresa.php?idEmpresa=" + idEmpresa;

        JsonObjectRequest jsonRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    try {
                        JSONArray array = response.getJSONArray("profesionales");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obj = array.getJSONObject(i);
                            int idProfesional = obj.getInt("idProfesional");
                            int idUsuario = obj.getInt("idUsuario");
                            String nombre = obj.getString("nombre");
                            String categoria = obj.getString("categoria");
                            String fotoPerfil = obj.getString("fotoPerfil");

                            ListaProfesionales.add(new Profesional(idProfesional, idUsuario, nombre, categoria, fotoPerfil));
                        }
                        profesionalAdapter.notifyDataSetChanged();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al procesar profesionales", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Error de conexión al cargar profesionales", Toast.LENGTH_SHORT).show();
                }
        );

        Volley.newRequestQueue(this).add(jsonRequest);
    }



}
