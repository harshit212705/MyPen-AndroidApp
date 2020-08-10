package application.example.mypen;

import android.content.Context;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CustomAdapterDownloadTemplate extends RecyclerView.Adapter<CustomAdapterDownloadTemplate.MyViewHolder> {

    private ArrayList<String> characters;
    private Context context;

    public CustomAdapterDownloadTemplate(Context context, ArrayList characters) {
        this.context = context;
        this.characters = characters;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.character_block_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        MyViewHolder vh = new MyViewHolder(v); // pass the view to View Holder
        return vh;
    }


    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        // set the data in items
        holder.character.setText(characters.get(position));
        // implement setOnClickListener event on item view.

    }


    @Override
    public int getItemCount() {
        return characters.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // init the item view's
        Button character;
        ImageView image;

        public MyViewHolder(final View itemView) {
            super(itemView);
            // get the reference of item view's
            character = (Button) itemView.findViewById(R.id.button1);
            image = (ImageView) itemView.findViewById(R.id.imageView1);


            character.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
//                    Log.d("ONCLICK", "CLICKED");
                    if (image.getVisibility() == View.VISIBLE) {
//                        Log.d("ONCLICK", "VISIBLE");
                        image.setVisibility(View.GONE);
                    }
                    else {
//                        Log.d("ONCLICK", "GONE");
                        image.setVisibility(View.VISIBLE);
                    }
                }
            });

        }

        @Override
        public void onClick(View view) {

        }


    }
}