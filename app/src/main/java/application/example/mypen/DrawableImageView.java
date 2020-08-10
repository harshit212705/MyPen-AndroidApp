package application.example.mypen;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatImageView;


public class DrawableImageView extends AppCompatImageView implements View.OnTouchListener
{
    float downx = 0;
    float downy = 0;
    float upx = 0;
    float upy = 0;

    Canvas canvas;
    Paint paint;
    Matrix matrix;

    public DrawableImageView(Context context)
    {
        super(context);
        initialize_canvas();
        setOnTouchListener(this);
    }

    public DrawableImageView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initialize_canvas();
        setOnTouchListener(this);
    }

    public DrawableImageView(Context context, AttributeSet attrs,
                             int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initialize_canvas();
        setOnTouchListener(this);
    }

    public void initialize_canvas() {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(20);
        Log.d("IMAGE ID", "" + getId());
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.x35);
        bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);

        canvas = new Canvas(bitmap);
//        canvas.drawLine(0, 0, 100, 100, paint);
        setImageBitmap(bitmap);
    }

    public void setNewImage(Bitmap alteredBitmap, Bitmap bmp)
    {
        canvas = new Canvas(alteredBitmap);
        paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStrokeWidth(5);
        matrix = new Matrix();
        canvas.drawBitmap(bmp, matrix, paint);

        setImageBitmap(alteredBitmap);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event)
    {
        int action = event.getAction();
        Log.d("CHECK", "5 " + event.getAction());
        switch (action)
        {
            case MotionEvent.ACTION_DOWN:
                downx = getPointerCoords(event)[0];//event.getX();
                downy = getPointerCoords(event)[1];//event.getY();
                Log.d("CHECK", "DOWN");
                break;
            case MotionEvent.ACTION_MOVE:
                upx = getPointerCoords(event)[0];//event.getX();
                upy = getPointerCoords(event)[1];//event.getY();
                canvas.drawLine(downx, downy, upx, upy, paint);
                invalidate();
                downx = upx;
                downy = upy;
                Log.d("CHECK", "MOVE");
                break;
            case MotionEvent.ACTION_UP:
                upx = getPointerCoords(event)[0];//event.getX();
                upy = getPointerCoords(event)[1];//event.getY();
                canvas.drawLine(downx, downy, upx, upy, paint);
                invalidate();
                Log.d("CHECK", "UP");
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
            default:
                break;
        }
        return true;
    }

    final float[] getPointerCoords(MotionEvent e)
    {
        final int index = e.getActionIndex();
        final float[] coords = new float[] { e.getX(index), e.getY(index) };
        Matrix matrix = new Matrix();
        getImageMatrix().invert(matrix);
        matrix.postTranslate(getScrollX(), getScrollY());
        matrix.mapPoints(coords);
        return coords;
    }
}