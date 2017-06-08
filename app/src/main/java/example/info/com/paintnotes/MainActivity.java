package example.info.com.paintnotes;

import android.app.Dialog;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SaveCanvasInterface {

    private LinearLayout linearLytUpload, linearLytDraw;
    private FloatingActionButton mFabMenu, mFabSelectImage, mFabDrawImage;
    private RecyclerView mRecyclerView, mRecyclerViewBack;
    private DialogMakeCanvas mDialogMakeCanvas = null;
    private ScrollView scrollView = null;
    private ImageView imageView = null;
    private PaintView paintView;
    private List<Bitmap> bitmapList;
    private RecyclerAdapter recyclerAdapter;
    private GridLayoutManager mGridLayoutManager1, mGridLayoutManager2, mGridLayoutManager3, mGridLayoutManager4, mGridLayoutManager5, mGridLayoutManager6;
    private RecyclerView.LayoutManager mCurrentLayoutManager, mCurrentLayoutManager2;
    private ScaleGestureDetector mScaleGestureDetector;
    private ImageView reclyclerImage;
    private float scaleFactor = 1;
    private RelativeLayout lyt_rlt_recycler;
    private  boolean inScale=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        linearLytUpload = (LinearLayout) findViewById(R.id.lin_lyt_upload_click);
        linearLytDraw = (LinearLayout) findViewById(R.id.lin_lyt_draw_note);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerViewBack = (RecyclerView) findViewById(R.id.recycler_view_back);
        reclyclerImage = (ImageView) findViewById(R.id.recyclerImage);
        mFabMenu = (FloatingActionButton) findViewById(R.id.fab_menu);
        mFabSelectImage = (FloatingActionButton) findViewById(R.id.fab_menu_option_upload);
        mFabDrawImage = (FloatingActionButton) findViewById(R.id.fab_menu_draw_note);
        lyt_rlt_recycler = (RelativeLayout) findViewById(R.id.lyt_rlt_recycler);
        //
        bitmapList = new ArrayList<Bitmap>();


        recyclerAdapter = new RecyclerAdapter(this, bitmapList);

        // RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        // mRecyclerView.setLayoutManager(mLayoutManager);
        //mRecyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        // mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        //mRecyclerView.setAdapter(recyclerAdapter);


        if (mRecyclerView != null) {

            //initialize layout managers
            mGridLayoutManager1 = new GridLayoutManager(this, 1);
            mGridLayoutManager2 = new GridLayoutManager(this, 2);
            mGridLayoutManager3 = new GridLayoutManager(this, 3);
            mGridLayoutManager4 = new GridLayoutManager(this, 4);
            mGridLayoutManager5 = new GridLayoutManager(this, 5);
            mGridLayoutManager6 = new GridLayoutManager(this, 6);


            //set layout manager
            mCurrentLayoutManager = mGridLayoutManager2;
            mCurrentLayoutManager2 = mGridLayoutManager3;
            mRecyclerView.setLayoutManager(mGridLayoutManager3);
            mRecyclerViewBack.setLayoutManager(mGridLayoutManager2);

            //set adapter
            mRecyclerView.setAdapter(recyclerAdapter);
            mRecyclerViewBack.setAdapter(recyclerAdapter);

            //set scale gesture detector
            mScaleGestureDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.SimpleOnScaleGestureListener() {
                @Override
                public boolean onScale(ScaleGestureDetector detector) {
                    if (detector.getCurrentSpan() > 200 && detector.getTimeDelta() > 200) {
                        if (detector.getCurrentSpan() - detector.getPreviousSpan() < -1) {
                            /////////////// Zoom out
                            float alpha = 1 - (detector.getTimeDelta()) / 1000F;
                            Log.d("alpha: ", alpha + "");
                            Log.d("getCurrentSpan: ", detector.getCurrentSpan() + "");
                            Log.d("getPreviousSpan: ", detector.getPreviousSpan() + "");
                            Log.d("getTimeDelta: ", detector.getTimeDelta() + "");
                            reclyclerImage.setAlpha(alpha);
                            if (mGridLayoutManager3.getSpanCount() <5 && inScale) {
                                mGridLayoutManager3.setSpanCount(mGridLayoutManager3.getSpanCount()+1);
                                mGridLayoutManager3.requestLayout();
                                inScale=false;
                            }
                        } else if (detector.getCurrentSpan() - detector.getPreviousSpan() > 1) {
                            /////////////// Zoom in
                            float alpha = 1 - (detector.getTimeDelta()) / 1000F;
                            Log.d("alpha: ", alpha + "");
                            Log.d("getCurrentSpan: ", detector.getCurrentSpan() + "");
                            Log.d("getPreviousSpan: ", detector.getPreviousSpan() + "");
                            Log.d("getTimeDelta: ", detector.getTimeDelta() + "");
                            reclyclerImage.setAlpha(alpha);
                            //animateRecyclerLayoutChange(2);
                            if (mGridLayoutManager3.getSpanCount() >1 && inScale) {
                                mGridLayoutManager3.setSpanCount(mGridLayoutManager3.getSpanCount()-1);
                                mGridLayoutManager3.requestLayout();
                                inScale=false;
                            }
                        }
                    }
                    return false;
                }

                @Override
                public boolean onScaleBegin(ScaleGestureDetector detector) {
                    reclyclerImage.setVisibility(View.VISIBLE);
                    inScale=true;
                    //mRecyclerViewBack.setVisibility(View.VISIBLE);
                    //reclyclerImage.setBackgroundColor(Color.RED);
                    try {
                        mRecyclerView.setDrawingCacheEnabled(true);
                        if (!mRecyclerView.getDrawingCache().isRecycled()) {
                            Bitmap b = Bitmap.createBitmap(mRecyclerView.getDrawingCache());
                            //Drawable d = new BitmapDrawable(getResources(), b);
                            reclyclerImage.setImageBitmap(b);
                        } else {
                            reclyclerImage.setBackgroundColor(Color.TRANSPARENT);
                        }
                        mRecyclerView.setDrawingCacheEnabled(false);
                    } catch (Error e) {
                        e.printStackTrace();
                    }

                    return super.onScaleBegin(detector);
                }

                @Override
                public void onScaleEnd(ScaleGestureDetector detector) {
                    reclyclerImage.setVisibility(View.GONE);
                    mRecyclerView.setAlpha(1);
                    inScale=false;
//                    if(detector.getCurrentSpan()>detector.getPreviousSpan() ){
//                        if(mCurrentLayoutManager!=mGridLayoutManager2)
//                        mRecyclerView.setLayoutManager(mGridLayoutManager2);
//                        if(mCurrentLayoutManager2!=mGridLayoutManager3)
//                        mRecyclerViewBack.setLayoutManager(mGridLayoutManager3);
//                        mCurrentLayoutManager = mGridLayoutManager2;
//                        mCurrentLayoutManager2 = mGridLayoutManager3;
//                    }else if(detector.getCurrentSpan()<detector.getPreviousSpan()){
//                        if(mCurrentLayoutManager!=mGridLayoutManager3)
//                        mRecyclerView.setLayoutManager(mGridLayoutManager3);
//
//                        if(mCurrentLayoutManager2!=mGridLayoutManager2)
//                        mRecyclerViewBack.setLayoutManager(mGridLayoutManager2);
//
//                        mCurrentLayoutManager = mGridLayoutManager3;
//                        mCurrentLayoutManager2 = mGridLayoutManager2;
//                    }

//                    if(detector.getScaleFactor()<1.5){
//
//                        mRecyclerView.setLayoutManager(mGridLayoutManager3);
//                        mRecyclerViewBack.setLayoutManager(mGridLayoutManager2);
//
//
//                    }else{
//                        mRecyclerView.setLayoutManager(mGridLayoutManager2);
//                        mRecyclerViewBack.setLayoutManager(mGridLayoutManager3);
//
//                    }
//                    scaleFactor=1;
//                    mRecyclerView.setVisibility(View.VISIBLE);
//                    mRecyclerViewBack.setAlpha(0);
//                    mRecyclerView.setAlpha(1);
//
//                    mRecyclerView.setScaleX(1);
//                    mRecyclerView.setScaleY(1);
//                    mRecyclerViewBack.setScaleX(1);
//                    mRecyclerViewBack.setScaleY(1);
                    super.onScaleEnd(detector);
                }
            });

//            //set touch listener on recycler view
//            reclyclerImage.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    mScaleGestureDetector.onTouchEvent(event);
//                    return true;
//                }
//            });
            //set touch listener on recycler view
            mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    mScaleGestureDetector.onTouchEvent(event);
                    return false;
                }
            });
