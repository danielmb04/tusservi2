package es.studium.tusservi.ui.homeProfesional;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
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

public class EditarServiciosActivity extends AppCompatActivity {

    int idEmpresa;
    RecyclerView recyclerView;
    ArrayList<Servicio> listaServicios = new ArrayList<>();
    ServicioAdapter adapter;
    Button btnAñadir, btnGuardarCambiosServicios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_servicios);

        Intent intent = getIntent();
        idEmpresa = intent.getIntExtra("idEmpresa", -1);

        recyclerView = findViewById(R.id.recyclerServicios);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Adapter con listener completo para editar y eliminar
        adapter = new ServicioAdapter(listaServicios, true, new ServicioAdapter.OnServicioActionListener() {
            @Override
            public void onEditarClick(Servicio servicio) {
                mostrarDialogoEditarServicio(servicio);
            }

            @Override
            public void onEliminarLongClick(Servicio servicio) {
                new AlertDialog.Builder(EditarServiciosActivity.this)
                        .setTitle("Eliminar servicio")
                        .setMessage("¿Estás seguro de que quieres eliminar este servicio?")
                        .setPositiveButton("Eliminar", (dialog, which) -> eliminarServicio(servicio.getId()))
                        .setNegativeButton("Cancelar", null)
                        .show();
            }
        });

        recyclerView.setAdapter(adapter);

        btnAñadir = findViewById(R.id.btnAñadirServicio);
        btnAñadir.setOnClickListener(v -> mostrarDialogoAñadirServicio());
        btnGuardarCambiosServicios = findViewById(R.id.btnGuardarCambiosServicios);
        btnGuardarCambiosServicios.setOnClickListener(v -> {
            Toast.makeText(this, "Cambios guardados", Toast.LENGTH_SHORT).show();
            finish();
        });

        cargarServicios();
    }

    private void cargarServicios() {
        String url = "http://10.0.2.2/TUSSERVI/obtenerServiciosPorEmpresa.php?idEmpresa=" + idEmpresa;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        listaServicios.clear();

                        JSONArray serviciosArray = response.getJSONArray("servicios");

                        for (int i = 0; i < serviciosArray.length(); i++) {
                            JSONObject servicioObj = serviciosArray.getJSONObject(i);
                            int id = servicioObj.getInt("idServicio");
                            String titulo = servicioObj.getString("tituloServicio");
                            String descripcion = servicioObj.getString("descripcionServicio");
                            double precio = servicioObj.getDouble("precioEstimadoServicio");

                            Servicio servicio = new Servicio(id, titulo, descripcion, precio);
                            listaServicios.add(servicio);
                        }

                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error al procesar datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Error al cargar servicios", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void mostrarDialogoAñadirServicio() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_servicio, null);
        builder.setView(dialogView);
        builder.setTitle("Añadir nuevo servicio");

        EditText edtTitulo = dialogView.findViewById(R.id.etTituloServicio);
        EditText edtDescripcion = dialogView.findViewById(R.id.etDescripcionServicio);
        EditText edtPrecio = dialogView.findViewById(R.id.etPrecioServicio);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String titulo = edtTitulo.getText().toString().trim();
            String descripcion = edtDescripcion.getText().toString().trim();
            String precioStr = edtPrecio.getText().toString().trim();

            if (titulo.isEmpty() || descripcion.isEmpty() || precioStr.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            double precio = Double.parseDouble(precioStr);
            insertarServicio(titulo, descripcion, precio);
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void mostrarDialogoEditarServicio(Servicio servicio) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_servicio, null);
        builder.setView(dialogView);
        builder.setTitle("Editar servicio");

        EditText edtTitulo = dialogView.findViewById(R.id.etTituloServicio);
        EditText edtDescripcion = dialogView.findViewById(R.id.etDescripcionServicio);
        EditText edtPrecio = dialogView.findViewById(R.id.etPrecioServicio);

        // Rellenar con los datos actuales
        edtTitulo.setText(servicio.getTituloServicio());
        edtDescripcion.setText(servicio.getDescripcionServicio());
        edtPrecio.setText(String.valueOf(servicio.getPrecioEstimadoServicio()));

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String titulo = edtTitulo.getText().toString().trim();
            String descripcion = edtDescripcion.getText().toString().trim();
            String precioStr = edtPrecio.getText().toString().trim();

            if (titulo.isEmpty() || descripcion.isEmpty() || precioStr.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            double precio = Double.parseDouble(precioStr);
            actualizarServicio(servicio.getId(), titulo, descripcion, precio);
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void insertarServicio(String titulo, String descripcion, double precio) {
        String url = "http://10.0.2.2/TUSSERVI/insertarServicio.php";

        JSONObject json = new JSONObject();
        try {
            json.put("idEmpresaFK", idEmpresa);
            json.put("tituloServicio", titulo);
            json.put("descripcionServicio", descripcion);
            json.put("precioEstimadoServicio", precio);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al crear JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, json,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            Toast.makeText(this, "Servicio añadido correctamente", Toast.LENGTH_SHORT).show();
                            cargarServicios(); // Recarga la lista
                        } else {
                            Toast.makeText(this, "Error: " + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error de red al insertar", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void actualizarServicio(int idServicio, String titulo, String descripcion, double precio) {
        String url = "http://10.0.2.2/TUSSERVI/actualizarServicio.php";

        JSONObject json = new JSONObject();
        try {
            json.put("idServicio", idServicio);
            json.put("tituloServicio", titulo);
            json.put("descripcionServicio", descripcion);
            json.put("precioEstimadoServicio", precio);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al crear JSON para actualizar", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, json,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            Toast.makeText(this, "Servicio actualizado correctamente", Toast.LENGTH_SHORT).show();
                            cargarServicios(); // Recarga la lista
                        } else {
                            Toast.makeText(this, "Error: " + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error de red al actualizar", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }

    private void eliminarServicio(int idServicio) {
        String url = "http://10.0.2.2/TUSSERVI/eliminarServicio.php";

        JSONObject json = new JSONObject();
        try {
            json.put("idServicio", idServicio);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al preparar JSON", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, json,
                response -> {
                    try {
                        if (response.getBoolean("success")) {
                            Toast.makeText(this, "Servicio eliminado", Toast.LENGTH_SHORT).show();
                            cargarServicios(); // Refrescar la lista
                        } else {
                            Toast.makeText(this, "Error: " + response.getString("message"), Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(this, "Error al eliminar servicio", Toast.LENGTH_SHORT).show()
        );

        Volley.newRequestQueue(this).add(request);
    }
}
