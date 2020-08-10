package application.example.mypen.ui.generate_new_font;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import application.example.mypen.R;

public class GenerateNewFontFragment extends Fragment {

    private GenerateNewFontViewModel generateNewFontViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        generateNewFontViewModel =
                ViewModelProviders.of(this).get(GenerateNewFontViewModel.class);
        View root = inflater.inflate(R.layout.fragment_generate_new_font, container, false);
//        final TextView textView = root.findViewById(R.id.text_generate_new_font);
//        generateNewFontViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }
}