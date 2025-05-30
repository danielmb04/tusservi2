package es.studium.tusservi.ui.homeProfesional;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import es.studium.tusservi.R;
import es.studium.tusservi.servicio.Servicio;

public class DialogoServicio extends DialogFragment {


    public interface OnServicioCreadoListener {
        void onServicioCreado(Servicio servicio);
    }

    private OnServicioCreadoListener listener;

    public DialogoServicio(OnServicioCreadoListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_servicio, null);

        EditText etTitulo = view.findViewById(R.id.etTituloServicio);
        EditText etDescripcion = view.findViewById(R.id.etDescripcionServicio);
        EditText etPrecio = view.findViewById(R.id.etPrecioServicio);

        builder.setView(view)
                .setTitle("Nuevo Servicio")
                .setPositiveButton("Guardar", (dialog, which) -> {
                    String titulo = etTitulo.getText().toString();
                    String descripcion = etDescripcion.getText().toString();
                    double precio = Double.parseDouble(etPrecio.getText().toString());

                    listener.onServicioCreado(new Servicio(0, titulo, descripcion, precio));
                })
                .setNegativeButton("Cancelar", null);

        return builder.create();
    }
}
