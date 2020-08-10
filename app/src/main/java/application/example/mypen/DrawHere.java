package application.example.mypen;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DrawHere extends AppCompatActivity implements View.OnClickListener {

    ArrayList characters = new ArrayList<>(Arrays.asList("\"", "%", "&", "'", "(", ")", ",","-", ".", "/", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ":", "=", "?" ,"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "!", "#", "$", "*", "+", ";", "<", ">", "@", "[", "\\", "]", "^", "_", "`", "{", "|", "}", "~"));
    ArrayList unicode_values = new ArrayList<>(Arrays.asList("x22", "x25", "x26", "x27", "x28", "x29", "x2c", "x2d", "x2e", "x2f", "x30", "x31", "x32", "x33", "x34", "x35", "x36", "x37", "x38", "x39", "x3a", "x3d", "x3f", "x41", "x42", "x43", "x44", "x45", "x46", "x47", "x48", "x49", "x4a", "x4b", "x4c", "x4d", "x4e", "x4f", "x50", "x51", "x52", "x53", "x54", "x55", "x56", "x57", "x58", "x59", "x5a", "x61", "x62", "x63", "x64", "x65", "x66", "x67", "x68", "x69", "x6a", "x6b", "x6c", "x6d", "x6e", "x6f", "x70", "x71", "x72", "x73", "x74", "x75", "x76", "x77", "x78", "x79", "x7a", "x21", "x23", "x24", "x2a", "x2b", "x3b", "x3c", "x3e", "x40", "x5b", "x5c", "x5d", "x5e", "x5f", "x60", "x7b", "x7c", "x7d", "x7e"));
    ContextWrapper cw;
    Runnable ans_true, ans_false;
    private File template_draw_here, unsaved, my_fonts;
    Button save_template, generate_new_font;
    private String template_name = "";
    private boolean is_saved = false;
    RelativeLayout parentlayout;

    private myDbAdapter helper;

    Intent intent;

    TextInputLayout errorInputLayout;
    TextInputEditText errorEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_draw_here);

        parentlayout = (RelativeLayout) this.findViewById(R.id.relativelayout);

        intent = getIntent();
        is_saved = intent.getBooleanExtra("is_saved", false);
        template_name = intent.getStringExtra("template_name");

        if (!is_saved) {
            this.setTitle("Untitled Template");
        }
        else {
            this.setTitle(template_name);
        }

        helper = new myDbAdapter(getApplicationContext());

        // get the reference of RecyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // set a GridLayoutManager with default vertical orientation and 2 number of columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),4);
        recyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        //  call the constructor of CustomAdapter to send the reference and data to Adapter

        cw = new ContextWrapper(getApplicationContext());
        template_draw_here = cw.getDir("template_draw_here", Context.MODE_PRIVATE);
        my_fonts = new File(getApplicationContext().getFilesDir(), "my_fonts");

//        save_template = (Button) findViewById(R.id.save_template_btn);
//        generate_new_font = (Button) findViewById(R.id.generate_font_btn);

//        save_template.setOnClickListener(this);
//        generate_new_font.setOnClickListener(this);

        CustomAdapterDrawHere customAdapter = new CustomAdapterDrawHere(DrawHere.this, characters, unicode_values, cw, is_saved, template_name);
        recyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView
//        customAdapter.setHasStableIds(true);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_draw, menu);

//        MenuItem myMenuItem = menu.findItem(R.id.generate_font);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.generate_font:
                generate_font(item);
                return true;
            case R.id.save_template:
                save_and_update_template(item);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void generate_font(MenuItem item) {
        unsaved = new File(template_draw_here, "unsaved");
        if (unsaved.isDirectory() && unsaved.listFiles().length == 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("No characters found in your template!")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else {

            get_new_font(item.getActionView());

        }
    }

    public void save_and_update_template(MenuItem item) {
        unsaved = new File(template_draw_here, "unsaved");

        if (!is_saved) {

            if (unsaved.listFiles().length == 0) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("No characters found in your template!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //do things
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
            else {
                saving_template_as_new(item.getActionView());
            }
        }

        else {

            final Dialog dialog;
            dialog = new Dialog(this);
            dialog.setContentView(R.layout.save_and_save_as);

            Button save = (Button) dialog.findViewById(R.id.save_btn);
            Button save_as = (Button) dialog.findViewById(R.id.save_as_btn);

            save.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    view.setBackgroundColor(Color.parseColor("#ada99e"));

                    if (template_name.equals("")) {
                        // Means template is not saved and that condition can never arise
                    }
                    else{
                        File new_folder = new File(template_draw_here, "Template_" + template_name);
                        if (new_folder.isDirectory()) {
                            for (File child : new_folder.listFiles()) {
                                if(!child.delete()) {
                                    Log.d("ERROR IN SAVE ONCLICK", "IN FILE DELETION WHILE SAVING A ALREADY SAVED TEMPLATE");
                                }
                            }
                        }

                        unsaved = new File(template_draw_here, "unsaved");

                        if (unsaved.isDirectory()) {
                            for (File child : unsaved.listFiles()) {
                                copyFile(child.toString(), new_folder.toString() + "/" + child.getName());
                            }
                        }

                    }

                    dialog.dismiss();
                }
            });

            save_as.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    view.setBackgroundColor(Color.parseColor("#ada99e"));

                    saving_template_as_new(view);

                    dialog.dismiss();
                }
            });

            dialog.show();
        }
    }


    @Override
    public void onClick(View view) {
//        unsaved = new File(template_draw_here, "unsaved");
//        if (view == save_template) {
//            if (!is_saved) {
//
//                if (unsaved.listFiles().length == 0) {
//                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                    builder.setMessage("No characters found in your template!")
//                            .setCancelable(false)
//                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                public void onClick(DialogInterface dialog, int id) {
//                                    //do things
//                                }
//                            });
//                    AlertDialog alert = builder.create();
//                    alert.show();
//                }
//                else {
//                    saving_template_as_new(view);
//                }
//            }
//
//            else {
//
//                final Dialog dialog;
//                dialog = new Dialog(this);
//                dialog.setContentView(R.layout.save_and_save_as);
//
//                Button save = (Button) dialog.findViewById(R.id.save_btn);
//                Button save_as = (Button) dialog.findViewById(R.id.save_as_btn);
//
//                save.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View view) {
//                        view.setBackgroundColor(Color.parseColor("#ada99e"));
//
//                        if (template_name.equals("")) {
//                            // Means template is not saved and that condition can never arise
//                        }
//                        else{
//                            File new_folder = new File(template_draw_here, "Template_" + template_name);
//                            if (new_folder.isDirectory()) {
//                                for (File child : new_folder.listFiles()) {
//                                    if(!child.delete()) {
//                                        Log.d("ERROR IN SAVE ONCLICK", "IN FILE DELETION WHILE SAVING A ALREADY SAVED TEMPLATE");
//                                    }
//                                }
//                            }
//
//                            unsaved = new File(template_draw_here, "unsaved");
//
//                            if (unsaved.isDirectory()) {
//                                for (File child : unsaved.listFiles()) {
//                                    copyFile(child.toString(), new_folder.toString() + "/" + child.getName());
//                                }
//                            }
//
//                        }
//
//                        dialog.dismiss();
//                    }
//                });
//
//                save_as.setOnClickListener(new View.OnClickListener() {
//
//                    @Override
//                    public void onClick(View view) {
//                        view.setBackgroundColor(Color.parseColor("#ada99e"));
//
//                        saving_template_as_new(view);
//
//                        dialog.dismiss();
//                    }
//                });
//
//                dialog.show();
//            }
//        }
//
//        else if (view == generate_new_font) {
//
//            unsaved = new File(template_draw_here, "unsaved");
//            if (unsaved.isDirectory() && unsaved.listFiles().length == 0) {
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setMessage("No characters found in your template!")
//                        .setCancelable(false)
//                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                //do things
//                            }
//                        });
//                AlertDialog alert = builder.create();
//                alert.show();
//            }
//            else {
//
//                get_new_font(view);
//
//            }
//        }
    }


    public void get_new_font(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Generate Font");
        builder.setCancelable(false);
        // I'm using fragment here so I'm using getView() to provide ViewGroup
        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.text_input_font_name, null);
        // Set up the input

        errorEditText = (TextInputEditText) viewInflated.findViewById(R.id.errorEditText);
        errorInputLayout = (TextInputLayout) viewInflated.findViewById(R.id.errorInputLayout);

        errorEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                if (str.length() == 0) {
                    errorInputLayout.setError("Font name cannot be empty.");
                } else {
                    if (!(str.matches("[a-zA-Z0-9-_. ]*"))) {
                        errorInputLayout.setError("Font name can only contains alphanumeric letters, space, hyphen, underscore and period.");
                    } else {
                        File check_file = new File(my_fonts,  str + ".ttf");
                        if (check_file.exists()) {
                            errorInputLayout.setError("Font with same name already exists.");
                        } else {
                            errorInputLayout.setError(null);
                        }
                    }
                }
            }
        });


        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (errorEditText.getError() == null || errorEditText.getError() == "") {
                    dialog.dismiss();
                    final String font_name = errorEditText.getText().toString();
                    Log.d("FONT NAME", font_name);

                    final ProgressDialog progressDialog = new ProgressDialog(DrawHere.this);
                    progressDialog.setCancelable(false);
                    progressDialog.setMessage("Please Wait...It may take a few seconds.");
                    progressDialog.setTitle("Generating Font");
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    progressDialog.show();

                    Log.d("FONT NAME", "a");

                    MultipartBody.Part[] charImages = new MultipartBody.Part[unsaved.listFiles().length];
                    int index = 0;
                    for (File image : unsaved.listFiles()) {
                        RequestBody charBody = RequestBody.create(MediaType.parse("image/*"),
                                image);
                        charImages[index] = MultipartBody.Part.createFormData("images[]",
                                image.getName(),
                                charBody);
                        index++;
                    }
                    Log.d("FONT NAME", "b");

                    APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
                    Call<GenerateFontResponse> call = apiInterface.getGenerateFontBodyResponse(charImages, font_name);
                    call.enqueue(new Callback<GenerateFontResponse>() {

                        @Override
                        public void onResponse(Call<GenerateFontResponse> call, Response<GenerateFontResponse> response) {
//                        Log.d("BODY", "" + response.body());
                            response.body().setContext(getApplicationContext());
                            Log.d("NAME", "" + response.body().getName());
                            Log.d("CONTENT64", "" + response.body().getContent64());
                            response.body().save();

                            long uid = helper.insertData(response.body().getName(), response.body().getLowercase(), response.body().getUppercase(), response.body().getNumbers(), response.body().getSymbols());
                            Log.d("FONT DETAILS", "INSERTED INTO THE DATABASE");

                            progressDialog.dismiss();

//                            File new_file = new File(my_fonts.toString() + "/" + response.body().getName());
//                            Path file = new_file.toPath();
//                            BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);

                            Snackbar.make(parentlayout, "Your Font has been generated and saved!!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                            Log.d("RESPONSE CODE", "onResponse: response code: retrofit: " + response.code());
                        }

                        @Override
                        public void onFailure(Call<GenerateFontResponse> call, Throwable t) {
                            Log.d("RESPONSE CODE", "onResponse: NO RESPONSE");
                        }
                    });

                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }


    public void saving_template_as_new(View view) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Save Template");
        builder.setCancelable(false);
        // I'm using fragment here so I'm using getView() to provide ViewGroup
        // but you can provide here any other instance of ViewGroup from your Fragment / Activity
        View viewInflated = LayoutInflater.from(this).inflate(R.layout.text_input_template_name, null);
        // Set up the input

        errorEditText = (TextInputEditText) viewInflated.findViewById(R.id.errorEditText);
        errorInputLayout = (TextInputLayout) viewInflated.findViewById(R.id.errorInputLayout);

        errorEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String str = s.toString();
                if (str.length() == 0) {
                    errorInputLayout.setError("File name cannot be empty.");
                } else {
                    if (!(str.matches("[a-zA-Z0-9-_. ]*"))) {
                        errorInputLayout.setError("File name can only contains alphanumeric letters, space, hyphen, underscore and period.");
                    } else {
                        File check_file = new File(template_draw_here, "Template_" + str);
                        if (check_file.exists()) {
                            errorInputLayout.setError("File with same name already exists.");
                        } else {
                            errorInputLayout.setError(null);
                        }
                    }
                }
            }
        });


        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        builder.setView(viewInflated);

        // Set up the buttons
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (errorEditText.getError() == null || errorEditText.getError() == "") {
                    dialog.dismiss();
                    template_name = errorEditText.getText().toString();
                    Log.d("NAME", template_name);

                    unsaved = new File(template_draw_here, "unsaved");
                    File new_folder = new File(template_draw_here, "Template_" + template_name);

                    if (!new_folder.mkdir()) {
                        Log.d("ERROR IN", "SAVING TEMPLATE NEW DIRECTORY NOT CREATED");
                    }
                    else {
                        if (unsaved.isDirectory()) {
                            for (File child : unsaved.listFiles()) {
                                copyFile(child.toString(), new_folder.toString() + "/" + child.getName());
                            }
                        }
                    }
                    is_saved = true;

                    setTitle(template_name);

                }
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }



    public static boolean copyFile(String from, String to) {
        try {
            int bytesum = 0;
            int byteread = 0;
            File oldfile = new File(from);
            if (oldfile.exists()) {
                InputStream inStream = new FileInputStream(from);
                FileOutputStream fs = new FileOutputStream(to);
                byte[] buffer = new byte[1444];
                while ((byteread = inStream.read(buffer)) != -1) {
                    bytesum += byteread;
                    fs.write(buffer, 0, byteread);
                }
                inStream.close();
                fs.close();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public void onBackPressed() {
        DialogHandler appdialog = new DialogHandler();
        appdialog.Confirm(this, "Confirmation", "Discard changes?",
                "Cancel", "OK", aproc(), bproc());
    }


    @Override
    public void onDestroy() {
        unsaved = new File(template_draw_here, "unsaved");
        deleteRecursive(unsaved);
        Log.d("ONDESTROY", "CALLED FOR DRAW HERE");
        super.onDestroy();
    }

    public Runnable aproc(){
        return new Runnable() {
            public void run() {
                unsaved = new File(template_draw_here, "unsaved");
                deleteRecursive(unsaved);
                finish();
            }
        };
    }

    public Runnable bproc(){
        return new Runnable() {
            public void run() {

            }
        };
    }

    public boolean Confirm(Activity act, String Title, String ConfirmText,
                           String CancelBtn, String OkBtn, Runnable aProcedure, Runnable bProcedure) {
        ans_true = aProcedure;
        ans_false= bProcedure;
        AlertDialog dialog = new AlertDialog.Builder(act).create();
        dialog.setTitle(Title);
        dialog.setMessage(ConfirmText);
        dialog.setCancelable(false);
        dialog.setButton(DialogInterface.BUTTON_POSITIVE, OkBtn,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int buttonId) {
                        ans_true.run();
                    }
                });
        dialog.setButton(DialogInterface.BUTTON_NEGATIVE, CancelBtn,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int buttonId) {
                        ans_false.run();
                    }
                });
        dialog.setIcon(android.R.drawable.ic_dialog_alert);
        dialog.show();
        return true;
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

}
