package es.studium.tusservi.ui.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import es.studium.tusservi.databinding.FragmentChatMensajesBinding;

public class ChatMensajesFragment extends Fragment {

    private FragmentChatMensajesBinding binding;
    private List<Mensaje> listaMensajes = new ArrayList<>();
    private MensajeAdapter adapter;
    private int idConversacion;
    private int idUsuario;

    public static ChatMensajesFragment newInstance(int idConversacion) {
        ChatMensajesFragment fragment = new ChatMensajesFragment();
        Bundle args = new Bundle();
        args.putInt("idConversacion", idConversacion);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentChatMensajesBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        idConversacion = getArguments().getInt("idConversacion", -1);
        idUsuario = getContext().getSharedPreferences("sesion", getContext().MODE_PRIVATE)
                .getInt("idUsuario", -1);

        binding.recyclerViewMensajes.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new MensajeAdapter(requireContext(), listaMensajes, idUsuario); // ✅

        binding.recyclerViewMensajes.setAdapter(adapter);

        cargarMensajes();

        binding.botonEnviar.setOnClickListener(v -> enviarMensaje());

        return root;
    }

    private void cargarMensajes() {
        String url = "http://10.0.2.2/TUSSERVI/obtenerMensajes.php?idConversacion=" + idConversacion;

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    listaMensajes.clear();
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject obj = response.getJSONObject(i);
                            int idEmisor = obj.getInt("idEmisor");
                            String contenido = obj.getString("contenido");
                            String fecha = obj.getString("fechaEnvio");
                            listaMensajes.add(new Mensaje(idEmisor, contenido, fecha));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    adapter.notifyDataSetChanged();
                    binding.recyclerViewMensajes.scrollToPosition(listaMensajes.size() - 1);
                },
                error -> error.printStackTrace()
        );

        Volley.newRequestQueue(getContext()).add(request);
    }

    private void enviarMensaje() {
        String mensaje = binding.editTextMensaje.getText().toString().trim();
        if (mensaje.isEmpty()) return;

        String url = "http://10.0.2.2/TUSSERVI/enviarMensaje.php";
        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    binding.editTextMensaje.setText("");
                    cargarMensajes(); // Refrescar mensajes
                },
                error -> Toast.makeText(getContext(), "Error al enviar", Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public byte[] getBody() {
                String datos = "idConversacion=" + idConversacion +
                        "&idEmisor=" + idUsuario +
                        "&contenido=" + mensaje;
                return datos.getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }
        };

        Volley.newRequestQueue(getContext()).add(request);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
