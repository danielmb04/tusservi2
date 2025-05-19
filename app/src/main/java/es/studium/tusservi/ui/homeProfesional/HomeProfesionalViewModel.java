package es.studium.tusservi.ui.homeProfesional;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeProfesionalViewModel extends ViewModel {
    private final MutableLiveData<String> mText;

    public HomeProfesionalViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is home Profesional fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }

}
