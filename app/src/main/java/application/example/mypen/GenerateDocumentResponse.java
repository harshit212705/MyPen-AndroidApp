package application.example.mypen;

import android.content.Context;
import android.os.Environment;
import android.util.Base64;

import com.google.gson.annotations.SerializedName;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class GenerateDocumentResponse {

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @SerializedName("document_name")
    private String name;

    public String getContent64() {
        return content64;
    }

    public void setContent64(String content64) {
        this.content64 = content64;
    }

    @SerializedName("content64")
    private String content64;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    private Context context;

    public GenerateDocumentResponse(Context context){
        this.context=context;
    }

    private byte[] content64AsByteArray(){
        return Base64.decode(content64, Base64.DEFAULT);
    }

    public void save() {

        try {

            File my_documents = new File(Environment.getExternalStorageDirectory() + "/MyPen", "Documents");
            File document_file = new File(my_documents.toString() + '/' + this.name);

            OutputStream out = null;
            out = new FileOutputStream(document_file);
            out.write(this.content64AsByteArray());
            out.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
