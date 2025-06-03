package es.studium.tusservi.empresa;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

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

        String logo = empresa.getLogo();
        Log.d("EmpresaAdapter", "logo recibido: '" + logo + "'");



        if (logo != null) {
            // Limpiar el nombre por si trae la carpeta "logos/"
            if (logo.startsWith("logos/")) {
                logo = logo.substring("logos/".length());
            }
            String urlLogo = "http://10.0.2.2/TUSSERVI/uploads/" + logo + "?t=" + System.currentTimeMillis();
            Log.d("EmpresaAdapter", "URL logo: " + urlLogo);
            Glide.with(context)
                    .load(urlLogo)
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.imgLogo);
        } else {
            holder.imgLogo.setImageResource(R.drawable.ic_launcher_background);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetalleEmpresaActivity.class);
            intent.putExtra("idEmpresaFK", empresa.getId());
            intent.putExtra("nombre", empresa.getNombre());
            intent.putExtra("descripcion", empresa.getDescripcion());
            intent.putExtra("ubicacion", empresa.getUbicacion());
            intent.putExtra("horario", empresa.getHorario());
            intent.putExtra("web", empresa.getWeb());
            intent.putExtra("categoria", empresa.getCategoria());
            intent.putExtra("experiencia", empresa.getExperiencia());
            intent.putExtra("logo", empresa.getLogo());
            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (listener != null) {
                listener.onEmpresaLongClick(empresa);
            }
            return true;
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
            imgLogo = itemView.findViewById(R.id.imgLogoEmpresa); // Aquí enlazas el ImageView
        }
    }
    public interface OnEmpresaLongClickListener {
        void onEmpresaLongClick(Empresa empresa);
    }

    private OnEmpresaLongClickListener listener;

    public void setOnEmpresaLongClickListener(OnEmpresaLongClickListener listener) {
        this.listener = listener;
    }

}
