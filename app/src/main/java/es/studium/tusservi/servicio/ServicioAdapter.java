package es.studium.tusservi.servicio;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.studium.tusservi.R;

public class ServicioAdapter extends RecyclerView.Adapter<ServicioAdapter.ServicioViewHolder> {

    private final List<Servicio> servicios;
    private final boolean mostrarBotones;
    private final OnServicioActionListener actionListener;

    // Constructor para visualización simple (sin botones)
    public ServicioAdapter(List<Servicio> servicios) {
        this(servicios, false, null);
    }

    // Constructor para edición (con botones y listener)
    public ServicioAdapter(List<Servicio> servicios, boolean mostrarBotones, OnServicioActionListener listener) {
        this.servicios = servicios;
        this.mostrarBotones = mostrarBotones;
        this.actionListener = listener;
    }

    @NonNull
    @Override
    public ServicioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_servicio, parent, false);
        return new ServicioViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServicioViewHolder holder, int position) {
        Servicio servicio = servicios.get(position);
        holder.titulo.setText(servicio.getTituloServicio());
        holder.descripcion.setText(servicio.getDescripcionServicio());
        holder.precio.setText(String.format("%.2f €", servicio.getPrecioEstimadoServicio()));

        if (mostrarBotones) {
            holder.btnEditar.setVisibility(View.VISIBLE);

            holder.btnEditar.setOnClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onEditarClick(servicio);
                }
            });

            holder.itemView.setOnLongClickListener(v -> {
                if (actionListener != null) {
                    actionListener.onEliminarLongClick(servicio);
                    return true;
                }
                return false;
            });
        } else {
            holder.btnEditar.setVisibility(View.GONE);
            holder.itemView.setOnLongClickListener(null);
        }
    }

    @Override
    public int getItemCount() {
        return servicios.size();
    }

    // ViewHolder
    public static class ServicioViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, descripcion, precio;
        Button btnEditar;

        public ServicioViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.txtNombreServicio);
            descripcion = itemView.findViewById(R.id.txtDescripcionServicio);
            precio = itemView.findViewById(R.id.txtPrecioServicio);
            btnEditar = itemView.findViewById(R.id.btnEditarServicio);
        }
    }

    // Listener para acciones
    public interface OnServicioActionListener {
        void onEditarClick(Servicio servicio);
        void onEliminarLongClick(Servicio servicio);
    }
}
