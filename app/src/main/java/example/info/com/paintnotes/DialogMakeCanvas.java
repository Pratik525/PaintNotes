package example.info.com.paintnotes;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresPermission;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by pratik.sinha on 6/1/2017.
 */

public class DialogMakeCanvas extends Dialog implements View.OnClickListener,DialogInterface.OnDismissListener, PaintReleaseCallBack{

    private Context mContext;
    private SaveCanvasInterface mSaveCanvasInterface = null;
    private RelativeLayout mRelativeCanvas;
    private PaintView mPaintView;
    private TextView mTvSave;
    private ImageView mPenImageView;
    //private ImageView mPenImage;

    public DialogMakeCanvas(Context context, SaveCanvasInterface saveCanvasInterface) {
        super(context);
        mContext = context;
        mSaveCanvasInterface = saveCanvasInterface;
        initDialogProperties();
    }

    private void initDialogProperties() {
        // This is the layout XML file that describes your Dialog layout
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.setContentView(R.layout.dialog_main_canvas);
        View view = this.getWindow().getDecorView();
        //
        WindowManager.LayoutParams wmlp = this.getWindow().getAttributes();
        Window window = getWindow();
        this.getWindow().setDimAmount(0.5f);
        //
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(this.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        this.getWindow().setAttributes(lp);
        //
        this.setCancelable(true);
        this.setOnDismissListener(this);
        //
        initView(view);
    }

    private void initView(View view) {
        mRelativeCanvas = (RelativeLayout) findViewById(R.id.rel_lyt_canvas);
        mTvSave = (TextView) findViewById(R.id.tv_save);
       // mPenImageView = (ImageView) findViewById(R.id.pen);
        mPaintView = new PaintView(mContext, this);

        //
        mPaintView.setLayoutParams(new ViewGroup.LayoutParams( ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        mRelativeCanvas.addView(mPaintView);
        mTvSave.setOnClickListener(this);
        //mPenImageView.setVisibility(View.GONE);
        mPenImageView = new ImageView(mContext);
        mPenImageView.setBackgroundColor(Color.RED);
        mPenImageView.setRotation(-30);

        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,100);
        mPenImageView.setLayoutParams(layoutParams);

        mRelativeCanvas.addView(mPenImageView);
    }

    @Override
    public void onDismiss(DialogInterface dialog) {

    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.tv_save :
                mPaintView.setDrawingCacheEnabled(true);
                Bitmap bitmap = Bitmap.createBitmap(mPaintView.getDrawingCache());
                mPaintView.setDrawingCacheEnabled(false);
                mSaveCanvasInterface.onSaveClicked(bitmap);
                dismiss();
                break;
        }
    }

    @Override
    public void setCanvasBackground() {
        mPaintView.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(mPaintView.getDrawingCache());
        mPaintView.setDrawingCacheEnabled(false);
        Drawable d = new BitmapDrawable(mContext.getResources(), bitmap);
        mPaintView.setBackground(d);
    }

    @Override
    public void setPenPosition(int x, int y) {
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) mPenImageView.getLayoutParams();
        layoutParams.leftMargin = x -25; layoutParams.topMargin = y - 75;
        mPenImageView.setLayoutParams(layoutParams);
    }


}
