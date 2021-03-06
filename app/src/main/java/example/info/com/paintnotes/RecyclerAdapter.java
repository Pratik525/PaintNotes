package example.info.com.paintnotes;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.ByteArrayOutputStream;
import java.util.List;

/**
 * Created by pratik.sinha on 6/1/2017.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder> {

        private Context mContext;
        private List<Bitmap> notesList;

        public class MyViewHolder extends RecyclerView.ViewHolder {
            public ImageView notesIcon;

            public MyViewHolder(View view) {
                super(view);
                notesIcon = (ImageView) view.findViewById(R.id.iv_notes);
            }
        }


        public RecyclerAdapter(Context mContext, List<Bitmap> notesList) {
            this.mContext = mContext;
            this.notesList = notesList;
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.card_view, parent, false);

            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, final int position) {

            Glide.with(holder.notesIcon.getContext()).load(bitmapToByte(notesList.get(position))).asBitmap().into(holder.notesIcon);
            //final Bitmap notes = notesList.get(position);
            //holder.notesIcon.setImageBitmap(notes);
            holder.notesIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DialogMakeCanvas dialogMakeCanvas = new DialogMakeCanvas(mContext, (MainActivity)mContext, notesList.get(position), position);
                    dialogMakeCanvas.show();
                }
            });
        }
    private byte[] bitmapToByte(Bitmap bitmap){ ByteArrayOutputStream stream = new ByteArrayOutputStream(); bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream); byte[] byteArray = stream.toByteArray(); return byteArray; }

        @Override
        public int getItemCount() {
            return notesList.size();
        }
}
