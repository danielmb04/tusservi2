package es.studium.tusservi.empresa;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.studium.tusservi.R;

public class EmpresaAdapter extends RecyclerView.Adapter<EmpresaAdapter.EmpresaViewHolder> {
    Context context;
    List<Empresa> lista;

    public EmpresaAdapter(Context context, List<Empresa> lista) {
        this.context = context;
        this.lista = lista;
    }

    @NonNull
    @Override
    public EmpresaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_empresa, parent, false);
        return new EmpresaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmpresaViewHolder holder, int position) {
        Empresa empresa = lista.get(position);
        holder.txtNombre.setText(empresa.getNombre());
        holder.txtDescripcion.setText(empresa.getDescripcion());


        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalleEmpresaActivity.class);
            intent.putExtra("idEmpresaFK", empresa.getId()); // <- Este es el que te falta
            intent.putExtra("nombre", empresa.getNombre());
            intent.putExtra("descripcion", empresa.getDescripcion());
            intent.putExtra("ubicacion", empresa.getUbicacion());
            intent.putExtra("horario", empresa.getHorario());
            intent.putExtra("web", empresa.getWeb());
            intent.putExtra("categoria", empresa.getCategoria());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return lista.size();
    }

    class EmpresaViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtDescripcion;
        ImageView imgLogo;

        public EmpresaViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreEmpresa);
            txtDescripcion = itemView.findViewById(R.id.txtUbicacionEmpresa);

        }
    }
}
