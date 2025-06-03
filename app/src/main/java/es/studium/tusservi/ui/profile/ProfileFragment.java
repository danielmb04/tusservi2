package es.studium.tusservi.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import es.studium.tusservi.R;
import es.studium.tusservi.WelcomeActivity;
import es.studium.tusservi.databinding.FragmentProfileProfesionalBinding;

public class ProfileFragment extends Fragment {
    private FragmentProfileProfesionalBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileProfesionalBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        cargarPerfilDesdeServidor();

        // Acción: Editar perfil
        binding.btnEditarPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditarPerfilActivity.class);
            startActivity(intent);
        });

        // Acción: Cerrar sesión
        binding.btnCerrarSesion.setOnClickListener(v -> {
            SharedPreferences preferences = requireContext().getSharedPreferences("session", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();

            Intent intent = new Intent(getActivity(), WelcomeActivity.class);
            startActivity(intent);
            requireActivity().finish();

            Toast.makeText(getContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show();
        });

        return root;
    }

    private void cargarPerfilDesdeServidor() {
        SharedPreferences preferences = requireContext().getSharedPreferences("session", Context.MODE_PRIVATE);
        String idUsuarioStr = preferences.getString("idUsuario", null);

        if (idUsuarioStr == null) {
            Toast.makeText(getContext(), "ID de usuario no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        int idUsuario = Integer.parseInt(idUsuarioStr); // Solo si lo necesitas como entero


        String BASE_URL = "http://10.0.2.2/TUSSERVI/"; // 🛠️ Sustituye con tu URL real

        RequestQueue queue = Volley.newRequestQueue(requireContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, BASE_URL + "obtenerPerfil.php",
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            String nombre = jsonObject.getString("nombre");
                            String urlFotoRelativa = jsonObject.getString("foto");
                            String urlFotoCompleta = BASE_URL;
                            if (!BASE_URL.endsWith("/")) {
                                urlFotoCompleta += "/";
                            }
                            urlFotoCompleta += "uploads/" + urlFotoRelativa;

                            binding.textNombreProfesional.setText(nombre);
                            cargarImagenDesdeUrl(urlFotoCompleta);

                        } else {
                            Toast.makeText(getContext(), "No se pudo cargar el perfil", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(getContext(), "Error de red", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("idUsuario", String.valueOf(idUsuario));
                return params;
            }
        };

        queue.add(stringRequest);
    }

    private void cargarImagenDesdeUrl(String urlFoto) {
        Glide.with(this)
                .load(urlFoto)
                .placeholder(R.drawable.ic_launcher_background)  // Imagen mientras carga
                .error(R.drawable.ic_dashboard_black_24dp)             // Imagen si falla cargar
                .into(binding.imageProfile);
    }
    @Override
    public void onResume() {
        super.onResume();
        cargarPerfilDesdeServidor(); // Se ejecuta siempre que el fragmento se vuelve visible
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
