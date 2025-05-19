package es.studium.tusservi.ui.chatProfesional;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ChatProfesionalViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public ChatProfesionalViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Chat Profesional fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

}