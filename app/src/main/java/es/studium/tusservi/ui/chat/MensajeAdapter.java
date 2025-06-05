package es.studium.tusservi.ui.chat;

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

import java.util.List;

import es.studium.tusservi.R;

public class MensajeAdapter extends RecyclerView.Adapter<MensajeAdapter.MensajeViewHolder> {
    private final Context context;
    private final List<Mensaje> lista;
    private final int idUsuarioActual;

    public MensajeAdapter(Context context, List<Mensaje> lista, int idUsuarioActual) {
        this.context = context;
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
        Mensaje mensaje = lista.get(position);

        Log.d("MensajeAdapter", "Tipo mensaje: " + mensaje.getTipo());

        if ("imagen".equals(mensaje.getTipo())) {
            holder.txtMensaje.setVisibility(View.GONE);
            holder.imgMensaje.setVisibility(View.VISIBLE);

            String urlImagen = "http://10.0.2.2/TUSSERVI/" + mensaje.getTexto();

            Glide.with(holder.imgMensaje.getContext())
                    .load(urlImagen)
                    .placeholder(R.drawable.ic_dashboard_black_24dp)
                    .error(R.drawable.ic_launcher_background)
                    .into(holder.imgMensaje);

            // Abrir en pantalla completa
            holder.imgMensaje.setOnClickListener(v -> {
                Intent intent = new Intent(context, FullScreenImageActivity.class);
                intent.putExtra("imageUrl", urlImagen);
                context.startActivity(intent);
            });

        } else {
            holder.imgMensaje.setVisibility(View.GONE);
            holder.txtMensaje.setVisibility(View.VISIBLE);
            holder.txtMensaje.setText(mensaje.getTexto());
        }



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
        ImageView imgMensaje;

        MensajeViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMensaje = itemView.findViewById(R.id.txtMensaje);
            imgMensaje = itemView.findViewById(R.id.imageViewMensaje);
        }

    }

}
