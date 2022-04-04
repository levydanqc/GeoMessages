package com.example.geomessages.ui.liste;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.geomessages.databinding.FragmentListeBinding;

public class ListeFragment extends Fragment {

    private FragmentListeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ListeViewModel listeViewModel =
                new ViewModelProvider(this).get(ListeViewModel.class);

        binding = FragmentListeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textListe;
        listeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}