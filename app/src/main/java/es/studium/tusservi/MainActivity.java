package es.studium.tusservi;

import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import es.studium.tusservi.databinding.ActivityMainBinding;
/**
 * Clase MainActivity
 *
 * Esta clase representa la actividad principal de la aplicación TusServi.
 * Es el punto de entrada después del login y contiene la barra de navegación inferior (BottomNavigationView)
 * para moverse entre las secciones principales: Inicio (Home), Chat y Perfil.
 *
 * Utiliza Navigation Component para gestionar la navegación entre fragmentos mediante un NavHostFragment.
 *
 * Hereda de AppCompatActivity para tener soporte con compatibilidad extendida de Android.
 */
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);

        // Referencia al NavHostFragment
        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment_activity_main);
        NavController navController = navHostFragment.getNavController();

        // Configura los destinos de nivel superior (los íconos del BottomNavigationView)
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_chat, R.id.navigation_profile)
                .build();

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }
}
