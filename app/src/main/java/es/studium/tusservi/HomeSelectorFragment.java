package es.studium.tusservi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import es.studium.tusservi.ui.home.HomeFragment;
import es.studium.tusservi.ui.homeProfesional.HomeProfesionalFragment;

public class HomeSelectorFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_selector, container, false);

        SharedPreferences preferences = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE);
        String tipoUsuario = preferences.getString("tipo_usuario", "cliente");

        Fragment fragment;
        if ("profesional".equals(tipoUsuario)) {
            fragment = new HomeProfesionalFragment();
        } else {
            fragment = new HomeFragment();
        }

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.home_fragment_container, fragment)
                .commit();

        return view;
    }
}

