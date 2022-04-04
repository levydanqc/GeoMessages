package com.example.geomessages.ui.liste;

import android.content.Context;
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
import com.example.geomessages.model.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListeFragment extends Fragment {

    private FragmentListeBinding binding;
    private ArrayList<Message> messages = new ArrayList<>();

    public interface ListMessagesAsyncResponse {
        void processFinished(ArrayList<Message> messagesArrayList);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        ListeViewModel listeViewModel =
                new ViewModelProvider(this).get(ListeViewModel.class);

        binding = FragmentListeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textListe;
        listeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        getMessages(requireContext(), new ListMessagesAsyncResponse() {
            @Override
            public void processFinished(ArrayList<Message> messagesArrayList) {
                Log.d("TAG", "finished: " + messagesArrayList.size());
            }
        });

        return root;
    }

    public void getMessages(Context context, ListMessagesAsyncResponse callback) {
        String url = "https://onoup.site/data.json";
        RequestQueue queue = Volley.newRequestQueue(requireContext());

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, response ->
                {
                    for (int i = 0; i < response.length(); i++) {
                        Message message = new Message();
                        try {
                            JSONObject jsonObject = response.getJSONObject(i);
                            message.setFirstname(jsonObject.getString("firstname"));
                            message.setLastname(jsonObject.getString("lastname"));
                            message.setLongitude(jsonObject.getString("longitude"));
                            message.setLatitude(jsonObject.getString("latitude"));
                            message.setPicture(jsonObject.getString("picture"));
                            message.setMessage(jsonObject.getString("message"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        messages.add(message);
                    }
                    if (callback != null) callback.processFinished(messages);
                },
                        error -> Log.d("TAG", "error: " + error));
        queue.add(jsonArrayRequest);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}