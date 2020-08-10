package application.example.mypen;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class CustomAdapterMyCustomFont extends RecyclerView.Adapter<CustomAdapterMyCustomFont.MyViewHolder> {

    private Context context;
    private ContextWrapper cw;
    private File my_fonts;
    private float dpHeight,dpWidth;
    int layouHeight, layoutWidth;

    private myDbAdapter helper;
    private ArrayList<String> custom_font_names = new ArrayList<String>();
//    private ArrayList<String> font_string = new ArrayList<String>();
    ArrayList unicode_values = new ArrayList<>(Arrays.asList("x41", "x61", "x42", "x62", "x43", "x63", "x44", "x64", "x45", "x65", "x46", "x66", "x47", "x67", "x48", "x68", "x49", "x69", "x4a", "x6a", "x4b", "x6b", "x4c", "x6c", "x4d", "x6d", "x4e", "x6e", "x4f", "x6f", "x50", "x70", "x51", "x71", "x52", "x72", "x53", "x73", "x54", "x74", "x55", "x75", "x56", "x76", "x57", "x77", "x58", "x78", "x59", "x79", "x5a", "x7a", "x30", "x31", "x32", "x33", "x34", "x35", "x36", "x37", "x38", "x39", "x22", "x25", "x26", "x27", "x28", "x29", "x2c", "x2d", "x2e", "x2f", "x3a", "x3d", "x3f", "x21", "x23", "x24", "x2a", "x2b", "x3b", "x3c", "x3e", "x40", "x5b", "x5c", "x5d", "x5e", "x5f", "x60", "x7b", "x7c", "x7d", "x7e"));



    public CustomAdapterMyCustomFont(Context context, ContextWrapper cw) {
        this.context = context;
        this.cw = cw;

        helper = new myDbAdapter(context);
        my_fonts = new File(context.getFilesDir(), "my_fonts");

        DisplayMetrics displayMetrics = context.getApplicationContext().getResources().getDisplayMetrics();
        dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        dpWidth -= 40;
        layoutWidth = (int) (dpHeight/2);
        layouHeight = (int) (layoutWidth*1.3);

        if (my_fonts.isDirectory()) {
            for (File child : my_fonts.listFiles()) {
                custom_font_names.add(child.getName());
            }
        }

    }


    @Override
    public CustomAdapterMyCustomFont.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_custom_font_grid_layout, parent, false);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(layoutWidth + 20, layouHeight + 60);
        v.setLayoutParams(layoutParams);

        // set the view's size, margins, paddings and layout parameters
        CustomAdapterMyCustomFont.MyViewHolder vh = new CustomAdapterMyCustomFont.MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        Typeface typeFace = Typeface.createFromFile(new File(my_fonts.toString() + "/" + custom_font_names.get(position)));
        holder.custom_font_preview.setTypeface(typeFace);

        String font_string = "";
        String[] data = new String[6];
        data = helper.getData(custom_font_names.get(position));

        String lowercase = data[2];
        String uppercase = data[3];
        String numbers = data[4];
        String symbols = data[5];

        // Selecting which characters to show as the font thumbnail
        String u = "",l = "",n = "",s = "";
        if (uppercase.length() > 0) {
            u = uppercase.substring(0, 1);
        }
        if (lowercase.length() > 0) {
            l = lowercase.substring(0, 1);
        }
        if (numbers.length() > 0) {
            n = numbers.substring(0, 1);
        }
        if (symbols.length() > 0) {
            s = symbols.substring(0, 1);
        }

        font_string = u + l + n + s;
        if (font_string.length() >= 2) {
            font_string = font_string.substring(0,2);
        }
        else {
            u = "";
            l = "";
            n = "";
            s = "";
            if (uppercase.length() > 1) {
                u = uppercase.substring(1, 2);
            }
            if (lowercase.length() > 1) {
                l = lowercase.substring(1, 2);
            }
            if (numbers.length() > 1) {
                n = numbers.substring(1, 2);
            }
            if (symbols.length() > 1) {
                s = symbols.substring(1, 2);
            }
            font_string += u + l + n + s;
            if (font_string.length() > 2) {
                font_string = font_string.substring(0, 2);
            }
        }


//        for (int i = 0; i < unicode_values.size() && font_string.length() < 2; i++) {
//            String charToTest = unicode_values.get(i).toString().substring(1);
//            charToTest = "u00" + charToTest;
//            charToTest = "\\" + charToTest;
//            Paint paint = new Paint();
//            paint.setTypeface(typeFace);
//
//            if (android.os.Build.VERSION.SDK_INT >= 23) {
//                boolean hasGlyph = paint.hasGlyph(charToTest);
//                if (hasGlyph) {
//                    char ch = charToTest.toCharArray()[0];
//                    font_string += ch;
//                }
//            }
//        }

        holder.custom_font_preview.setText(font_string);
        holder.custom_font_name.setText(custom_font_names.get(position).substring(0, custom_font_names.get(position).length() - 4));

    }

    @Override
    public int getItemCount() {
        return custom_font_names.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's

        TextView custom_font_preview;
        TextView custom_font_name;


        public MyViewHolder(final View itemView) {
            super(itemView);
            // get the reference of item view's
            custom_font_preview = (TextView) itemView.findViewById(R.id.custom_font_preview);
            custom_font_name = (TextView) itemView.findViewById(R.id.custom_font_name);


            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                }

            });
        }

    }

}
