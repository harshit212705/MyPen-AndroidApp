package application.example.mypen;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;


//  In this file font denotes template

public class CustomAdapterMyFont  extends RecyclerView.Adapter<CustomAdapterMyFont.MyViewHolder> {

    private Context context;
    private ContextWrapper cw;
    private File template_draw_here;
    private ArrayList<String> font_names = new ArrayList<String>();
    private ArrayList<String> font_thumbnail_image = new ArrayList<String>();
    ArrayList unicode_values = new ArrayList<>(Arrays.asList("x41", "x61", "x42", "x62", "x43", "x63", "x44", "x64", "x45", "x65", "x46", "x66", "x47", "x67", "x48", "x68", "x49", "x69", "x4a", "x6a", "x4b", "x6b", "x4c", "x6c", "x4d", "x6d", "x4e", "x6e", "x4f", "x6f", "x50", "x70", "x51", "x71", "x52", "x72", "x53", "x73", "x54", "x74", "x55", "x75", "x56", "x76", "x57", "x77", "x58", "x78", "x59", "x79", "x5a", "x7a", "x30", "x31", "x32", "x33", "x34", "x35", "x36", "x37", "x38", "x39", "x22", "x25", "x26", "x27", "x28", "x29", "x2c", "x2d", "x2e", "x2f", "x3a", "x3d", "x3f", "x21", "x23", "x24", "x2a", "x2b", "x3b", "x3c", "x3e", "x40", "x5b", "x5c", "x5d", "x5e", "x5f", "x60", "x7b", "x7c", "x7d", "x7e"));
    private float dpHeight,dpWidth;
    int layouHeight, layoutWidth;


    public CustomAdapterMyFont(Context context, ContextWrapper cw) {
        this.context = context;
        this.cw = cw;

        DisplayMetrics displayMetrics = context.getApplicationContext().getResources().getDisplayMetrics();
        dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        dpWidth -= 60;
        layoutWidth = (int) (dpHeight/2);
        layouHeight = (int) (layoutWidth*1.3);

        template_draw_here = cw.getDir("template_draw_here", Context.MODE_PRIVATE);

        if (template_draw_here.isDirectory()) {
            File file;
            for (File child : template_draw_here.listFiles()) {
                if (!child.getName().equals("unsaved")) {
                    font_names.add(child.getName().substring(9));
                    boolean flag = false;
                    for (int i = 0; i < unicode_values.size(); i++) {
                        file = new File(template_draw_here.toString() + "/" + child.getName() + "/" + unicode_values.get(i).toString() + ".png");
                        if (file.exists()) {
                            font_thumbnail_image.add(unicode_values.get(i).toString());
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        font_thumbnail_image.add("");
                    }
                }
            }
        }


    }


    @Override
    public CustomAdapterMyFont.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_font_grid_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(layoutWidth + 20, layouHeight + 100);
        v.setLayoutParams(layoutParams);

        CustomAdapterMyFont.MyViewHolder vh = new CustomAdapterMyFont.MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(layoutWidth + 20, layouHeight);
        holder.font_image.setLayoutParams(layoutParams);

//        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(layoutWidth, 150);
//        holder.font_name.setLayoutParams(layoutParams1);

        holder.font_name.setText(font_names.get(position));
//        Log.d("INDEX", "" + position + " " + font_thumbnail_image.get(position));
        if (!font_thumbnail_image.get(position).equals("")) {

            File file = new File(template_draw_here.toString() + "/Template_" + font_names.get(position) + "/" + font_thumbnail_image.get(position) + ".png");
            Bitmap imageBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.empty_font);
            if (file.exists()) {
                try {
                    imageBitmap = BitmapFactory.decodeStream(new FileInputStream(file));
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
            BitmapDrawable bdrawable = new BitmapDrawable(context.getResources(), imageBitmap);
            holder.font_image.setBackground(bdrawable);
        }

    }

    @Override
    public int getItemCount() {
        return font_names.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's

        ImageView font_image;
        TextView font_name;


        public MyViewHolder(final View itemView) {
            super(itemView);
            // get the reference of item view's
            font_image = (ImageView) itemView.findViewById(R.id.font_img);
            font_name = (TextView) itemView.findViewById(R.id.font_name);


            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), DrawHere.class);
                    intent.putExtra("is_saved", true);
                    intent.putExtra("template_name", font_name.getText().toString());
                    view.getContext().startActivity(intent);
                }

            });

        }

    }

}
