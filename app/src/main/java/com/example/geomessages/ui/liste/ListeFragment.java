package com.example.geomessages.ui.liste;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.geomessages.R;
import com.example.geomessages.data.AppExecutors;
import com.example.geomessages.data.MessagesRoomDatabase;
import com.example.geomessages.databinding.FragmentListeBinding;
import com.example.geomessages.http.VolleyUtils;
import com.example.geomessages.model.Message;
import com.example.geomessages.ui.MessageAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListeFragment extends Fragment {

    private FragmentListeBinding binding;
    private RecyclerView rvMessages;
    private MessageAdapter messageAdapter;
    private ArrayList<Message> messages;
    private MessagesRoomDatabase mDb;
    private ListeViewModel listeViewModel;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        listeViewModel = new ViewModelProvider(requireActivity()).get(ListeViewModel.class);

        binding = FragmentListeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
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

        listeViewModel.getMessages().observe(getViewLifecycleOwner(), new Observer<List<Message>>() {
            @Override
            public void onChanged(List<Message> messagesList) {
                messages.clear();
                messages.addAll(messagesList);
                messageAdapter.notifyDataSetChanged();
            }
        });

    }

    public void loadMessages() {
        if (listeViewModel.getMessages().getValue() == null || listeViewModel.getMessages().getValue().size() < 1) {
            new VolleyUtils().getMessages(getContext(), new VolleyUtils.ListMessagesAsyncResponse() {
                @Override
                public void processFinished(ArrayList<Message> messagesArrayList) {
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mDb.messageDao().deleteAll();
                            for (Message article : messagesArrayList) {
                                mDb.messageDao().insert(article);
                            }
                        }
                    });
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