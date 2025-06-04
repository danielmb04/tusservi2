package es.studium.tusservi.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.studium.tusservi.R;

public class ConversacionAdapter extends RecyclerView.Adapter<ConversacionAdapter.ConversacionViewHolder> {

    public interface OnConversacionClickListener {
        void onConversacionClick(Conversacion conversacion);
    }

    private final List<Conversacion> listaConversaciones;
    private final OnConversacionClickListener listener;

    public ConversacionAdapter(List<Conversacion> listaConversaciones, OnConversacionClickListener listener) {
        this.listaConversaciones = listaConversaciones;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ConversacionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_conversacion, parent, false);
        return new ConversacionViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversacionViewHolder holder, int position) {
        Conversacion conversacion = listaConversaciones.get(position);
        holder.txtNombreUsuario.setText(conversacion.getNombreOtroUsuario());
        holder.txtUltimoMensaje.setText(conversacion.getUltimoMensaje());

        holder.itemView.setOnClickListener(v -> listener.onConversacionClick(conversacion));
    }

    @Override
    public int getItemCount() {
        return listaConversaciones.size();
    }

    public static class ConversacionViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombreUsuario, txtUltimoMensaje;

        public ConversacionViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombreUsuario = itemView.findViewById(R.id.txtNombreUsuario);
            txtUltimoMensaje = itemView.findViewById(R.id.txtUltimoMensaje);
        }
    }
}

