package application.example.mypen;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space;
    private int numOfColumns;

    public SpacesItemDecoration(int space, int numOfColumns) {
        this.space = space;
        this.numOfColumns = numOfColumns;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view,
                               RecyclerView parent, RecyclerView.State state) {
        outRect.left = space;
        outRect.right = space;
        outRect.bottom = space;

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildLayoutPosition(view) < numOfColumns) {
            outRect.top = space;
        } else {
            outRect.top = 0;
        }
    }
}