package es.studium.tusservi.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.studium.tusservi.R;
import es.studium.tusservi.databinding.FragmentHomeBinding;
import es.studium.tusservi.empresa.Empresa;
import es.studium.tusservi.empresa.EmpresaAdapter;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private EmpresaAdapter adapter;
    private List<Empresa> listaEmpresas;
    private EditText searchBar;

    private static final String URL_EMPRESAS = "http://10.0.2.2/TUSSERVI/obtenerEmpresas.php";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = root.findViewById(R.id.recyclerEmpresa);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        searchBar = root.findViewById(R.id.searchBar);

        listaEmpresas = new ArrayList<>();
        adapter = new EmpresaAdapter(getContext(), listaEmpresas);
        recyclerView.setAdapter(adapter);

        cargarEmpresasDesdeServidor();

        return root;
    }

    private void cargarEmpresasDesdeServidor() {
        RequestQueue queue = Volley.newRequestQueue(getContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, URL_EMPRESAS, null,
                response -> {
                    try {
                        boolean success = response.getBoolean("success");
                        if (success) {
                            JSONArray empresasArray = response.getJSONArray("empresas");
                            listaEmpresas.clear();

                            for (int i = 0; i < empresasArray.length(); i++) {
                                JSONObject obj = empresasArray.getJSONObject(i);

                                int id = obj.getInt("idEmpresa");
                                String nombre = obj.getString("nombreEmpresa");
                                String descripcion = obj.getString("descripcionEmpresa");
                                String ubicacion = obj.getString("ubicacionEmpresa");
                                String horario = obj.getString("horarioEmpresa");
                                String web = obj.getString("webEmpresa");
                                String logo = obj.getString("logoEmpresa");
                                String categoria = obj.getString("categoriaProfesional");

                                Empresa empresa = new Empresa(id, nombre, descripcion, ubicacion, horario, web, logo, categoria);
                                listaEmpresas.add(empresa);
                            }

                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(getContext(), "No hay empresas disponibles", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error al procesar datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Error de conexión con servidor", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }

}
