package application.example.mypen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import java.util.ArrayList;
import java.util.Arrays;

public class DownloadTemplate extends AppCompatActivity {


    ArrayList characters = new ArrayList<>(Arrays.asList("\"", "%", "&", "'", "(", ")", ",","-", ".", "/", "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", ":", "=", "?" ,"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z", "!", "#", "$", "*", "+", ";", "<", ">", "@", "[", "\\", "]", "^", "_", "`", "{", "|", "}", "~"));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_template);


        // get the reference of RecyclerView
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        // set a GridLayoutManager with default vertical orientation and 2 number of columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(),4);
        recyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
        //  call the constructor of CustomAdapter to send the reference and data to Adapter
        CustomAdapterDownloadTemplate customAdapter = new CustomAdapterDownloadTemplate(DownloadTemplate.this, characters);
        recyclerView.setAdapter(customAdapter); // set the Adapter to RecyclerView


    }



}
