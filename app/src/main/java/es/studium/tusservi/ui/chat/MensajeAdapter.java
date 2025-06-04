package es.studium.tusservi.ui.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.studium.tusservi.R;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.MensajeViewHolder> {

    private final List<Mensaje> lista;
    private final int idUsuarioActual;

    public MensajeAdapter(List<Mensaje> lista, int idUsuarioActual) {
        this.lista = lista;
        this.idUsuarioActual = idUsuarioActual;
    }

    @NonNull
    @Override
    public MensajeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layout = (viewType == 1) ? R.layout.item_mensaje_emisor : R.layout.item_mensaje_receptor;
        View view = LayoutInflater.from(parent.getContext()).inflate(layout, parent, false);
        return new MensajeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MensajeViewHolder holder, int position) {
        holder.txtMensaje.setText(lista.get(position).getTexto());
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    @Override
    public int getItemViewType(int position) {
        return lista.get(position).getEmisorId() == idUsuarioActual ? 1 : 0;
    }

    static class MensajeViewHolder extends RecyclerView.ViewHolder {
        TextView txtMensaje;

        MensajeViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMensaje = itemView.findViewById(R.id.txtMensaje);
        }
    }
}
