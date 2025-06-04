package es.studium.tusservi.empresa;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import es.studium.tusservi.R;

public class ProfesionalAdapter extends RecyclerView.Adapter<ProfesionalAdapter.ViewHolder> {

    List<Profesional> listaProfesionales;
    OnContactarClickListener listener;

    public interface OnContactarClickListener {
        void onContactarClick(Profesional profesional);
    }

    public ProfesionalAdapter(List<Profesional> listaProfesionales, OnContactarClickListener listener) {
        this.listaProfesionales = listaProfesionales;
        this.listener = listener;
    }

    @Override
    public ProfesionalAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profesional, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ProfesionalAdapter.ViewHolder holder, int position) {
        Profesional profesional = listaProfesionales.get(position);
        holder.bind(profesional, listener);
    }

    @Override
    public int getItemCount() {
        return listaProfesionales.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtCategoria;
        ImageView imgFotoPerfil;
        Button btnContactar;

        public ViewHolder(View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreProfesional);
            txtCategoria = itemView.findViewById(R.id.txtCategoriaProfesional);
            imgFotoPerfil = itemView.findViewById(R.id.imgFotoPerfil);
            btnContactar = itemView.findViewById(R.id.btnContactar);
        }

        public void bind(final Profesional profesional, final OnContactarClickListener listener) {
            txtNombre.setText(profesional.getNombre());
            txtCategoria.setText(profesional.getCategoria());
            Glide.with(itemView.getContext())
                    .load(profesional.getFotoPerfil())
                    .placeholder(R.drawable.ic_launcher_background)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(imgFotoPerfil);

            btnContactar.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onContactarClick(profesional);
                }
            });
        }
    }
}
