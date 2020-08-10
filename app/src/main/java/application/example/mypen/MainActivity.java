package application.example.mypen;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.Menu;

import java.io.File;
import java.io.IOException;

public class MainActivity extends AppCompatActivity{

    private AppBarConfiguration mAppBarConfiguration;
    ContextWrapper cw;
    File template_draw_here, my_fonts, unsaved;
    private final int request_code_write_external_storage = 100;
    private final int request_code_read_external_storage = 200;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        cw = new ContextWrapper(getApplicationContext());
        template_draw_here = cw.getDir("template_draw_here", Context.MODE_PRIVATE);
        unsaved = new File(template_draw_here, "unsaved");

        if (unsaved.exists()) {
            deleteRecursive(unsaved);
        }

//        my_fonts = cw.getDir("my_fonts", Context.MODE_PRIVATE);
        my_fonts = new File(getApplicationContext().getFilesDir(), "my_fonts");
        if (!my_fonts.exists()) {
            if (!my_fonts.mkdir()) {
                Log.d("ERROR", "MY_FONTS FOLDER NOT CREATED IN ACTIVITY MAIN");
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
//            Log.d("MAIN ACTIVITY", "FIRST CONDITION CALLED");
            make_documents_folder();

        } else if (android.os.Build.VERSION.SDK_INT >= 23 && shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            Log.d("MAIN ACTIVITY", "SECOND CONDITION CALLED");
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected. In this UI,
            // include a "cancel" or "no thanks" button that allows the user to
            // continue using your app without granting the permission.
//            showInContextUI(...);
        } else {
            // You can directly ask for the permission.
//            Log.d("MAIN ACTIVITY", "THIRD CONDITION CALLED");
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, request_code_write_external_storage);
            }
        }


//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
//            // You can use the API that requires the permission.
//            Log.d("MAIN ACTIVITY", "FIRST CONDITION CALLED");
////            make_documents_folder();
//
//        } else if (android.os.Build.VERSION.SDK_INT >= 23 && shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
////            Log.d("MAIN ACTIVITY", "SECOND CONDITION CALLED");
//            // In an educational UI, explain to the user why your app requires this
//            // permission for a specific feature to behave as expected. In this UI,
//            // include a "cancel" or "no thanks" button that allows the user to
//            // continue using your app without granting the permission.
////            showInContextUI(...);
//        } else {
//            // You can directly ask for the permission.
//            Log.d("MAIN ACTIVITY", "THIRD CONDITION CALLED");
//            if (android.os.Build.VERSION.SDK_INT >= 23) {
//                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, request_code_read_external_storage);
//            }
//        }


//        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
//        float dpHeight = displayMetrics.heightPixels / displayMetrics.density;
//        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;


        // code for getting saved image dimensions

//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(template_draw_here.toString() + "/Template_Dimen/x36.png", options);
//        int imageHeight = options.outHeight;
//        int imageWidth = options.outWidth;
//        Log.d("IMAGE DIMENSIONS", "" + imageHeight + " " + imageWidth);

//        BitmapFactory.decodeFile(template_draw_here.toString() + "/unsaved/x54.png", options);
//        Log.d("IMAGE DIMENSIONS", "" + options.outHeight + " " + options.outWidth);


//        File new_folder = cw.getDir("new_folder", Context.MODE_PRIVATE);
//        File template = new File("/data/user/0/application.example.mypen/app_template_draw_here");
//        File file = new File("/data/user/0/application.example.mypen/app_new_folder");


        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_generate_new_font, R.id.nav_my_custom_fonts,
                R.id.nav_tools, R.id.nav_share, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
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

                }  else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
                }
                return;
            case request_code_read_external_storage:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.
//                    make_documents_folder();
                    Log.d("READ", "CALLED");
                }  else {
                    // Explain to the user that the feature is unavailable because
                    // the features requires a permission that the user has denied.
                    // At the same time, respect the user's decision. Don't link to
                    // system settings in an effort to convince the user to change
                    // their decision.
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


    public void deleteRecursive(File fileOrDirectory) {

        if (fileOrDirectory.isDirectory()) {
            for (File child : fileOrDirectory.listFiles()) {
//                deleteRecursive(child);
                try {
                    boolean a = child.delete();
                    Log.d("DELETED", "" + a);
                }
                catch (Exception e) {
                    Log.d("ERROR", "" + e);
                }
            }
        }

        fileOrDirectory.delete();
//        try {
//            boolean a = fileOrDirectory.delete();
//            Log.d("DELETED", "" + a);
//        }
//        catch (Exception e) {
//            Log.d("ERROR", "" + e);
//        }
    }

    @Override
    public void onDestroy() {
        File unsaved = new File(template_draw_here, "unsaved");
        if (unsaved.exists()) {
            deleteRecursive(unsaved);
        }
        Log.d("ONDESTROY", "CALLED FOR MAIN ACTIVITY");
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }


    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }
}
