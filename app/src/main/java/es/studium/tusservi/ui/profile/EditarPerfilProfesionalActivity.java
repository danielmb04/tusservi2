package es.studium.tusservi.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;

import es.studium.tusservi.R;

public class EditarPerfilProfesionalActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    EditText editNombreUsuario, editEmailUsuario, editContrasenaUsuario, editTelefonoUsuario,
            editDireccionUsuario, editCiudadUsuario, editCategoriaProfesional, editExperienciaProfesional;
    ImageView imgFotoPerfil;
    Button btnCambiarFoto, btnGuardarCambios;
    Uri imagenSeleccionadaUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar_perfil_profesional);

        // Enlazar vistas
        editNombreUsuario = findViewById(R.id.editNombreUsuario);
        editEmailUsuario = findViewById(R.id.editEmailUsuario);
        editContrasenaUsuario = findViewById(R.id.editContrasenaUsuario);
        editTelefonoUsuario = findViewById(R.id.editTelefonoUsuario);
        editDireccionUsuario = findViewById(R.id.editDireccionUsuario);
        editCiudadUsuario = findViewById(R.id.editCiudadUsuario);
        editCategoriaProfesional = findViewById(R.id.editCategoriaProfesional);
        editExperienciaProfesional = findViewById(R.id.editExperienciaProfesional);
        imgFotoPerfil = findViewById(R.id.imgFotoPerfil);
        btnCambiarFoto = findViewById(R.id.btnCambiarFoto);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);

        // TODO: Sustituye esto por tu lógica real para obtener datos del usuario
        cargarDatosUsuario();

        btnCambiarFoto.setOnClickListener(v -> abrirGaleria());

        btnGuardarCambios.setOnClickListener(v -> {
            guardarCambios();
        });
    }

    private void cargarDatosUsuario() {
        // Simulación de carga de datos
        editNombreUsuario.setText("Nombre actual");
        editEmailUsuario.setText("usuario@correo.com");
        editContrasenaUsuario.setText("123456");
        editTelefonoUsuario.setText("666666666");
        editDireccionUsuario.setText("Calle Falsa 123");
        editCiudadUsuario.setText("Sevilla");
        editCategoriaProfesional.setText("Electricista");
        editExperienciaProfesional.setText("5 años de experiencia en instalaciones eléctricas.");
    }

    private void guardarCambios() {
        String nombre = editNombreUsuario.getText().toString();
        String email = editEmailUsuario.getText().toString();
        String contrasena = editContrasenaUsuario.getText().toString();
        String telefono = editTelefonoUsuario.getText().toString();
        String direccion = editDireccionUsuario.getText().toString();
        String ciudad = editCiudadUsuario.getText().toString();
        String categoria = editCategoriaProfesional.getText().toString();
        String experiencia = editExperienciaProfesional.getText().toString();

        // Aquí puedes llamar a tu API o guardar en base de datos
        Toast.makeText(this, "Cambios guardados correctamente", Toast.LENGTH_SHORT).show();
    }

    private void abrirGaleria() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imagenSeleccionadaUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imagenSeleccionadaUri);
                imgFotoPerfil.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error al cargar imagen", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
