package es.studium.tusservi;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import es.studium.tusservi.ui.profile.ProfileFragment;
import es.studium.tusservi.ui.profile.ProfileProfesionalFragment;

public class ProfileSelectorFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile_selector, container, false);

        SharedPreferences preferences = requireActivity().getSharedPreferences("session", Context.MODE_PRIVATE);
        String tipoUsuario = preferences.getString("tipo_usuario", "cliente");

        Fragment fragment;
        if ("profesional".equals(tipoUsuario)) {
            fragment = new ProfileProfesionalFragment();
        } else {
            fragment = new ProfileFragment();
        }

        requireActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.profile_fragment_container, fragment)
                .commit();

        return view;
    }
}

