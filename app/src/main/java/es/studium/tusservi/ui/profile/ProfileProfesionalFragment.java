package es.studium.tusservi.ui.profile;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;

import es.studium.tusservi.LoginActivity;
import es.studium.tusservi.R;
import es.studium.tusservi.WelcomeActivity;
import es.studium.tusservi.databinding.FragmentProfileProfesionalBinding;

public class ProfileProfesionalFragment extends Fragment {
    private FragmentProfileProfesionalBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        cargarDatosPerfil(); // Llama al método justo después de inflar el layout

        binding = FragmentProfileProfesionalBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        // Acción: Editar perfil
        binding.btnEditarPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), EditarPerfilProfesionalActivity.class);
            startActivity(intent);
        });


        // Acción: Cerrar sesión
        binding.btnCerrarSesion.setOnClickListener(v -> {
            SharedPreferences preferences = requireContext().getSharedPreferences("session", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear(); // Borra todos los datos de sesión
            editor.apply();

// Redirige al login
            Intent intent = new Intent(getActivity(), WelcomeActivity.class);
            startActivity(intent);
            requireActivity().finish();

            Toast.makeText(getContext(), "Sesión cerrada", Toast.LENGTH_SHORT).show();

            // Ejemplo de cómo podrías redirigir (adaptar según tu app):
            // startActivity(new Intent(getActivity(), LoginActivity.class));
            // getActivity().finish();
        });

        return root;
    }
    @Override
    public void onResume() {
        super.onResume();
        cargarDatosPerfil(); // Se ejecuta siempre que el fragmento se vuelve visible
    }
    private void cargarDatosPerfil() {
        SharedPreferences preferences = requireContext().getSharedPreferences("session", Context.MODE_PRIVATE);
        String idUsuarioStr = preferences.getString("idUsuario", null);

        if (idUsuarioStr == null) {
            Toast.makeText(getContext(), "ID de usuario no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        int idUsuario = Integer.parseInt(idUsuarioStr);

        String url = "http://10.0.2.2/TUSSERVI/obtenerPerfilProfesional.php?idUsuario=" + idUsuario;

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        String nombre = response.getString("nombreUsuario");
                        String fotoPerfil = response.getString("fotoPerfilProfesional");

                        binding.textNombreProfesional.setText(nombre);

                        String fotoPerfilUrl = response.getString("fotoPerfilProfesional");

                        Glide.with(requireContext())
                                .load(fotoPerfilUrl)
                                .placeholder(R.drawable.ic_launcher_background)
                                .into(binding.imageProfile);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(getContext(), "Error al procesar datos", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    error.printStackTrace();
                    Toast.makeText(getContext(), "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                });

        Volley.newRequestQueue(requireContext()).add(request);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
