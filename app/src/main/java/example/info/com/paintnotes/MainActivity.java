package example.info.com.paintnotes;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SaveCanvasInterface{

    private LinearLayout linearLytUpload, linearLytDraw;
    private FloatingActionButton mFabMenu, mFabSelectImage, mFabDrawImage;
    private RecyclerView mRecyclerView;
    private DialogMakeCanvas mDialogMakeCanvas = null;
    private ScrollView scrollView = null;
    private ImageView imageView = null;
    private PaintView paintView;
    private List<Bitmap> bitmapList;
    private RecyclerAdapter recyclerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        linearLytUpload = (LinearLayout) findViewById(R.id.lin_lyt_upload_click);
        linearLytDraw =  (LinearLayout) findViewById(R.id.lin_lyt_draw_note);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mFabMenu = (FloatingActionButton) findViewById(R.id.fab_menu);
        mFabSelectImage = (FloatingActionButton) findViewById(R.id.fab_menu_option_upload);
        mFabDrawImage = (FloatingActionButton) findViewById(R.id.fab_menu_draw_note);
        //
        bitmapList = new ArrayList<Bitmap>();
        recyclerAdapter = new RecyclerAdapter(this, bitmapList);

        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(recyclerAdapter);

        setClickListeners();
    }

    private void setClickListeners() {
        mFabMenu.setOnClickListener(this);
        mFabSelectImage.setOnClickListener(this);
        mFabDrawImage.setOnClickListener(this);
    }

    public void backgroundCanvas(){
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            paintView.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(paintView.getDrawingCache());
            paintView.setDrawingCacheEnabled(false);
            Drawable d = new BitmapDrawable(getResources(), bitmap);
            paintView.setBackground(d);
            bitmap=Bitmap.createScaledBitmap(bitmap,bitmap.getWidth()/2,bitmap.getHeight()/2,false);
            d = new BitmapDrawable(getResources(), bitmap);
            imageView.setImageDrawable(d);
        }*/
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fab_menu :
                if(linearLytUpload.getVisibility() == View.VISIBLE){
                    linearLytUpload.setVisibility(View.INVISIBLE);
                    linearLytDraw.setVisibility(View.INVISIBLE);
                }
                else{
                    linearLytUpload.setVisibility(View.VISIBLE);
                    linearLytDraw.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.fab_menu_draw_note :
                mDialogMakeCanvas = new DialogMakeCanvas(this, this);
                mDialogMakeCanvas.show();
                break;
            case R.id.fab_menu_option_upload :
                break;
        }
    }

    @Override
    public void onSaveClicked(Bitmap bitmap) {
        bitmapList.add(bitmap);
        recyclerAdapter.notifyDataSetChanged();
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

    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
}
