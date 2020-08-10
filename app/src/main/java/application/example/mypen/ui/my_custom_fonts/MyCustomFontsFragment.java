package application.example.mypen.ui.my_custom_fonts;

import android.content.ContextWrapper;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import application.example.mypen.CustomAdapterMyCustomFont;
import application.example.mypen.CustomAdapterMyFont;
import application.example.mypen.R;
import application.example.mypen.SpacesItemDecoration;

public class MyCustomFontsFragment extends Fragment {

    private MyCustomFontViewModel myCustomFontViewModel;
    ContextWrapper cw;
    CustomAdapterMyCustomFont customAdapter;
    RecyclerView recyclerView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        myCustomFontViewModel =
                ViewModelProviders.of(this).get(MyCustomFontViewModel.class);
        View root = inflater.inflate(R.layout.fragment_my_custom_fonts, container, false);

        cw = new ContextWrapper(getActivity().getApplicationContext());
        // get the reference of RecyclerView
        recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);
        recyclerView.addItemDecoration(new SpacesItemDecoration(
                getResources().getDimensionPixelSize(R.dimen.spacing),
                getResources().getInteger(R.integer.font_list_columns)));
        // set a GridLayoutManager with default vertical orientation and 2 number of columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(),2);
        recyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        //  call the constructor of CustomAdapter to send the reference and data to Adapter

        customAdapter = new CustomAdapterMyCustomFont(getActivity().getApplicationContext(), cw);
        recyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView
//        customAdapter.setHasStableIds(true);


//        final TextView textView = root.findViewById(R.id.text_slideshow);
//        myCustomFontViewModel.getText().observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
        return root;
    }
}