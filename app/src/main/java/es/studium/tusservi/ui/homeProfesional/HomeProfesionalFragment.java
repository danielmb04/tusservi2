package es.studium.tusservi.ui.homeProfesional;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.studium.tusservi.R;
import es.studium.tusservi.databinding.FragmentHomeProfesionalBinding;
import es.studium.tusservi.empresa.Empresa;
import es.studium.tusservi.empresa.EmpresaAdapter;

public class HomeProfesionalFragment extends Fragment {

    private FragmentHomeProfesionalBinding binding;
    private RecyclerView recyclerView;
    private EmpresaAdapter adapter;
    private List<Empresa> listaEmpresas;
    private static final int REQUEST_EDIT_EMPRESA = 123;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeProfesionalBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        recyclerView = root.findViewById(R.id.recyclerEmpresas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        listaEmpresas = new ArrayList<>();
        adapter = new EmpresaAdapter(getContext(), listaEmpresas);
        recyclerView.setAdapter(adapter);
        adapter.setOnEmpresaLongClickListener(empresa -> {
            new AlertDialog.Builder(requireContext())
                    .setTitle("Eliminar empresa")
                    .setMessage("¿Estás seguro de que deseas eliminar la empresa \"" + empresa.getNombre() + "\"?")
                    .setPositiveButton("Eliminar", (dialog, which) -> {
                        eliminarEmpresa(empresa);
                        actualizarVista();// Aquí defines tu lógica de eliminación
                    })
                    .setNegativeButton("Cancelar", null)
                    .show();
        });

        // Listener botón Publicar empresa
        binding.btnPublicarEmpresa.setOnClickListener(v -> {
            SharedPreferences prefs = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE);
            String idProfesionalStr = prefs.getString("idProfesional", null);
            int idProfesional = -1;
            if (idProfesionalStr != null) {
                try {
                    idProfesional = Integer.parseInt(idProfesionalStr);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
            Intent intent = new Intent(getContext(), RegistroEmpresaActivity.class);
            intent.putExtra("idProfesional", idProfesional);
            startActivity(intent);
        });

        // Listener botón Editar empresa
        binding.btnEditarEmpresa.setOnClickListener(v -> {
            if (!listaEmpresas.isEmpty()) {
                Empresa empresa = listaEmpresas.get(0);
                Intent intent = new Intent(getContext(), EditarEmpresaActivity.class);
                intent.putExtra("empresa", empresa);
                startActivityForResult(intent, REQUEST_EDIT_EMPRESA);
            }
        });


        actualizarVista();

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences prefs = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE);
        String idProfesionalStr = prefs.getString("idProfesional", null);
        if (idProfesionalStr != null) {
            try {
                int idProfesional = Integer.parseInt(idProfesionalStr);
                cargarEmpresas(idProfesional);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }


    private void actualizarVista() {
        SharedPreferences prefs = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE);
        String idProfesionalStr = prefs.getString("idProfesional", null);

        if (idProfesionalStr != null) {
            try {
                int idProfesional = Integer.parseInt(idProfesionalStr);
                cargarEmpresas(idProfesional); // Nuevo método
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
    }


    private void cargarEmpresas(int idProfesional) {
        String url = "http://10.0.2.2/TUSSERVI/getEmpresasPorProfesional.php?idProfesional=" + idProfesional;

        RequestQueue queue = Volley.newRequestQueue(getContext());

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
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
                                String logo = obj.optString("logoEmpresa", "");
                                if (logo.equalsIgnoreCase("null")) {
                                    logo = "";
                                }

                                String categoria = obj.optString("categoriaProfesional");
                                int experiencia = obj.optInt("experienciaProfesional");

                                Empresa empresa = new Empresa(id, nombre, descripcion, ubicacion, horario, web, logo, categoria, experiencia);
                                listaEmpresas.add(empresa);
                            }

                            adapter.notifyDataSetChanged();

                            if (listaEmpresas.isEmpty()) {
                                binding.recyclerEmpresas.setVisibility(View.GONE);
                                binding.btnPublicarEmpresa.setVisibility(View.VISIBLE);
                                binding.btnEditarEmpresa.setVisibility(View.GONE); // Ocultar si no tiene empresa
                            } else {
                                binding.recyclerEmpresas.setVisibility(View.VISIBLE);
                                binding.btnPublicarEmpresa.setVisibility(View.GONE);
                                binding.btnEditarEmpresa.setVisibility(View.VISIBLE); // Mostrar si tiene empresa
                            }


                        } else {
                            // No tiene empresas
                            binding.btnPublicarEmpresa.setVisibility(View.VISIBLE);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error al procesar datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(getContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
        );

        queue.add(request);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_EDIT_EMPRESA && resultCode == getActivity().RESULT_OK) {
            // Recarga la lista de empresas para reflejar los cambios
            SharedPreferences prefs = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE);
            String idProfesionalStr = prefs.getString("idProfesional", null);
            if (idProfesionalStr != null) {
                try {
                    int idProfesional = Integer.parseInt(idProfesionalStr);
                    cargarEmpresas(idProfesional);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void eliminarEmpresa(Empresa empresa) {
        StringRequest request = new StringRequest(Request.Method.POST,
                "http://10.0.2.2/TUSSERVI/eliminarEmpresa.php",
                response -> {
                    try {
                        JSONObject json = new JSONObject(response);
                        if (json.getBoolean("success")) {
                            Toast.makeText(getContext(), "Empresa eliminada", Toast.LENGTH_SHORT).show();
                            // Recarga toda la lista
                            actualizarVista();
                        } else {
                            Toast.makeText(getContext(), "Error: " + json.getString("message"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error de respuesta del servidor", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show();
                }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("idEmpresa", String.valueOf(empresa.getId()));
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}