package es.studium.tusservi.empresa;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import es.studium.tusservi.R;
import es.studium.tusservi.servicio.Servicio;
import es.studium.tusservi.servicio.ServicioAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.List;

public class DetalleEmpresaActivity extends AppCompatActivity {

    TextView txtNombre, txtDescripcion, txtUbicacion, txtHorario, txtWeb, txtCategoria;
    ImageView imgLogo;
    RecyclerView recyclerServicios;
    ServicioAdapter servicioAdapter;
    List<Servicio> listaServicios = new ArrayList<>();
    int idEmpresaFK;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle_empresa);

        txtNombre = findViewById(R.id.txtNombreEmpresa);
        txtDescripcion = findViewById(R.id.txtDescripcion);
        txtUbicacion = findViewById(R.id.txtUbicacion);
        txtHorario = findViewById(R.id.txtHorario);
        txtWeb = findViewById(R.id.txtWeb);
        txtCategoria = findViewById(R.id.txtCategoria);
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

        Toast.makeText(this, "ID empresa: " + idEmpresaFK, Toast.LENGTH_SHORT).show();

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
                            servicioAdapter = new ServicioAdapter(this, listaServicios);
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
                }
                );

        Volley.newRequestQueue(this).add(request);
    }

}

