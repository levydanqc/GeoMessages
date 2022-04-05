package com.example.geomessages.ui.maps;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.geomessages.data.MessagesRoomDatabase;
import com.example.geomessages.model.Message;

import java.util.List;

public class MapsViewModel extends AndroidViewModel {

    private final LiveData<List<Message>> messages;

    public MapsViewModel(Application application) {
        super(application);
        MessagesRoomDatabase mDb = MessagesRoomDatabase.getDatabase(application);
        messages = mDb.messageDao().getMessages();
    }

    public LiveData<List<Message>> getMessages() {
        return messages;
    }
}