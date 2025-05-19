package es.studium.tusservi.ui.homeProfesional;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import es.studium.tusservi.databinding.FragmentHomeProfesionalBinding;

public class HomeProfesionalFragment extends Fragment {

    private FragmentHomeProfesionalBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeProfesionalViewModel homeProfesionalViewModel =
                new ViewModelProvider(this).get(HomeProfesionalViewModel.class);

        binding = FragmentHomeProfesionalBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textHomeProfesional;
        homeProfesionalViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

