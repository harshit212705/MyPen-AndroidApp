package application.example.mypen;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class GenerateFontResponse {

    @SerializedName("name")
    private String name;

    @SerializedName("content64")
    private String content64;

    @SerializedName("lowercase")
    private String lowercase;

    @SerializedName("uppercase")
    private String uppercase;

    @SerializedName("numbers")
    private String numbers;

    @SerializedName("symbols")
    private String symbols;


    public String getLowercase() {
        return lowercase;
    }

    public void setLowercase(String lowercase) {
        this.lowercase = lowercase;
    }

    public String getUppercase() {
        return uppercase;
    }

    public void setUppercase(String uppercase) {
        this.uppercase = uppercase;
    }

    public String getNumbers() {
        return numbers;
    }

    public void setNumbers(String numbers) {
        this.numbers = numbers;
    }

    public String getSymbols() {
        return symbols;
    }

    public void setSymbols(String symbols) {
        this.symbols = symbols;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private Context context;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent64() {
        return content64;
    }

    public void setContent64(String content64) {
        this.content64 = content64;
    }

    public GenerateFontResponse(Context context){
        this.context=context;
    }

    private byte[] content64AsByteArray(){
        return Base64.decode(content64, Base64.DEFAULT);
    }

    public void save() {

        try {

            File my_fonts = new File(context.getFilesDir(), "my_fonts");
            File ttf_file = new File(my_fonts.toString() + '/' + this.name);

            OutputStream out = null;
            out = new FileOutputStream(ttf_file);
            out.write(this.content64AsByteArray());
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

