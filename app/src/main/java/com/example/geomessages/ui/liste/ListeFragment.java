package com.example.geomessages.ui.liste;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.example.geomessages.R;
import com.example.geomessages.data.AppExecutors;
import com.example.geomessages.data.MessagesRoomDatabase;
import com.example.geomessages.databinding.FragmentListeBinding;
import com.example.geomessages.model.Message;
import com.example.geomessages.ui.MessageAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ListeFragment extends Fragment {

    private FragmentListeBinding binding;
    private RecyclerView rvMessages;
    private MessageAdapter messageAdapter;
    private ArrayList<Message> messages;
    private MessagesRoomDatabase mDb;
    private ListeViewModel listeViewModel;

    public interface ListMessagesAsyncResponse {
        void processFinished(ArrayList<Message> messagesArrayList);
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listeViewModel = new ViewModelProvider(requireActivity()).get(ListeViewModel.class);

        binding = FragmentListeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    public void getMessages(Context context, ListMessagesAsyncResponse callback) {
        ArrayList<Message> messages = new ArrayList<>();
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
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDb = MessagesRoomDatabase.getDatabase(getContext());

        rvMessages = view.findViewById(R.id.rv_messages);
        messages = new ArrayList<>();
        messageAdapter = new MessageAdapter(messages);
        rvMessages.setLayoutManager(new LinearLayoutManager(requireActivity()));
        rvMessages.setAdapter(messageAdapter);

        if (listeViewModel.getMessages().getValue() == null || listeViewModel.getMessages().getValue().size() < 1) {
            getMessages(getContext(), new ListMessagesAsyncResponse() {
                @Override
                public void processFinished(ArrayList<Message> messagesArrayList) {
                    AppExecutors.getInstance().diskIO().execute(
                            () -> {
                                for (Message message : messagesArrayList) {
                                    mDb.messageDao().insert(message);
                                }
                            }
                    );
                    messages.addAll(messagesArrayList);
                    messageAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}