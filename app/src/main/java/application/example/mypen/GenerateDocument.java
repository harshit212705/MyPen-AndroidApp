package application.example.mypen;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GenerateDocument extends AppCompatActivity {

    Intent intent;
    private Spinner font_list;
    private Spinner font_ink_color_list;
    File my_fonts, my_documents;
    Button generate_document, upload_file_btn;
    TextView file_name, font_size;
    ImageView font_size_minus, font_size_plus;
    SwitchCompat paper_margin, paper_lines;

    LinearLayout parentlayout;

    MultipartBody.Part multipartBody;

    TextInputLayout errorInputLayout;
    TextInputEditText errorEditText;

    private myDbAdapter helper;

    private String upload_file_path = "";
    private String upload_file_content = "";
    private String upload_file_content_1 = "";
    private String upload_file_name = "";

    private final int request_code_write_external_storage = 100;
    private final int request_code_read_external_storage = 200;
    private final int request_code_select_file = 300;

    private HashMap<String,Integer> all_lowercase = new HashMap<>();
    private HashMap<String,Integer> all_uppercase = new HashMap<>();
    private HashMap<String,Integer> all_numbers = new HashMap<>();
    private HashMap<String,Integer> all_symbols = new HashMap<>();

    ArrayList lowercase_characters = new ArrayList<>(Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"));
    ArrayList uppercase_characters = new ArrayList<>(Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"));
    ArrayList numbers_characters = new ArrayList<>(Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9"));
    ArrayList symbols_characters = new ArrayList<>(Arrays.asList("\"", "%", "&", "'", "(", ")", ",","-", ".", "/", ":", "=", "?", "!", "#", "$", "*", "+", ";", "<", ">", "@", "[", "\\", "]", "^", "_", "`", "{", "|", "}", "~"));


    private boolean is_write_permission_granted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_document);

        my_fonts = new File(getApplicationContext().getFilesDir(), "my_fonts");
        intent = getIntent();

        is_write_permission_granted = false;
        check_external_dir();
        if (!is_write_permission_granted) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Unable to generate document!! Write to External Storage Permission denied!!\n\n To enable, Go to Settings-> Apps-> MyPen-> Allow Permissions-> Storage(Turn it ON). ")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //do things
                            finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
        else{
            my_documents = new File(Environment.getExternalStorageDirectory() + "/MyPen", "Documents");
        }

        file_name = (TextView) findViewById(R.id.file_name);
        file_name.setText("");

        parentlayout = (LinearLayout) this.findViewById(R.id.linearlayout);

        helper = new myDbAdapter(getApplicationContext());

        for (int i = 0;i < lowercase_characters.size();i++) {
            all_lowercase.put(lowercase_characters.get(i).toString(), 1);
        }

        for (int i = 0;i < uppercase_characters.size();i++) {
            all_uppercase.put(uppercase_characters.get(i).toString(), 1);
        }

        for (int i = 0;i < numbers_characters.size();i++) {
            all_numbers.put(numbers_characters.get(i).toString(), 1);
        }

        for (int i = 0;i < symbols_characters.size();i++) {
            all_symbols.put(symbols_characters.get(i).toString(), 1);
        }

        addItemsOnFontList();
        addListenerOnButton();
        addCheckOnDocumentName();
        addItemsOnFontInkColor();

    }

    private void check_external_dir() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            make_documents_folder();
            is_write_permission_granted = true;

        } else if (android.os.Build.VERSION.SDK_INT >= 23 && shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
//            Log.d("PERMISSION", "FIRST CONDITION");
            is_write_permission_granted = false;
        } else {
            if (android.os.Build.VERSION.SDK_INT >= 23) {
//                Log.d("PERMISSION", "SECOND CONDITION");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, request_code_write_external_storage);
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



    private void addCheckOnDocumentName() {
        errorEditText = (TextInputEditText) findViewById(R.id.errorEditText);
        errorInputLayout = (TextInputLayout) findViewById(R.id.errorInputLayout);

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
                    errorInputLayout.setError("Document name cannot be empty.");
                } else {
                    if (!(str.matches("[a-zA-Z0-9-_. ]*"))) {
                        errorInputLayout.setError("Document name can only contains alphanumeric letters, space, hyphen, underscore and period.");
                    } else {
                        check_external_dir();
                        if (!is_write_permission_granted) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                            builder.setMessage("Unable to generate document!! Write to External Storage Permission denied!!")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //do things
                                            finish();
                                        }
                                    });
                            AlertDialog alert = builder.create();
                            alert.show();
                        }
                        else{
                            my_documents = new File(Environment.getExternalStorageDirectory() + "/MyPen", "Documents");
                        }
                        File check_file = new File(my_documents,  str + ".pdf");
                        if (check_file.exists()) {
                            errorInputLayout.setError("Document with same name already exists.");
                        } else {
                            errorInputLayout.setError(null);
                        }
                    }
                }
            }
        });
    }


    private void addItemsOnFontInkColor() {

        font_ink_color_list = (Spinner) findViewById(R.id.font_ink_color_list);
        List<String> list = new ArrayList<String>();
        list.add("Blue");
        list.add("Black");
        list.add("Red");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        font_ink_color_list.setAdapter(dataAdapter);
    }


    // add items into spinner dynamically
    private void addItemsOnFontList() {

        font_list = (Spinner) findViewById(R.id.font_list);
        List<String> list = new ArrayList<String>();
        list.add("---SELECT FONT---");
        if (my_fonts.isDirectory()) {
            for (File child : my_fonts.listFiles()) {
                list.add(child.getName().toString().substring(0, child.getName().toString().length() - 4));
            }
        }

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        font_list.setAdapter(dataAdapter);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        // check if the request code is same as what is passed  here it is 300

        if(requestCode == request_code_select_file)
        {
            if (data != null) {
//                path = data.getStringExtra("dat");
                Uri uri = data.getData();
                if (uri != null) {
                    File file = new File(uri.getPath());
                    upload_file_path = file.getAbsolutePath();

                    RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                    multipartBody = MultipartBody.Part.createFormData("text_file",file.getName(),requestFile);

                    String[] words = upload_file_path.split(":");
                    upload_file_name = words[words.length - 1];
                    words = upload_file_name.split("/");
                    upload_file_name = words[words.length - 1];


                    file_name.setText(upload_file_name);


                    try {
                        InputStream inputStream = getContentResolver().openInputStream(uri);
                        BufferedReader r = new BufferedReader(new InputStreamReader(inputStream));
                        String x = "";
                        x = r.readLine();

                        upload_file_content = "";
                        upload_file_content_1 = "";
                        while (x != null) {
                            upload_file_content += x;
                            upload_file_content_1 += x;
                            upload_file_content_1 += '\n';
                            x = r.readLine();
                        }

                        r.close();
                        inputStream.close();

                    } catch (FileNotFoundException e) {
                        Log.d("FILE NOT FOUND", "IN GENERATE DOCUMENT");
                    } catch (IOException e) {
                        Log.d("CONTENT NOT FOUND", "IN FILE IN GENERATE DOCUMENT");
                    }
                }
            }
        }
    }


    public void addListenerOnButton() {

        generate_document = (Button) findViewById(R.id.generate_document);
        upload_file_btn = (Button) findViewById(R.id.upload_document);
        font_size = (TextView) findViewById(R.id.txtNumbers);
        font_size_minus = (ImageView) findViewById(R.id.imgMinus);
        font_size_plus = (ImageView) findViewById(R.id.imgPlus);
        paper_margin = (SwitchCompat) findViewById(R.id.switch_button_paper_margin);
        paper_lines = (SwitchCompat) findViewById(R.id.switch_button_paper_lines);

        paper_margin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        paper_lines.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });

        font_size_minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = Integer.parseInt(font_size.getText().toString());
                if (size > 10) {
                    size--;
                }
                font_size.setText("" + size);
            }
        });

        font_size_plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int size = Integer.parseInt(font_size.getText().toString());
                if (size < 28) {
                    size++;
                }
                font_size.setText("" + size);
            }
        });

        upload_file_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("text/*");
                startActivityForResult(intent, request_code_select_file);
            }
        });

        generate_document.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_external_dir();
                if (!is_write_permission_granted) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                    builder.setMessage(Html.fromHtml("Unable to generate document!! Write to External Storage Permission denied!!<br><br> To enable, Go to <b>Settings</b>-> <b>Apps</b>-> <b>MyPen</b>-> <b>Allow Permissions</b>-> <b>Storage</b>(Turn it <b>ON</b>). "))
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //do things
                                    finish();
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else{
                    my_documents = new File(Environment.getExternalStorageDirectory() + "/MyPen", "Documents");

                    if (errorEditText.getText().toString().length() > 0 && (errorEditText.getError() == null || errorEditText.getError() == "")) {
                        if (font_list.getSelectedItemPosition() != 0) {
                            if (file_name.getText().toString() != "") {
                                String missing_characters = "";
                                String font_name = font_list.getSelectedItem().toString();
                                missing_characters = check_if_font_has_all_required_characters(missing_characters, font_name);
                                if (missing_characters.length() == 0) {
                                    get_new_document();
                                }
                                else{
                                    error_alert_dialog("Your font is missing the following characters required for generating given file <br>" + missing_characters + "<br>Try using another font!!");
                                }
                            }
                            else{
                                error_alert_dialog("No file selected!!");
                            }
                        }
                        else {
                            error_alert_dialog("No font selected!!");
                        }
                    }
                    else{
                        error_alert_dialog("Document name does not validate the specified requirement!!");
                    }
                }
                //check if all the characters that are on the document are there in the specified font or not
            }
        });
    }


    private void get_new_document() {

        final ProgressDialog progressDialog = new ProgressDialog(GenerateDocument.this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Please Wait...It may take a few seconds.");
        progressDialog.setTitle("Generating Document");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        String document_name = errorEditText.getText().toString();

        String font_name = font_list.getSelectedItem().toString();
        File file = new File(my_fonts, font_name + ".ttf");

//        Log.d("file path", file.toString());
        String font_file_content64 = getBase64FromPath(file.toString());
//        Log.d("font_file_content64", font_file_content64);
//        Log.d("font_file_content64 LEN", "" + font_file_content64.length());
        String font_size_no = font_size.getText().toString();
        String font_ink_color = font_ink_color_list.getSelectedItem().toString();
        String paper_margin_set = Boolean.toString(paper_margin.isChecked());
        String paper_lines_set = Boolean.toString(paper_lines.isChecked());

        byte[] data = upload_file_content.getBytes(StandardCharsets.UTF_8);
        String text_file_content64 = Base64.encodeToString(data, Base64.DEFAULT);
        Log.d("text_file_content64", upload_file_content_1);
        Log.d("text_file_content64 LEN", "" + text_file_content64.length());


        APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
        Call<GenerateDocumentResponse> call = apiInterface.getGenerateDocumentBodyResponse(document_name, font_file_content64, font_size_no, font_ink_color, paper_margin_set, paper_lines_set, upload_file_content_1);
        call.enqueue(new Callback<GenerateDocumentResponse>() {

            @Override
            public void onResponse(Call<GenerateDocumentResponse> call, Response<GenerateDocumentResponse> response) {
                progressDialog.dismiss();

                response.body().setContext(getApplicationContext());
                check_external_dir();
                if (is_write_permission_granted) {
                    response.body().save();
                    Snackbar.make(parentlayout, "Your Document has been generated and saved!!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                else {
                    Snackbar.make(parentlayout, "Your Document could not be saved because writing to external memory permission denied!!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

                Log.d("RESPONSE CODE", "onResponse: response code: retrofit: " + response.code());
            }

            @Override
            public void onFailure(Call<GenerateDocumentResponse> call, Throwable t) {
                Log.d("RESPONSE CODE", "onResponse: NO RESPONSE");
            }
            
        });

    }


    public static String getBase64FromPath(String path) {
        String base64 = "";
        try {
            File file = new File(path);
            byte[] buffer = new byte[(int) file.length() + 100];
            @SuppressWarnings("resource")
            int length = new FileInputStream(file).read(buffer);
            base64 = Base64.encodeToString(buffer, 0, length,
                    Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return base64;
    }


    private void error_alert_dialog(String error_str) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(Html.fromHtml(error_str))
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //do things
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private String check_if_font_has_all_required_characters(String missing_characters, String font_name) {

        String[] data = new String[6];
        data = helper.getData(font_name + ".ttf");

        String all_characters = data[2] + data[3] + data[4] + data[5];
//        Log.d("ALL CHARACTERS", all_characters);

        HashMap<String,Integer> map = new HashMap<>();
        SortedMap<String, Integer> missing_lowercase = new TreeMap<String, Integer>();
        SortedMap<String, Integer> missing_uppercase = new TreeMap<String, Integer>();
        SortedMap<String, Integer> missing_numbers = new TreeMap<String, Integer>();
        SortedMap<String, Integer> missing_symbols = new TreeMap<String, Integer>();


        for(int i = 0;i < all_characters.length();i++) {
            map.put("" + all_characters.charAt(i), 1);
        }

        for (int i = 0;i < upload_file_content.length();i++){
            if (upload_file_content.charAt(i) != ' ') {
                if (!map.containsKey("" + upload_file_content.charAt(i))) {
                    if (all_lowercase.containsKey("" + upload_file_content.charAt(i))) {
                        missing_lowercase.put("" + upload_file_content.charAt(i), 1);
                    } else if (all_uppercase.containsKey("" + upload_file_content.charAt(i))) {
                        missing_uppercase.put("" + upload_file_content.charAt(i), 1);
                    } else if (all_numbers.containsKey("" + upload_file_content.charAt(i))) {
                        missing_numbers.put("" + upload_file_content.charAt(i), 1);
                    } else if (all_symbols.containsKey("" + upload_file_content.charAt(i))) {
                        missing_symbols.put("" + upload_file_content.charAt(i), 1);
                    }
                }
            }
        }

        Set s;
        Iterator itr;
        if (missing_uppercase.size() > 0) {
            missing_characters += "<br><b>Uppercase Letters : </b><br>";

            s = missing_uppercase.entrySet();
            itr = s.iterator();

            while (itr.hasNext()) {
                Map.Entry m = (Map.Entry)itr.next();
                String key = (String)m.getKey();
                missing_characters += key + " ";
            }
            missing_characters = missing_characters.substring(0, missing_characters.length() - 1);
            missing_characters += "<br>";
        }

        if (missing_lowercase.size() > 0) {
            missing_characters += "<br><b>Lowercase Letters : </b><br>";

            s = missing_lowercase.entrySet();
            itr = s.iterator();

            while (itr.hasNext()) {
                Map.Entry m = (Map.Entry)itr.next();
                String key = (String)m.getKey();
                missing_characters += key + " ";
            }
            missing_characters = missing_characters.substring(0, missing_characters.length() - 1);
            missing_characters += "<br>";
        }

        if (missing_numbers.size() > 0) {
            missing_characters += "<br><b>Numbers : </b><br>";

            s = missing_numbers.entrySet();
            itr = s.iterator();

            while (itr.hasNext()) {
                Map.Entry m = (Map.Entry)itr.next();
                String key = (String)m.getKey();
                missing_characters += key + " ";
            }
            missing_characters = missing_characters.substring(0, missing_characters.length() - 1);
            missing_characters += "<br>";
        }

        if (missing_symbols.size() > 0) {
            missing_characters += "<br><b>Symbols : </b><br>";

            s = missing_symbols.entrySet();
            itr = s.iterator();

            while (itr.hasNext()) {
                Map.Entry m = (Map.Entry)itr.next();
                String key = (String)m.getKey();
                missing_characters += key + " ";
            }
            missing_characters = missing_characters.substring(0, missing_characters.length() - 1);
            missing_characters += "<br>";
        }

        return missing_characters;
    }

}