//            mRecyclerViewBack.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    mScaleGestureDetector.onTouchEvent(event);
//                    return false;
//                }
//            });

        }
        setClickListeners();
    }

    private void setClickListeners() {
        mFabMenu.setOnClickListener(this);
        mFabSelectImage.setOnClickListener(this);
        mFabDrawImage.setOnClickListener(this);
    }

    public void backgroundCanvas() {
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
        switch (id) {
            case R.id.fab_menu:
                if (linearLytUpload.getVisibility() == View.VISIBLE) {
                    linearLytUpload.setVisibility(View.INVISIBLE);
                    linearLytDraw.setVisibility(View.INVISIBLE);
                } else {
                    linearLytUpload.setVisibility(View.VISIBLE);
                    linearLytDraw.setVisibility(View.VISIBLE);
                }
                break;
            case R.id.fab_menu_draw_note:
                mDialogMakeCanvas = new DialogMakeCanvas(this, this);
                mDialogMakeCanvas.show();
                break;
            case R.id.fab_menu_option_upload:
                showDialogSelector(this);
                break;
        }
    }

    public void showDialogSelector(Context mContext) {
        final Dialog mDialog = new Dialog(mContext);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.pic_selector_dialog);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        Button mButtonCamera = (Button) mDialog.findViewById(R.id.cameraButton);
        Button mButtonGallery = (Button) mDialog.findViewById(R.id.galleryButton);
        mButtonCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, 0);
                mDialog.dismiss();
            }
        });

        mButtonGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 0);
                mDialog.dismiss();
            }
        });
        mDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap;
        String filePath;
        if (resultCode == RESULT_OK && requestCode == 0) {
            Uri selectedImage = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                filePath = getPathFromURI(selectedImage);
                try {
                    ExifInterface exif = new ExifInterface(filePath);
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                    Log.d("CAMERA_ORIENTATION", "" + orientation);
                    Matrix matrix = new Matrix();
                    if (orientation == 6) {
                        matrix.postRotate(90);
                    } else if (orientation == 3) {
                        matrix.postRotate(180);
                    } else if (orientation == 8) {
                        matrix.postRotate(270);
                    }
                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
                } catch (Exception e) {
                }
                if (bitmap != null) {
                    onSaveClicked(bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private String getPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(this, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

    @Override
    public void onSaveClicked(Bitmap bitmap) {
        bitmapList.add(bitmap);
        recyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onSaveClicked(Bitmap bitmap, int index) {
        bitmapList.remove(index);
        bitmapList.add(index, bitmap);
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


    private void animateRecyclerLayoutChange(final int layoutSpanCount) {
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new DecelerateInterpolator());
        fadeOut.setDuration(200);
        fadeOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mGridLayoutManager3.setSpanCount(layoutSpanCount);
                mGridLayoutManager3.requestLayout();
                Animation fadeIn = new AlphaAnimation(0, 1);
                fadeIn.setInterpolator(new AccelerateInterpolator());
                fadeIn.setDuration(200);
                mRecyclerView.startAnimation(fadeIn);
            }
        });
        mRecyclerView.startAnimation(fadeOut);
    }
}
