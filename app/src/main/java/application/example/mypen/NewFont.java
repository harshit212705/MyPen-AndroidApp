package application.example.mypen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class NewFont extends AppCompatActivity {

    Intent intent;

    ListView simpleList;
    int icons[] = {R.drawable.ic_file_download_blue_24dp, R.drawable.ic_create_blue_24dp};
    String options[] = {"Download Template", "Draw Here"};
    String optionsDescription[] = {"Download template for characters you want in your font", "Try drawing the characters at your mobile screen"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_font);


        intent = getIntent();
        simpleList = (ListView) findViewById(R.id.simpleListView);
        CustomAdapterNewFont customAdapter = new CustomAdapterNewFont(getApplicationContext(), icons, options, optionsDescription);
        simpleList.setAdapter(customAdapter);

        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

//                Log.d("NEW FONT ONCLICK", "" + position + " " + id);
                Intent intent;
                switch (position) {
                    case 0: intent = new Intent(view.getContext(), DownloadTemplate.class);
                            view.getContext().startActivity(intent);
                            break;

                    case 1: intent = new Intent(view.getContext(), DrawHere.class);
                            intent.putExtra("is_saved", false);
                            intent.putExtra("template_name", "");
                            view.getContext().startActivity(intent);
                            break;

                    default: break;
                }
            }
        });

    }
}
