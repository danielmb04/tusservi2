package es.studium.tusservi.ui.homeProfesional;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import es.studium.tusservi.R;
import es.studium.tusservi.servicio.Servicio;

public class DialogoServicio extends DialogFragment {

    public interface OnServicioCreadoListener {
        void onServicioCreado(Servicio servicio);
    }

    private OnServicioCreadoListener listener;
    private EditText etTitulo, etDescripcion, etPrecio;

    public DialogoServicio(OnServicioCreadoListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View view = LayoutInflater.from(getContext()).inflate(R.layout.dialog_servicio, null);

        etTitulo = view.findViewById(R.id.etTituloServicio);
        etDescripcion = view.findViewById(R.id.etDescripcionServicio);
        etPrecio = view.findViewById(R.id.etPrecioServicio);

        builder.setView(view)
                .setTitle("Nuevo Servicio")
                .setPositiveButton("Guardar", null) // Lo manejamos manualmente en onStart()
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss());

        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        AlertDialog dialog = (AlertDialog) getDialog();
        if (dialog != null) {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                boolean valido = true;

                String titulo = etTitulo.getText().toString().trim();
                String descripcion = etDescripcion.getText().toString().trim();
                String precioStr = etPrecio.getText().toString().trim();

                if (TextUtils.isEmpty(titulo)) {
                    etTitulo.setError("El título es obligatorio");
                    valido = false;
                } else {
                    etTitulo.setError(null);
                }

                if (TextUtils.isEmpty(descripcion)) {
                    etDescripcion.setError("La descripción es obligatoria");
                    valido = false;
                } else {
                    etDescripcion.setError(null);
                }

                if (TextUtils.isEmpty(precioStr)) {
                    etPrecio.setError("El precio es obligatorio");
                    valido = false;
                } else {
                    try {
                        Double.parseDouble(precioStr);
                        etPrecio.setError(null);
                    } catch (NumberFormatException e) {
                        etPrecio.setError("Precio no válido");
                        valido = false;
                    }
                }

                if (valido) {
                    double precio = Double.parseDouble(precioStr);
                    Servicio servicio = new Servicio(0, titulo, descripcion, precio);
                    listener.onServicioCreado(servicio);
                    dialog.dismiss();
                }
            });
        }
    }
}
