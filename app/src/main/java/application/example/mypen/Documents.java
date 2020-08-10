package application.example.mypen;

import android.Manifest;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;

public class Documents extends Fragment {

    FloatingActionButton fab;
    ContextWrapper cw;
    CustomAdapterMyDocument customAdapter;
    RecyclerView recyclerView;

    private boolean is_write_permission_granted = false;

    private final int request_code_write_external_storage = 100;
    private final int request_code_read_external_storage = 200;

    //Overriden method onCreateView
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //Returning the layout file after inflating
        //Change R.layout.tab1 in you classes
        View root = inflater.inflate(R.layout.tab_documents, container, false);

//        my_fonts = new File(getActivity().getApplicationContext().getFilesDir(), "my_fonts");
//        TextView myTextView=(TextView)root.findViewById(R.id.textView);
//        Typeface typeFace=Typeface.createFromFile(new File(my_fonts.toString() + "/AryanFont.ttf"));
//        myTextView.setTypeface(typeFace);

        cw = new ContextWrapper(getActivity().getApplicationContext());
        check_external_dir();
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

        customAdapter = new CustomAdapterMyDocument(getActivity().getApplicationContext(), cw, is_write_permission_granted);
        recyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView
//        customAdapter.setHasStableIds(true);


        fab = root.findViewById(R.id.fab_documents);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), GenerateDocument.class);
                view.getContext().startActivity(intent);
            }
        });

        return root;
    }

    private void check_external_dir() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            make_documents_folder();
            is_write_permission_granted = true;

        } else if (android.os.Build.VERSION.SDK_INT >= 23 && shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            Log.d("PERMISSION", "FIRST CONDITION");
            is_write_permission_granted = false;
        } else {
            if (android.os.Build.VERSION.SDK_INT >= 23) {
//                Log.d("PERMISSION", "SECOND CONDITION");
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, request_code_write_external_storage);
            }
            else {
                is_write_permission_granted = true;
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case request_code_write_external_storage:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
                    make_documents_folder();
                    is_write_permission_granted = true;

                }  else {
                    is_write_permission_granted = false;
                }
                return;
            case request_code_read_external_storage:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("READ", "CALLED");
                }  else {

                }
                return;
        }
        // Other 'case' lines to check for other
        // permissions this app might request.
    }

    private void make_documents_folder() {
        String folder_main = "MyPen";
        File f = new File(Environment.getExternalStorageDirectory(), folder_main);
        if (!f.exists()) {
            if (f.mkdir()) {
                File f1 = new File(Environment.getExternalStorageDirectory() + "/" + folder_main, "Documents");
                if (!f1.exists()) {
                    if (!f1.mkdir()) {
                        Log.d("ERROR", "DOCUMENTS FOLDER NOT CREATED IN EXTERNAL STORAGE");
                    }
                }
            }
            else{
                Log.d("ERROR", "MYPEN FOLDER NOT CREATED IN EXTERNAL STORAGE");
            }
        }
        else{
            File f1 = new File(Environment.getExternalStorageDirectory() + "/" + folder_main, "Documents");
            if (!f1.exists()) {
                if (!f1.mkdir()) {
                    Log.d("ERROR", "DOCUMENTS FOLDER NOT CREATED IN EXTERNAL STORAGE");
                }
            }
        }
    }

    @Override
    public void onResume() {
        customAdapter = new CustomAdapterMyDocument(getActivity().getApplicationContext(), cw, is_write_permission_granted);
        recyclerView.setAdapter(customAdapter);

        super.onResume();
    }



}
