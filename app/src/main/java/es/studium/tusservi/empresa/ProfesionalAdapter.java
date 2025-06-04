package es.studium.tusservi.empresa;

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

public class ProfesionalAdapter extends RecyclerView.Adapter<ProfesionalAdapter.ProfesionalViewHolder> {

    private final List<Profesional> listaProfesionales;

    public ProfesionalAdapter(List<Profesional> listaProfesionales) {
        this.listaProfesionales = listaProfesionales;
    }

    @NonNull
    @Override
    public ProfesionalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vista = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profesional, parent, false);
        return new ProfesionalViewHolder(vista);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfesionalViewHolder holder, int position) {
        Profesional profesional = listaProfesionales.get(position);
        holder.txtNombre.setText(profesional.getNombre());
        holder.txtCategoria.setText(profesional.getCategoria());

        // Cargar imagen con Glide
        Glide.with(holder.imgFoto.getContext())
                .load(profesional.getFotoPerfil())
                .placeholder(R.drawable.ic_launcher_background) // asegúrate de tener esta imagen en res/drawable
                .error(R.drawable.ic_launcher_foreground)
                .into(holder.imgFoto);
    }

    @Override
    public int getItemCount() {
        return listaProfesionales.size();
    }

    public static class ProfesionalViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtCategoria;
        ImageView imgFoto;

        public ProfesionalViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreProfesional);
            txtCategoria = itemView.findViewById(R.id.txtCategoriaProfesional);
            imgFoto = itemView.findViewById(R.id.imgFotoPerfil);
        }
    }
}
