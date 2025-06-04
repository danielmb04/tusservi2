package es.studium.tusservi.ui.chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.studium.tusservi.databinding.FragmentChatBinding;

public class ChatFragment extends Fragment {

    private FragmentChatBinding binding;
    private List<Usuario> listaUsuarios = new ArrayList<>();
    private UsuarioAdapter adapter;
    private int idUsuarioActual;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);

        idUsuarioActual = obtenerIdUsuarioDesdeSharedPreferences();

        binding.recyclerConversaciones.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UsuarioAdapter(listaUsuarios, usuario -> {
            // Aquí abrir chat con usuario seleccionado
            abrirChatConUsuario(usuario);
        });
        binding.recyclerConversaciones.setAdapter(adapter);

        obtenerUsuariosParaChatear();

        return binding.getRoot();
    }

    private int obtenerIdUsuarioDesdeSharedPreferences() {
        SharedPreferences preferences = requireContext().getSharedPreferences("session", Context.MODE_PRIVATE);
        String idUsuarioString = preferences.getString("idUsuario", "-1");
        return Integer.parseInt(idUsuarioString);
    }

    private void obtenerUsuariosParaChatear() {
        String url = "http://10.0.2.2/TUSSERVI/obtenerUsuario.php?idUsuario=" + idUsuarioActual;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {

                    if (binding == null)return;

                    listaUsuarios.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            int idUsuario = obj.getInt("idUsuario");
                            String nombre = obj.getString("nombreUsuario");
                            String urlImagenPerfil = obj.optString("fotoPerfilUsuario", "");  // nombre del campo que envíes desde PHP

                            listaUsuarios.add(new Usuario(idUsuario, nombre, urlImagenPerfil));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();

                    binding.txtSinConversaciones.setVisibility(listaUsuarios.isEmpty() ? View.VISIBLE : View.GONE);
                },
                error -> {
                    error.printStackTrace();
                    binding.txtSinConversaciones.setText("Error al cargar usuarios.");
                    binding.txtSinConversaciones.setVisibility(View.VISIBLE);
                }
        );

        Volley.newRequestQueue(requireContext()).add(request);
    }


    private void abrirChatConUsuario(Usuario usuario) {
        Intent intent = new Intent(getContext(), ChatMensajesActivity.class);
        intent.putExtra("idEmisor", idUsuarioActual);
        intent.putExtra("idReceptor", usuario.getIdUsuario());
        startActivity(intent);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

