package example.info.com.paintnotes;

import android.graphics.Bitmap;

/**
 * Created by pratik.sinha on 6/1/2017.
 */

public interface SaveCanvasInterface {
    void onSaveClicked(Bitmap bitmap);
    void onSaveClicked(Bitmap bitmap, int index);
}
