package es.studium.tusservi.ui.chat;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import es.studium.tusservi.R;

public class FullScreenImageActivity extends AppCompatActivity {

    ImageView fullScreenImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_screen_image);

        fullScreenImageView = findViewById(R.id.fullScreenImageView);

        // Obtener la URL o ruta de la imagen desde el intent
        String imageUrl = getIntent().getStringExtra("imageUrl");

        // Cargar la imagen (usando Glide)
        Glide.with(this)
                .load(imageUrl)
                .into(fullScreenImageView);

        // Cerrar al hacer clic
        fullScreenImageView.setOnClickListener(v -> finish());
    }
}

