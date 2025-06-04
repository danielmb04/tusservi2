package es.studium.tusservi.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import es.studium.tusservi.R;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    public interface OnUsuarioClickListener {
        void onUsuarioClick(Usuario usuario);
    }

    private final List<Usuario> listaUsuarios;
    private final OnUsuarioClickListener listener;


    public UsuarioAdapter(List<Usuario> listaUsuarios, OnUsuarioClickListener listener) {
        this.listaUsuarios = listaUsuarios;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_usuario, parent, false);
        return new UsuarioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        Usuario usuario = listaUsuarios.get(position);
        holder.txtNombreUsuario.setText(usuario.getNombre());
        String URL_BASE = "http://10.0.2.2/TUSSERVI/";
        String urlImagenCompleta = URL_BASE + usuario.getUrlImagenPerfil();
        // Si tienes la URL del perfil:

        Glide.with(holder.imgPerfil.getContext())
                .load(urlImagenCompleta)
                .placeholder(R.drawable.ic_launcher_background)
                .circleCrop()
                .into(holder.imgPerfil);

        holder.itemView.setOnClickListener(v -> listener.onUsuarioClick(usuario));

    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    public static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        public ImageView imgPerfil;
        TextView txtNombreUsuario;


        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombreUsuario = itemView.findViewById(R.id.txtNombreUsuario);
            imgPerfil = itemView.findViewById(R.id.imgPerfil);
        }
    }
}

