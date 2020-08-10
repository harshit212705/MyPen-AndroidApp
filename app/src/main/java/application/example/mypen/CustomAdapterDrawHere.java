package application.example.mypen;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class CustomAdapterDrawHere extends RecyclerView.Adapter<CustomAdapterDrawHere.MyViewHolder> {
    private ArrayList<String> characters;
    private ArrayList<String> unicode_values;
    private Context context;
    private ContextWrapper cw;
    private File template_draw_here, unsaved;
    final int THUMBSIZE = 64;
    private boolean is_saved = false;
    private String template_name;


    public CustomAdapterDrawHere(Context context, ArrayList characters, ArrayList unicode_values, ContextWrapper cw, boolean is_saved, String template_name) {
        this.context = context;
        this.characters = characters;
        this.unicode_values = unicode_values;
        this.cw = cw;
        this.is_saved = is_saved;
        this.template_name = template_name;

        template_draw_here = cw.getDir("template_draw_here", Context.MODE_PRIVATE);
        unsaved = new File(template_draw_here, "unsaved");
        unsaved.mkdir();


        if (is_saved) {

            File new_folder = new File(template_draw_here, "Template_" + this.template_name);
            if (new_folder.isDirectory()) {
                for (File child : new_folder.listFiles()) {
                    copyFile(child.toString(), unsaved.toString() + "/" + child.getName());
                }
            }

        }

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

//        holder.setIsRecyclable(false);

        holder.unicode_value = unicode_values.get(position);

        Resources resources = context.getResources();
        holder.image_drawable_id = resources.getIdentifier(holder.unicode_value, "drawable",
                context.getPackageName());

        File check_file = new File(unsaved.getAbsolutePath() + "/" + holder.unicode_value + ".png");
        Bitmap imageBitmap = BitmapFactory.decodeResource(resources, holder.image_drawable_id);
        if (check_file.exists()) {
            try {
                imageBitmap = BitmapFactory.decodeStream(new FileInputStream(check_file));
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
//        imageBitmap = ThumbnailUtils.extractThumbnail(imageBitmap, THUMBSIZE, THUMBSIZE);
        BitmapDrawable bdrawable = new BitmapDrawable(resources, imageBitmap);
        holder.character.setBackground(bdrawable);



    }

    @Override
    public void onViewAttachedToWindow(MyViewHolder holder) {
        Log.d("WINDOW", "CALLED");
        if (holder != null) {
            holder.setIsRecyclable(false);
        }
        super.onViewAttachedToWindow(holder);
    }

    @Override
    public void onViewDetachedFromWindow(MyViewHolder holder) {
        Log.d("NOT WINDOW", "CALLED");
        if (holder != null){
            holder.setIsRecyclable(true);
        }
        super.onViewDetachedFromWindow(holder);
    }


    @Override
    public int getItemCount() {
        return characters.size();
    }

//    @Override
//    public int getItemViewType(int position) {
//        return position;
//    }


    public class SingleTapConfirm extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapUp(MotionEvent event) {
            return true;
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnTouchListener {
        // init the item view's
        Button character;
        ImageView image;
        String unicode_value;
        int image_drawable_id;

        Dialog dialog;

        ImageView choosenImageView;
        Button savePicture;
        Button ButtonDismiss;
        ImageView ButtonClear;
        Button ButtonUndo;
        Button ButtonRedo;


        float downx = 0;
        float downy = 0;
        float upx = 0;
        float upy = 0;
        float x_diff = 0;
        float y_diff = 0;
        Boolean clear_called;

        Canvas canvas;
        Paint paint;
        Paint paint_black;
        Paint transparent;
        Matrix matrix;

        private Path mPath;
        private ArrayList<Path> paths = new ArrayList<Path>();
        private ArrayList<Path> undonePaths = new ArrayList<Path>();

        private Bitmap image_fixed;
        private Bitmap bitmap;

        private GestureDetector gestureDetector;
        private boolean pointer_set = false;
        private float pointer_x_coordinate, pointer_y_coordinate;


        @SuppressLint("ClickableViewAccessibility")
        public MyViewHolder(final View itemView) {
            super(itemView);
            // get the reference of item view's
            character = (Button) itemView.findViewById(R.id.button1);
            image = (ImageView) itemView.findViewById(R.id.imageView1);

            mPath = new Path();

            paint = new Paint();
            paint.setColor(Color.RED);
            paint.setAntiAlias(true);
            paint.setDither(true);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeJoin(Paint.Join.ROUND);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeWidth(20);

            transparent = new Paint();
            transparent.setColor(Color.TRANSPARENT);
            transparent.setAntiAlias(true);
            transparent.setDither(true);
            transparent.setStyle(Paint.Style.STROKE);
            transparent.setStrokeJoin(Paint.Join.ROUND);
            transparent.setStrokeCap(Paint.Cap.ROUND);
            transparent.setStrokeWidth(1);

            paint_black = new Paint();
            paint_black.setColor(Color.BLACK);
            paint_black.setAntiAlias(true);
            paint_black.setDither(true);
            paint_black.setStyle(Paint.Style.STROKE);
            paint_black.setStrokeJoin(Paint.Join.ROUND);
            paint_black.setStrokeCap(Paint.Cap.ROUND);
            paint_black.setStrokeWidth(1);


            dialog = new Dialog(context);
            dialog.setCanceledOnTouchOutside(false);
            dialog.setContentView(R.layout.activity_drawing_area);

            savePicture = (Button) dialog.findViewById(R.id.dialogButtonOk);
            choosenImageView = (ImageView) dialog.findViewById(R.id.selectedImage);
            ButtonDismiss = (Button) dialog.findViewById(R.id.dialogButtonDismiss);
            ButtonUndo = (Button) dialog.findViewById(R.id.dialogButtonUndo);
            ButtonRedo = (Button) dialog.findViewById(R.id.dialogButtonRedo);
            ButtonClear = (ImageView) dialog.findViewById(R.id.dialogImageClear);


            savePicture.setOnClickListener(this);
            ButtonDismiss.setOnClickListener(this);
            ButtonUndo.setOnClickListener(this);
            ButtonRedo.setOnClickListener(this);
            ButtonClear.setOnClickListener(this);

            gestureDetector = new GestureDetector(context, new SingleTapConfirm());
            choosenImageView.setOnTouchListener(this);



            character.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    File char_img = new File(unsaved.getAbsolutePath() + "/" + unicode_value + ".png");
                    if (char_img.exists()) {
                        try {
                            image_fixed = BitmapFactory.decodeStream(new FileInputStream(char_img));
                            image_fixed = Bitmap.createScaledBitmap(image_fixed, 650, 845, false);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else {
                        image_fixed = BitmapFactory.decodeResource(context.getResources(), image_drawable_id);
                        image_fixed = Bitmap.createScaledBitmap(image_fixed, 650, 845, false);
                    }
//                    image_fixed = ((BitmapDrawable)view.getBackground()).getBitmap();
//                    image_fixed = BitmapFactory.decodeResource(context.getResources(), image_drawable_id);
                    bitmap = image_fixed.copy(Bitmap.Config.ARGB_8888, true);

                    canvas = new Canvas(bitmap);
                    canvas. clipRect((float)0.0, (float)136.5, (float)650.0, (float)845.0);
                    choosenImageView.setImageBitmap(bitmap);

                    clear_called = false;
                    pointer_set = false;
//                    paths = new ArrayList<Path>();
//                    undonePaths = new ArrayList<Path>();
                    dialog.show();
                }
            });

        }

        @Override
        public void onClick(View view) {

            if (view == ButtonDismiss) {
                paths.clear();
                undonePaths.clear();
                dialog.dismiss();
            }
            else if (view == ButtonUndo) {
                onClickUndo();
            }
            else if (view == ButtonRedo) {
                onClickRedo();
            }
            else if (view == ButtonClear) {
                clear_called = true;
                pointer_set = false;
                paths.clear();
                undonePaths.clear();
                image_fixed = BitmapFactory.decodeResource(context.getResources(), image_drawable_id);
                image_fixed = Bitmap.createScaledBitmap(image_fixed, 650, 845, false);

                bitmap = image_fixed.copy(Bitmap.Config.ARGB_8888, true);


                canvas = new Canvas(bitmap);
                canvas. clipRect((float)0.0, (float)136.5, (float)650.0, (float)845.0);
                choosenImageView.setImageBitmap(bitmap);
            }
            else if (view == savePicture) {
                if (paths.size() > 0) {
                    File check_file = new File(unsaved.getAbsolutePath() + "/" + unicode_value + ".png");
                    if (!delete_file(check_file)) {
                        Log.d("IN SAVE PICTURE", "EXISTING FILE NOT DELETED");
                    }

                    bitmap = image_fixed.copy(Bitmap.Config.ARGB_8888, true);

                    canvas = new Canvas(bitmap);
                    canvas. clipRect((float)0.0, (float)136.5, (float)650.0, (float)845.0);

                    for (Path p : paths){
                        canvas.drawPath(p, paint);
                    }

                    File char_image = new File(unsaved, unicode_value + ".png");
                    FileOutputStream fos = null;

                    try {
                        fos = new FileOutputStream(char_image);
                        // Use the compress method on the BitMap object to write image to the OutputStream
                        bitmap = Bitmap.createScaledBitmap(bitmap, 200, 260, false);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                    finally {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    char_image = new File(unsaved.getAbsolutePath() + "/" + unicode_value + ".png");

                    try {
                        Bitmap imageBitmap = BitmapFactory.decodeStream(new FileInputStream(char_image));
//                        imageBitmap = ThumbnailUtils.extractThumbnail(imageBitmap, THUMBSIZE, THUMBSIZE);
                        BitmapDrawable bdrawable = new BitmapDrawable(context.getResources(), imageBitmap);
                        character.setBackground(bdrawable);
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else if (paths.size() == 0 && clear_called) {
                    Log.d("IT IS", "CALLED");
                    File check_file = new File(unsaved.getAbsolutePath() + "/" + unicode_value + ".png");
                    if (!delete_file(check_file)) {
                        Log.d("IN SAVE PICTURE", "FILE NOT DELETED");
                    }

                    Bitmap imageBitmap = BitmapFactory.decodeResource(context.getResources(), image_drawable_id);
//                    imageBitmap = ThumbnailUtils.extractThumbnail(imageBitmap, THUMBSIZE, THUMBSIZE);
                    BitmapDrawable bdrawable = new BitmapDrawable(context.getResources(), imageBitmap);
                    character.setBackground(bdrawable);
                }
                paths.clear();
                undonePaths.clear();
                dialog.dismiss();
            }
        }


        private boolean delete_file(File check_file) {

            if (check_file.exists()) {
                try {
                    boolean flag = check_file.delete();
                    return flag;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return false;
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {

            if (gestureDetector.onTouchEvent(event)) {
                // single tap
//                Bitmap marker = BitmapFactory.decodeResource(context.getResources(), R.drawable.draw_pointer);
//                marker = Bitmap.createScaledBitmap(marker, 40, 40, true);
                if (pointer_set) {
                    bitmap = image_fixed.copy(Bitmap.Config.ARGB_8888, true);

                    canvas = new Canvas(bitmap);
                    canvas. clipRect((float)0.0, (float)136.5, (float)650.0, (float)845.0);

                    for (Path p : paths){
                        canvas.drawPath(p, paint);
                    }

                }
                canvas.drawCircle(event.getX(), event.getY(), (float) 20.0, paint_black);
                choosenImageView.setImageBitmap(bitmap);
                pointer_x_coordinate = event.getX();
                pointer_y_coordinate = event.getY();
                pointer_set = true;

                return true;

            } else if (pointer_set) {
                // your code for move and drag

                int action = event.getAction();

                switch (action) {

                    case MotionEvent.ACTION_DOWN:

//                        Log.d("COORDINATES", "" + event.getRawX() + " " + event.getX() + " " + event.getRawY() + " " + event.getY());
                        downx = event.getX();
                        downy = event.getY();
                        undonePaths.clear();
                        mPath.reset();
                        mPath.moveTo(pointer_x_coordinate, pointer_y_coordinate);
                        x_diff = downx - pointer_x_coordinate;
                        y_diff = downy - pointer_y_coordinate;
                        break;

                    case MotionEvent.ACTION_MOVE:

//                        Log.d("COORDINATES", "" + event.getRawX() + " " + event.getX() + " " + event.getRawY() + " " + event.getY());
                        upx = event.getX();
                        upy = event.getY();
                        canvas.drawLine(downx - x_diff, downy - y_diff, upx - x_diff, upy - y_diff, paint);
                        mPath.quadTo(downx - x_diff, downy - y_diff, upx - x_diff, upy - y_diff);
                        choosenImageView.invalidate();
                        downx = upx;
                        downy = upy;

                        break;

                    case MotionEvent.ACTION_UP:

//                        Log.d("COORDINATES", "" + event.getRawX() + " " + event.getX() + " " + event.getRawY() + " " + event.getY());
                        upx = event.getX();
                        upy = event.getY();

                        mPath.lineTo(upx - x_diff, upy - y_diff);
                        canvas.drawLine(downx - x_diff, downy - y_diff, upx - x_diff, upy - y_diff, paint);
                        paths.add(mPath);

                        mPath = new Path();
                        choosenImageView.invalidate();
                        break;

                    case MotionEvent.ACTION_CANCEL:
                        break;

                    default:
                        break;
                }
                return true;
            }

            return true;
        }


        private void onClickUndo () {

            if (paths.size() > 0) {

                undonePaths.add(paths.remove(paths.size()-1));
                choosenImageView.invalidate();

                bitmap = image_fixed.copy(Bitmap.Config.ARGB_8888, true);

                canvas = new Canvas(bitmap);
                canvas. clipRect((float)0.0, (float)136.5, (float)650.0, (float)845.0);
                if (pointer_set) {
                    canvas.drawCircle(pointer_x_coordinate, pointer_y_coordinate, (float)20.0, paint_black);
                }
                choosenImageView.setImageBitmap(bitmap);

                for (Path p : paths){
                    canvas.drawPath(p, paint);
                }

            }

        }


        private void onClickRedo (){

            if (undonePaths.size() > 0) {

                Path addPath = undonePaths.remove(undonePaths.size()-1);
                paths.add(addPath);
                canvas.drawPath(addPath, paint);
                choosenImageView.invalidate();
            }

        }


//        final float[] getPointerCoords(MotionEvent e) {
//
//            final int index = e.getActionIndex();
//            final float[] coords = new float[] { e.getX(index), e.getY(index) };
//            Matrix matrix = new Matrix();
//            choosenImageView.getImageMatrix().invert(matrix);
//            matrix.postTranslate(choosenImageView.getScrollX(), choosenImageView.getScrollY());
//            matrix.mapPoints(coords);
//            return coords;
//        }

    }
}
