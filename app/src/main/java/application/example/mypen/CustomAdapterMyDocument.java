package application.example.mypen;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
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

public class CustomAdapterMyDocument  extends RecyclerView.Adapter<CustomAdapterMyDocument.MyViewHolder> {

    private Context context;
    private ContextWrapper cw;
    private ArrayList<String> document_names = new ArrayList<String>();
    private float dpHeight,dpWidth;
    int layouHeight, layoutWidth;
    File my_documents;

    private boolean is_write_permission_granted = false;


    public CustomAdapterMyDocument(Context context, ContextWrapper cw, boolean is_write_permission_granted) {
        this.context = context;
        this.cw = cw;
        this.is_write_permission_granted = is_write_permission_granted;

        DisplayMetrics displayMetrics = context.getApplicationContext().getResources().getDisplayMetrics();
        dpHeight = displayMetrics.heightPixels / displayMetrics.density;
        dpWidth = displayMetrics.widthPixels / displayMetrics.density;

        dpWidth -= 40;
        layoutWidth = (int) (dpHeight/2);
        layouHeight = (int) (layoutWidth*1.3);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        if (this.is_write_permission_granted) {
            make_documents_folder();
            my_documents = new File(Environment.getExternalStorageDirectory() + "/MyPen", "Documents");
            if (my_documents.isDirectory()) {
                for (File child: my_documents.listFiles()) {
                    document_names.add(child.getName().substring(0, child.getName().length() - 4));
                }
            }
        }
        Log.d("DOCUMENTS SIZE", "" + document_names.size());
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

    @Override
    public CustomAdapterMyDocument.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // infalte the item Layout
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.my_document_grid_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(layoutWidth + 20, layouHeight + 100);
        v.setLayoutParams(layoutParams);

        CustomAdapterMyDocument.MyViewHolder vh = new CustomAdapterMyDocument.MyViewHolder(v); // pass the view to View Holder
        return vh;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(layoutWidth + 20, layouHeight);
        holder.document_image.setLayoutParams(layoutParams);

//        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(layoutWidth, 150);
//        holder.font_name.setLayoutParams(layoutParams1);

        holder.document_name.setText(document_names.get(position));
//        Log.d("INDEX", "" + position + " " + font_thumbnail_image.get(position));


        try {
            File pdfFile = new File(my_documents, document_names.get(position) + ".pdf");
            if (android.os.Build.VERSION.SDK_INT >= 21) {
                PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY));
                Bitmap bitmap;
                final int pageCount = renderer.getPageCount();
                PdfRenderer.Page page = renderer.openPage(0);

                bitmap = Bitmap.createBitmap(layoutWidth + 20, layouHeight, Bitmap.Config.ARGB_8888);

                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                BitmapDrawable bdrawable = new BitmapDrawable(context.getResources(), bitmap);
                holder.document_image.setBackground(bdrawable);
                // close the renderer
                renderer.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    @Override
    public int getItemCount() {
        return document_names.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // init the item view's

        ImageView document_image;
        TextView document_name;


        public MyViewHolder(final View itemView) {
            super(itemView);
            // get the reference of item view's
            document_image = (ImageView) itemView.findViewById(R.id.document_img);
            document_name = (TextView) itemView.findViewById(R.id.document_name);


            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    File file = new File(my_documents, document_name.getText().toString() + ".pdf");
                    Intent target = new Intent(Intent.ACTION_VIEW);
                    target.setDataAndType(Uri.fromFile(file), "application/pdf");
                    target.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    target.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    Intent intent = Intent.createChooser(target, "Open File");

                    try {
                        view.getContext().startActivity(intent);
                    } catch (ActivityNotFoundException e) {
                        // Instruct the user to install a PDF reader here, or something
                    }
                }

            });

        }

    }

}
