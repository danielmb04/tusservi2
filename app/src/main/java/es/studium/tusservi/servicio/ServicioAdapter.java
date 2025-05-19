package es.studium.tusservi.servicio;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import es.studium.tusservi.R;
import es.studium.tusservi.empresa.DetalleEmpresaActivity;

public class ServicioAdapter extends RecyclerView.Adapter<ServicioAdapter.ServicioViewHolder> {
    private List<Servicio> servicios;

    public ServicioAdapter(DetalleEmpresaActivity detalleEmpresaActivity, List<Servicio> servicios) {
        this.servicios = servicios;
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
        holder.precio.setText(servicio.getPrecioEstimadoServicio() + " €");
    }

    @Override
    public int getItemCount() {
        return servicios.size();
    }

    public class ServicioViewHolder extends RecyclerView.ViewHolder {
        TextView titulo, descripcion, precio;

        public ServicioViewHolder(@NonNull View itemView) {
            super(itemView);
            titulo = itemView.findViewById(R.id.txtNombreServicio);
            descripcion = itemView.findViewById(R.id.txtDescripcionServicio);
            precio = itemView.findViewById(R.id.txtPrecioServicio);
        }
    }
}
