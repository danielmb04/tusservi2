package es.studium.tusservi.ui.chatProfesional;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import es.studium.tusservi.databinding.FragmentChatProfesionalBinding;

public class ChatProfesionalFragment extends Fragment {

    private FragmentChatProfesionalBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ChatProfesionalViewModel chatProfesionalViewModel =
                new ViewModelProvider(this).get(ChatProfesionalViewModel.class);

        binding = FragmentChatProfesionalBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textChatProfesional;
        chatProfesionalViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}

