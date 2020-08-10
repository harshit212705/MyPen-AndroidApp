package application.example.mypen;

import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroupOverlay;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class Fonts extends Fragment {

    //Overriden method onCreateView
    ContextWrapper cw;
    CustomAdapterMyFont customAdapter;
    RecyclerView recyclerView;
    boolean isRotate = false;
    Button download_template;
    Button draw_here;
    FloatingActionButton fab;
    LinearLayout fab_options;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Returning the layout file after inflating
//        Log.d("DEBUG", "onCreateView of FontFragment");


        View root = inflater.inflate(R.layout.tab_fonts, container, false);


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

        customAdapter = new CustomAdapterMyFont(getActivity().getApplicationContext(), cw);
        recyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView
//        customAdapter.setHasStableIds(true);


        fab = root.findViewById(R.id.fab_fonts);
        fab_options = (LinearLayout) root.findViewById(R.id.fab_options);
        download_template = (Button) root.findViewById(R.id.download_template);
        draw_here = (Button) root.findViewById(R.id.draw_here);
        ViewAnimation.init(download_template);
        ViewAnimation.init(draw_here);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewGroup root = (ViewGroup) getActivity().getWindow().getDecorView().getRootView();
                isRotate = ViewAnimation.rotateFab(view, !isRotate);
                if(isRotate){
//                    applyDim(root, 0.8f);
                    ViewAnimation.showIn(download_template);
                    ViewAnimation.showIn(draw_here);
//                    clearDim((ViewGroup) download_template.getParent());
                }else{
//                    clearDim(root);
                    ViewAnimation.showOut(download_template);
                    ViewAnimation.showOut(draw_here);
                }

//                Intent intent = new Intent(view.getContext(), NewFont.class);
//                view.getContext().startActivity(intent);
            }
        });

        download_template.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DownloadTemplate.class);
                view.getContext().startActivity(intent);
            }
        });

        draw_here.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DrawHere.class);
                intent.putExtra("is_saved", false);
                intent.putExtra("template_name", "");
                view.getContext().startActivity(intent);
            }
        });

        return root;
    }


    public static void applyDim(@NonNull ViewGroup parent, float dimAmount){
        Drawable dim = new ColorDrawable(Color.BLACK);
        dim.setBounds(0, 0, parent.getWidth(), parent.getHeight());
        dim.setAlpha((int) (255 * dimAmount));

        ViewGroupOverlay overlay = parent.getOverlay();
        overlay.add(dim);
    }

    public static void clearDim(@NonNull ViewGroup parent) {
        ViewGroupOverlay overlay = parent.getOverlay();
        overlay.clear();
    }

    @Override
    public void onResume() {
//        Log.d("DEBUG", "onResume of FontFragment");
        customAdapter = new CustomAdapterMyFont(getActivity().getApplicationContext(), cw);
        recyclerView.setAdapter(customAdapter);


        if (isRotate) {
            ViewAnimation.init(download_template);
            ViewAnimation.init(draw_here);
            isRotate = ViewAnimation.rotateFab(fab, !isRotate);
        }
        super.onResume();
    }


}
