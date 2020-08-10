package application.example.mypen.ui.generate_new_font;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GenerateNewFontViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public GenerateNewFontViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is Generate New Font fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}