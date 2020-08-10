package application.example.mypen.ui.my_custom_fonts;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MyCustomFontViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public MyCustomFontViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is My Custom Fonts fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}