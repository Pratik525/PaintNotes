package example.info.com.paintnotes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by pratik.sinha on 11/23/2016.
 */

public class PaintView extends View implements View.OnTouchListener, View.OnLongClickListener {
    Context mContext;
    private static final String TAG = "PaintView";
    private List<Point> points = new ArrayList<Point>();
    private Point lastPoint = null;
    Paint paint = new Paint();
    public int previousColor = 0;
    private PaintReleaseCallBack paintReleaseCallBack;

    public PaintView(Context context, PaintReleaseCallBack paintReleaseCallBack) {
        super(context);
        mContext = context;
        this.paintReleaseCallBack = paintReleaseCallBack;
        setFocusable(true);
        setFocusableInTouchMode(true);
        this.setOnTouchListener(this);
        this.setLongClickable(true);
        this.setOnLongClickListener(this);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3);
    }

    @Override
    public void onDraw(Canvas canvas) {
        paint.setStyle(Paint.Style.STROKE);
        Path path1 = new Path();
        for (Point point : points) {
            if (lastPoint != null) {
                path1.quadTo(lastPoint.x, lastPoint.y, point.x, point.y);
            } else {
                path1.moveTo(point.x, point.y);
            }
            lastPoint = point;
        }
        lastPoint = null;
        canvas.drawPath(path1, paint);

    }

    public boolean onTouch(View view, MotionEvent event) {
        Point point = new Point();
        point.x = event.getX();
        point.y = event.getY()-dpToPx(200);
        points.add(point);
        switch (event.getAction()) {
            case MotionEvent.ACTION_UP:
                lastPoint = null;
                paintReleaseCallBack.setCanvasBackground();
                points.clear();
                invalidate();
                break;
        }
        paintReleaseCallBack.setPenPosition((int)event.getX(), (int)event.getY());
        invalidate();
        Log.d(TAG, "point: " + point);
        return true;
    }

    @Override
    public boolean onLongClick(View view) {
        Toast.makeText(mContext, "Long Pressed", Toast.LENGTH_LONG).show();
        return false;
    }

    /**
     * dp to px
     *
     * @param dp
     * @return
     */
    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }
}

class Point {
    float x, y;

    @Override
    public String toString() {
        return x + ", " + y;
    }
}


