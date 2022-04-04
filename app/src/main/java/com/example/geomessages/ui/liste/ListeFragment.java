package com.example.geomessages.ui.liste;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.geomessages.databinding.FragmentListeBinding;

import org.json.JSONException;
import org.json.JSONObject;

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

        RequestQueue queue = Volley.newRequestQueue(requireContext());
        String url = "https://onoup.site/data.json";

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, response ->
                {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            Log.d("TAG", "response: " + jsonObject.getString("picture"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                        error -> Log.d("TAG", "error: " + error));
        queue.add(jsonArrayRequest);


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}